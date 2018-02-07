package com.monitoring.util;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public final class QueryUtil {
  private static List<String> fDiskMeasurementList = Arrays.asList(
                                                          Constants.DISK_TOTAL,
                                                          Constants.DISK_USED,
                                                          Constants.DISK_FREE);

  private static List<String> fMemoryMeasurementList = Arrays.asList(
                                                          Constants.MEM_TOTAL,
                                                          Constants.MEM_USED,
                                                          Constants.MEM_FREE);

  private static List<String> fLoadMeasurementList = Arrays.asList(
                                                          Constants.SYSTEM_LOAD_1,
                                                          Constants.SYSTEM_LOAD_5,
                                                          Constants.SYSTEM_LOAD_15);

  private static List<String> fCpuMeasurementList = Arrays.asList(
                                                          Constants.CPU_USAGE_SYSTEM,
                                                          Constants.CPU_USAGE_USER,
                                                          Constants.CPU_TIME_SYSTEM,
                                                          Constants.CPU_TIME_USER);

  private static List<String> fSwapMeasurementList = Arrays.asList(
                                                          Constants.SWAP_TOTAL,
                                                          Constants.SWAP_USED,
                                                          Constants.SWAP_FREE);

  private static List<String> fNetifMeasurementList = Arrays.asList(
                                                          Constants.NET_BYTES_RECV,
                                                          Constants.NET_BYTES_SENT,
                                                          Constants.NET_DROP_IN,
                                                          Constants.NET_DROP_OUT,
                                                          Constants.NET_ERR_IN,
                                                          Constants.NET_ERR_OUT,
                                                          Constants.NET_PACKETS_RECV,
                                                          Constants.NET_PACKETS_SENT);

  public static String getDiskMeasurements() {
    return StringUtils.join(fDiskMeasurementList, ",");
  }

  public static String getMemoryMeasurements() {
    return StringUtils.join(fMemoryMeasurementList, ",");
  }

  public static String getLoadMeasurements() {
    return StringUtils.join(fLoadMeasurementList, ",");
  }

  public static String getCpuMeasurements() {
    return StringUtils.join(fCpuMeasurementList, ",");
  }

  public static String getSwapMeasurements() {
    return StringUtils.join(fSwapMeasurementList, ",");
  }

  public static String getNetifMeasurements() {
    return StringUtils.join(fNetifMeasurementList, ",");
  }

  public static String generate(String aIP, String aMeasurement) {
    if (aIP == null) {
      return "";
    }

    String aQuery = "";
    if (Constants.DISK.equalsIgnoreCase(aMeasurement)) {
      aQuery = "SELECT * FROM " + getDiskMeasurements() +
          " WHERE ip='" + aIP + "' AND (fstype = 'ext4'  OR fstype =  'fuseblk')"
          + " GROUP BY fstype, path"
          + " ORDER BY time DESC"
          + " LIMIT 1";
    } else if (Constants.MEMORY.equalsIgnoreCase(aMeasurement)) {
      aQuery = "SELECT * FROM " + getMemoryMeasurements() +
          " WHERE ip='" + aIP + "' ORDER BY time DESC"
          + " LIMIT 1";
    } else if (Constants.SYSTEM_LOAD.equalsIgnoreCase(aMeasurement)) {
      aQuery = "SELECT * FROM " + getLoadMeasurements() +
          " WHERE ip='" + aIP + "' ORDER BY time DESC"
          + " LIMIT 1";
    } else if (Constants.CPU.equalsIgnoreCase(aMeasurement)) {
      aQuery = "SELECT * FROM " + getCpuMeasurements() +
          " WHERE ip='" + aIP
          + "' GROUP BY cpu"
          + " ORDER BY time DESC"
          + " LIMIT 1";
    } else if (Constants.SWAP.equalsIgnoreCase(aMeasurement)) {
      aQuery = "SELECT * FROM " + getSwapMeasurements() +
          " WHERE ip='" + aIP + "' ORDER BY time DESC"
          + " LIMIT 1";
    } else if (Constants.NETIF.equalsIgnoreCase(aMeasurement)) {
      aQuery = "SELECT * FROM " + getNetifMeasurements() +
          " WHERE ip='" + aIP
          + "' GROUP BY interface"
          + " ORDER BY time DESC"
          + " LIMIT 1";
    }
    return aQuery;
  }

  private QueryUtil() {}
}
