package com.monitoring.model;

import java.util.HashMap;
import java.util.Map;

public class Node {
  private final String fIpAddress;
  private final String fHost;
  private long fUptime;
  private Memory fMemory;
  private Load fLoad;
  private Swap fSwap;
  private NetStat fNetStat;
  private final Map<String, NetIf> fNetIfs = new HashMap<String, NetIf>();
  private final Map<String, Cpu> fCpus = new HashMap<String, Cpu>();
  private final Map<String, Disk> fDisks = new HashMap<String, Disk>();

  public Node(String aIpAddress, String aHost) {
    if (aIpAddress == null) {
      throw new NullPointerException("IP Address must not null");
    }

    if (aHost == null) {
      throw new NullPointerException("Host must not null");
    }

    fIpAddress = aIpAddress;
    fHost = aHost;
  }

  public String getIpAddress() {
    return fIpAddress;
  }

  public String getHost() {
    return fHost;
  }

  public Memory getMemory() {
    return fMemory;
  }

  public void setMemory(Memory aMemory) {
    fMemory = aMemory;
  }

  public Load getLoad() {
    return fLoad;
  }

  public void setLoad(Load aLoad) {
    fLoad = aLoad;
  }

  public Swap getSwap() {
    return fSwap;
  }

  public void setSwap(Swap aSwap) {
    fSwap = aSwap;
  }

  public NetStat getNetStat() {
    return fNetStat;
  }

  public void setNetStat(NetStat aNetStat) {
    fNetStat = aNetStat;
  }

  public long getUptime() {
    return fUptime;
  }

  public void setUptime(long aUptime) {
    fUptime = aUptime;
  }

  public void addNetIf(NetIf aNetIf) {
    fNetIfs.put(aNetIf.getName(), aNetIf);
  }

  public void removeNetIf(NetIf aNetIf) {
    fNetIfs.remove(aNetIf.getName(), aNetIf);
  }

  public int getNetIfSize() {
    return fNetIfs.size();
  }

  public NetIf getNetIfByName(String aName) {
    return fNetIfs.get(aName);
  }

  public Iterable<String> getNetIfs() {
    return fNetIfs.keySet();
  }

  public void addCpu(Cpu aCpu) {
    fCpus.put(aCpu.getName(), aCpu);
  }

  public void removeCpu(Cpu aCpu) {
    fCpus.remove(aCpu.getName(), aCpu);
  }

  public int getCpuSize() {
    return fCpus.size();
  }

  public Cpu getCpuByName(String aName) {
    return fCpus.get(aName);
  }

  public Iterable<String> getCpus() {
    return fCpus.keySet();
  }

  public void addDisk(Disk aDisk) {
    fDisks.put(aDisk.getPath(), aDisk);
  }

  public void removeDisk(Disk aDisk) {
    fDisks.remove(aDisk.getPath(), aDisk);
  }

  public int getDiskSize() {
    return fDisks.size();
  }

  public Iterable<String> getDiskPaths() {
    return fDisks.keySet();
  }

  public Disk getDiskByPath(String aPath) {
    return fDisks.get(aPath);
  }

  @Override
  public boolean equals(Object aO) {
    if (this == aO) return true;
    if (aO == null || getClass() != aO.getClass()) return false;

    Node node = (Node) aO;

    return fIpAddress.equals(node.fIpAddress);
  }

  @Override
  public int hashCode() {
    return fIpAddress.hashCode();
  }
}
