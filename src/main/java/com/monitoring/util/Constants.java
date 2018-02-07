package com.monitoring.util;

public final class Constants {
  //public static final String ASSET_API_URL = "http://192.168.10.26";
  //public static final String DB_API_URL = "http://192.168.10.24:8086/";
  public static final String DB_NAME_TELEGRAF = "telegraf";
  public static final String SYSTEM_LOAD = "system_load";
  public static final String SYSTEM_LOAD_1 = "system_load1";
  public static final String SYSTEM_LOAD_5 = "system_load5";
  public static final String SYSTEM_LOAD_15 = "system_load15";
  public static final String MEMORY = "memory";
  public static final String MEM_TOTAL = "mem_total";
  public static final String MEM_USED = "mem_used";
  public static final String MEM_FREE = "mem_available";
  public static final String DISK = "disk";
  public static final String DISK_TOTAL = "disk_total";
  public static final String DISK_USED = "disk_used";
  public static final String DISK_FREE = "disk_free";
  public static final String CPU = "cpu";
  public static final String CPU_USAGE_SYSTEM = "cpu_usage_system";
  public static final String CPU_USAGE_USER = "cpu_usage_user";
  public static final String CPU_TIME_SYSTEM = "cpu_time_system";
  public static final String CPU_TIME_USER = "cpu_time_user";
  public static final String CPU_TOTAL = "cpu-total";
  public static final String SWAP = "swap";
  public static final String SWAP_TOTAL = "swap_total";
  public static final String SWAP_USED = "swap_used";
  public static final String SWAP_FREE = "swap_free";
  public static final String NETIF = "netif";
  public static final String NET_BYTES_RECV = "net_bytes_recv";
  public static final String NET_BYTES_SENT = "net_bytes_sent";
  public static final String NET_DROP_IN = "net_drop_in";
  public static final String NET_DROP_OUT = "net_drop_out";
  public static final String NET_ERR_IN = "net_err_in";
  public static final String NET_ERR_OUT = "net_err_out";
  public static final String NET_PACKETS_RECV = "net_packets_recv";
  public static final String NET_PACKETS_SENT = "net_packets_sent";
  public static final String CPU_LOAD_1 = "Load1 (%)";
  public static final String CPU_LOAD_5 = "Load5 (%)";
  public static final String CPU_LOAD_15 = "Load15 (%)";
  public static final String TOTAL = "Total";
  public static final String USED = "Used";
  public static final String FREE = "Free";
  public static final String USAGE_SYSTEM = "System (%)";
  public static final String USAGE_USER = "User (%)";
  public static final String TIME_SYSTEM = "System";
  public static final String TIME_USER = "User";
  public static final String TOTAL_CPU_USAGE = "Total (%)";
  public static final String TOTAL_CPU_TIME = "Total";
  public static final String NETWORK_TOTAL = "Total";
  public static final String NETWORK_SENT = "Sent";
  public static final String NETWORK_RECV = "Receiving";
  public static final String ETH_IF = "eth(.*)";
  public static final int MAX_DATA = 20;

  private Constants() {}
}
