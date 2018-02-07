package com.monitoring.model;

public class NetStat {
  private NetStatTcp fTcp;
  private NetStatUdp fUdp;

  public NetStatTcp getTcp() {
    return fTcp;
  }

  public void setTcp(NetStatTcp aTcp) {
    fTcp = aTcp;
  }

  public NetStatUdp getUdp() {
    return fUdp;
  }

  public void setUdp(NetStatUdp aUdp) {
    fUdp = aUdp;
  }
}
