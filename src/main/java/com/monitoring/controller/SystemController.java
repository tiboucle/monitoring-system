package com.monitoring.controller;

import com.monitoring.model.Cpu;
import com.monitoring.model.Disk;
import com.monitoring.model.Load;
import com.monitoring.model.MainSystem;
import com.monitoring.model.Memory;
import com.monitoring.model.NetIf;
import com.monitoring.model.Node;
import com.monitoring.model.Swap;
import com.monitoring.util.Constants;
import com.monitoring.view.DockingComponent;
import com.monitoring.view.DockingView;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemController {
  private static final Logger LOG = LoggerFactory.getLogger(SystemController.class);
  private static final int UPDATE_PERIOD_MIN = 3000;
  private static final int UPDATE_PERIOD_MAX = 120000;
  private final List<PropertyChangeListener> fListeners;
  private final ExecutorService fTimeExecutor = Executors.newSingleThreadExecutor();
  private final ExecutorService fDiskExecutor = Executors.newSingleThreadExecutor();
  private final ExecutorService fRamExecutor = Executors.newSingleThreadExecutor();
  private final ExecutorService fCpuExecutor = Executors.newSingleThreadExecutor();
  private final ExecutorService fSwapExecutor = Executors.newSingleThreadExecutor();
  private final ExecutorService fLoadExecutor = Executors.newSingleThreadExecutor();
  private final ExecutorService fNetifExecutor = Executors.newSingleThreadExecutor();
  private final Runnable fTimeUpdater;
  private final Runnable fDiskUpdater;
  private final Runnable fRamUpdater;
  private final Runnable fCpuUpdater;
  private final Runnable fSwapUpdater;
  private final Runnable fLoadUpdater;
  private final Runnable fNetifUpdater;
  private final MainSystem fSystem;
  private final DockingView fView;
  private final InfluxDB fInfluxDB;
  private final String fApiUrl;
  private int fUpdatePeriod;
  private volatile boolean fStopped;
  private int time;

  public SystemController(DockingView aView, MainSystem aSystem, String aApiUrl, int aUpdatePeriod) {
    if (aSystem == null) {
      throw new NullPointerException();
    }
    fView = aView;
    fSystem = aSystem;
    fListeners = new ArrayList<PropertyChangeListener>();
    fTimeUpdater = new TimeUpdater();
    fDiskUpdater = new DiskUpdater();
    fRamUpdater = new MemoryUpdater();
    fCpuUpdater = new CpuUpdater();
    fSwapUpdater = new SwapUpdater();
    fLoadUpdater = new LoadUpdater();
    fNetifUpdater = new NetifUpdater();
    fStopped = true;
    if (fUpdatePeriod < UPDATE_PERIOD_MIN) {
      fUpdatePeriod = UPDATE_PERIOD_MIN;
    } else if (fUpdatePeriod > UPDATE_PERIOD_MAX) {
      fUpdatePeriod = UPDATE_PERIOD_MAX;
    } else {
      fUpdatePeriod = aUpdatePeriod;
    }

    if (aApiUrl == null || aApiUrl.isEmpty()) {
      throw new IllegalArgumentException("API URL must not null nor empty");
    }

    fApiUrl = aApiUrl;
    fInfluxDB = InfluxDBFactory.connect(fApiUrl, "root", "root");
    LOG.info("URL: " + aApiUrl);
  }

  public void addPropertyChangeListener(PropertyChangeListener aListener) {
    if (aListener != null) {
      fListeners.add(aListener);
    }
  }

  public void removePropertyChangeListener(PropertyChangeListener aListener) {
    if (aListener != null) {
      fListeners.remove(aListener);
    }
  }

  private void fireEvent(PropertyChangeEvent aEvent) {
    if (aEvent.getOldValue().equals(aEvent.getNewValue()) &&
        (aEvent.getPropertyName().equalsIgnoreCase(Constants.DISK_FREE) ||
            aEvent.getPropertyName().equalsIgnoreCase(Constants.DISK_USED))) {
      return;
    }

    Node node = null;
    Date date = null;

    if (aEvent.getSource() instanceof Node) {
      node = (Node) aEvent.getSource();
    } else if (aEvent.getSource() instanceof Date) {
      date = (Date) aEvent.getSource();
    }

    for (PropertyChangeListener listener : fListeners) {
      DockingComponent dockingComponent = (DockingComponent) listener;
      if (node != null && aEvent.getSource() instanceof Node) {
        if (dockingComponent.getIpAddress().equalsIgnoreCase(node.getIpAddress())) {
          listener.propertyChange(aEvent);
        }
      }

      if (date != null && aEvent.getSource() instanceof Date) {
        listener.propertyChange(aEvent);
      }
    }
  }

  public void start() throws Exception {
    if (!fStopped) {
      return;
    }

    fStopped = false;
    fTimeExecutor.execute(fTimeUpdater);
    fDiskExecutor.execute(fDiskUpdater);
    fRamExecutor.execute(fRamUpdater);
    fCpuExecutor.execute(fCpuUpdater);
    fSwapExecutor.execute(fSwapUpdater);
    fLoadExecutor.execute(fLoadUpdater);
    fNetifExecutor.execute(fNetifUpdater);

    LOG.info("Controller started");
  }

  public void stop() throws Exception {
    if (fStopped) {
      return;
    }

    fStopped = true;
    fTimeExecutor.shutdownNow();
    fDiskExecutor.shutdownNow();
    fRamExecutor.shutdownNow();
    fCpuExecutor.shutdownNow();
    fSwapExecutor.shutdownNow();
    fLoadExecutor.shutdownNow();
    fNetifExecutor.shutdownNow();

    LOG.info("Controller stopped");
  }

  public boolean isStarted() {
    return !fStopped;
  }

  private final class DiskUpdater implements Runnable, Updater {
    public void run() {
      if (fStopped) {
        return;
      }

      while (!fStopped) {
        try {
          Thread.sleep(fUpdatePeriod);
          Util.updateSystem(fView, fInfluxDB, fSystem, Constants.DISK, this);
        } catch (InterruptedException ignored) {

        }
      }
    }

    public void update(Node aNode, String aName, Number value, Map<String, String> aTags) {
      String path = aTags.get("path");
      String type = aTags.get("fstype");
      Disk disk = aNode.getDiskByPath(path);
      if (disk == null) {
        disk = new Disk();
        disk.setPath(path);
        disk.setType(type);
        aNode.addDisk(disk);
      }

      if (value != null) {
        if (Constants.DISK_TOTAL.equalsIgnoreCase(aName)) {
          long oldValue = disk.getTotal();
          long newValue = value.longValue();
          disk.setTotal(newValue);
          fireEvent(new PropertyChangeEvent(aNode, Constants.DISK_TOTAL, oldValue, newValue));
        } else if (Constants.DISK_USED.equalsIgnoreCase(aName)) {
          long oldValue = disk.getUsed();
          long newValue = value.longValue();
          disk.setUsed(newValue);
          fireEvent(new PropertyChangeEvent(aNode, Constants.DISK_USED, oldValue, newValue));
        } else if (Constants.DISK_FREE.equalsIgnoreCase(aName)) {
          long oldValue = disk.getFree();
          long newValue = value.longValue();
          disk.setFree(newValue);
          fireEvent(new PropertyChangeEvent(aNode, Constants.DISK_FREE, oldValue, newValue));
        }
      }
    }
  }

  private class MemoryUpdater implements Runnable, Updater {
    public void run() {
      if (fStopped) {
        return;
      }

      while (!fStopped) {
        try {
          Thread.sleep(fUpdatePeriod);
          Util.updateSystem(fView, fInfluxDB, fSystem, Constants.MEMORY, this);
        } catch (InterruptedException ignored) {

        }
      }
    }

    public void update(Node aNode, String aName, Number value, Map<String, String> aTags) {
      Memory memory = aNode.getMemory();
      if (memory == null) {
        memory = new Memory();
        aNode.setMemory(memory);
      }

      if (value != null) {
        if (Constants.MEM_TOTAL.equalsIgnoreCase(aName)) {
          long oldValue = memory.getTotal();
          long newValue = value.longValue();
          memory.setTotal(newValue);
          fireEvent(new PropertyChangeEvent(aNode, Constants.MEM_TOTAL, oldValue, newValue));
        } else if (Constants.MEM_USED.equalsIgnoreCase(aName)) {
          long oldValue = memory.getUsed();
          long newValue = value.longValue();
          memory.setUsed(newValue);
          fireEvent(new PropertyChangeEvent(aNode, Constants.MEM_USED, oldValue, newValue));
        } else if (Constants.MEM_FREE.equalsIgnoreCase(aName)) {
          long oldValue = memory.getFree();
          long newValue = value.longValue();
          memory.setFree(newValue);
          fireEvent(new PropertyChangeEvent(aNode, Constants.MEM_FREE, oldValue, newValue));
        }
      }
    }
  }

  private final class CpuUpdater implements Runnable, Updater {

    public void run() {
      if (fStopped) {
        return;
      }

      while (!fStopped) {
        try {
          Thread.sleep(fUpdatePeriod);
          Util.updateSystem(fView, fInfluxDB, fSystem, Constants.CPU, this);
        } catch (InterruptedException ignored) {

        }
      }
    }

    public void update(Node aNode, String aName, Number aValue, Map<String, String> aTags) {
      String name = aTags.get("cpu");
      Cpu cpu = aNode.getCpuByName(name);
      if (cpu == null) {
        cpu = new Cpu();
        cpu.setName(name);
        aNode.addCpu(cpu);
      }

      if (aValue != null) {
        if (Constants.CPU_USAGE_SYSTEM.equalsIgnoreCase(aName)) {
          double oldValue = cpu.getUsageSystem();
          double newValue = aValue.doubleValue();
          cpu.setUsageSystem(newValue);
          fireEvent(new PropertyChangeEvent(aNode, Constants.CPU_USAGE_SYSTEM, oldValue, newValue));
        } else if (Constants.CPU_TIME_SYSTEM.equalsIgnoreCase(aName)) {
          double oldValue = cpu.getTimeSystem();
          double newValue = aValue.doubleValue();
          cpu.setTimeSystem(newValue);
          fireEvent(new PropertyChangeEvent(aNode, Constants.CPU_TIME_SYSTEM, oldValue, newValue));
        } else if (Constants.CPU_USAGE_USER.equalsIgnoreCase(aName)) {
          double oldValue = cpu.getUsageUser();
          double newValue = aValue.doubleValue();
          cpu.setUsageUser(newValue);
          fireEvent(new PropertyChangeEvent(aNode, Constants.CPU_USAGE_USER, oldValue, newValue));
        } else if (Constants.CPU_TIME_USER.equalsIgnoreCase(aName)) {
          double oldValue = cpu.getTimeUser();
          double newValue = aValue.doubleValue();
          cpu.setTimeUser(newValue);
          fireEvent(new PropertyChangeEvent(aNode, Constants.CPU_TIME_USER, oldValue, newValue));
        }
      }
    }
  }

  private class SwapUpdater implements Runnable, Updater {
    public void run() {
      if (fStopped) {
        return;
      }

      while (!fStopped) {
        try {
          Thread.sleep(fUpdatePeriod);
          Util.updateSystem(fView, fInfluxDB, fSystem, Constants.SWAP, this);
        } catch (InterruptedException ignored) {

        }
      }
    }

    public void update(Node aNode, String aName, Number value, Map<String, String> aTags) {
      Swap swap = aNode.getSwap();
      if (swap == null) {
        swap = new Swap();
        aNode.setSwap(swap);
      }

      if (value != null) {
        if (Constants.SWAP_TOTAL.equalsIgnoreCase(aName)) {
          long oldValue = swap.getTotal();
          long newValue = value.longValue();
          swap.setTotal(newValue);
          fireEvent(new PropertyChangeEvent(aNode, Constants.SWAP_TOTAL, oldValue, newValue));
        } else if (Constants.SWAP_USED.equalsIgnoreCase(aName)) {
          long oldValue = swap.getUsed();
          long newValue = value.longValue();
          swap.setUsed(newValue);
          fireEvent(new PropertyChangeEvent(aNode, Constants.SWAP_USED, oldValue, newValue));
        } else if (Constants.SWAP_FREE.equalsIgnoreCase(aName)) {
          long oldValue = swap.getFree();
          long newValue = value.longValue();
          swap.setFree(newValue);
          fireEvent(new PropertyChangeEvent(aNode, Constants.SWAP_FREE, oldValue, newValue));
        }
      }
    }
  }


  private class LoadUpdater implements Runnable, Updater {
    public void run() {
      if (fStopped) {
        return;
      }

      while (!fStopped) {
        try {
          Thread.sleep(fUpdatePeriod);
          Util.updateSystem(fView, fInfluxDB, fSystem, Constants.SYSTEM_LOAD, this);
        } catch (InterruptedException ignored) {

        }
      }
    }

    public void update(Node aNode, String aName, Number value, Map<String, String> aTags) {
      Load load = aNode.getLoad();
      if (load == null) {
        load = new Load();
        aNode.setLoad(load);
      }

      if (value != null) {
        if (Constants.SYSTEM_LOAD_1.equalsIgnoreCase(aName)) {
          double oldValue = load.getLoad1();
          double newValue = value.doubleValue();
          load.setLoad1(newValue);
          fireEvent(new PropertyChangeEvent(aNode, Constants.SYSTEM_LOAD_1, oldValue, newValue));
        } else if (Constants.SYSTEM_LOAD_5.equalsIgnoreCase(aName)) {
          double oldValue = load.getLoad5();
          double newValue = value.doubleValue();
          load.setLoad5(newValue);
          fireEvent(new PropertyChangeEvent(aNode, Constants.SYSTEM_LOAD_5, oldValue, newValue));
        } else if (Constants.SYSTEM_LOAD_15.equalsIgnoreCase(aName)) {
          double oldValue = load.getLoad15();
          double newValue = value.doubleValue();
          load.setLoad15(newValue);
          fireEvent(new PropertyChangeEvent(aNode, Constants.SYSTEM_LOAD_15, oldValue, newValue));
        }
      }
    }
  }

  private class NetifUpdater implements Runnable, Updater {
    public void run() {
      if (fStopped) {
        return;
      }

      while (!fStopped) {
        try {
          Thread.sleep(fUpdatePeriod);
          Util.updateSystem(fView, fInfluxDB, fSystem, Constants.NETIF, this);
        } catch (InterruptedException ignored) {

        }
      }
    }

    public void update(Node aNode, String aName, Number aValue, Map<String, String> aTags) {
      String name = aTags.get("interface");
      NetIf netif = aNode.getNetIfByName(name);
      if (netif == null) {
        netif = new NetIf();
        netif.setName(name);
        aNode.addNetIf(netif);
      }

      if (aValue != null) {
        if (Constants.NET_BYTES_RECV.equalsIgnoreCase(aName)) {
          long oldValue = netif.getBytesRecv();
          long newValue = aValue.longValue();
          netif.setBytesRecv(newValue);
          fireEvent(new PropertyChangeEvent(aNode, Constants.NET_BYTES_RECV, oldValue, newValue));
        } else if (Constants.NET_BYTES_SENT.equalsIgnoreCase(aName)) {
          long oldValue = netif.getBytesSent();
          long newValue = aValue.longValue();
          netif.setBytesSent(newValue);
          fireEvent(new PropertyChangeEvent(aNode, Constants.NET_BYTES_SENT, oldValue, newValue));
        } else if (Constants.NET_DROP_IN.equalsIgnoreCase(aName)) {
          long oldValue = netif.getDropIn();
          long newValue = aValue.longValue();
          netif.setDropIn(newValue);
          //fireEvent(new PropertyChangeEvent(aNode, Constants.NET_DROP_IN, oldValue, newValue));
        } else if (Constants.NET_DROP_OUT.equalsIgnoreCase(aName)) {
          long oldValue = netif.getDropOut();
          long newValue = aValue.longValue();
          netif.setDropOut(newValue);
          //fireEvent(new PropertyChangeEvent(aNode, Constants.NET_DROP_OUT, oldValue, newValue));
        } else if (Constants.NET_ERR_IN.equalsIgnoreCase(aName)) {
          long oldValue = netif.getErrIn();
          long newValue = aValue.longValue();
          netif.setErrIn(newValue);
          //fireEvent(new PropertyChangeEvent(aNode, Constants.NET_ERR_IN, oldValue, newValue));
        } else if (Constants.NET_ERR_OUT.equalsIgnoreCase(aName)) {
          long oldValue = netif.getErrOut();
          long newValue = aValue.longValue();
          netif.setErrOut(newValue);
          //fireEvent(new PropertyChangeEvent(aNode, Constants.NET_ERR_OUT, oldValue, newValue));
        } else if (Constants.NET_PACKETS_RECV.equalsIgnoreCase(aName)) {
          long oldValue = netif.getPacketsRecv();
          long newValue = aValue.longValue();
          netif.setPacketsRecv(newValue);
          fireEvent(new PropertyChangeEvent(aNode, Constants.NET_PACKETS_RECV, oldValue, newValue));
        } else if (Constants.NET_PACKETS_SENT.equalsIgnoreCase(aName)) {
          long oldValue = netif.getPacketsSent();
          long newValue = aValue.longValue();
          netif.setPacketsSent(newValue);
          fireEvent(new PropertyChangeEvent(aNode, Constants.NET_PACKETS_SENT, oldValue, newValue));
        }
      }
    }
  }

  private class TimeUpdater implements Runnable {
    public void run() {
      if (fStopped) {
        return;
      }
      Calendar date = Calendar.getInstance();
      while (!fStopped) {
        try {
          Thread.sleep(1000);
          //int oldValue = time;
          int oldValue = ++time;
          Date newValue = new Date();
          fireEvent(new PropertyChangeEvent(date.getTime(), "time", oldValue, newValue));
        } catch (InterruptedException ignored) {

        }
      }
    }
  }
}