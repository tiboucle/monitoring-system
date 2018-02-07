package com.monitoring.model;

public class NetIf {
  private String fName = "unk";
  private long fBytesRecv;
  private long fBytesSent;
  private long fDropIn;
  private long fDropOut;
  private long fErrIn;
  private long fErrOut;
  private long fPacketsRecv;
  private long fPacketsSent;

  public String getName() {
    return fName;
  }

  public void setName(String aName) {
    if (aName == null) {
      throw new NullPointerException("Name must not null");
    }
    fName = aName;
  }

  public long getBytesRecv() {
    return fBytesRecv;
  }

  public void setBytesRecv(long aBytesRecv) {
    fBytesRecv = aBytesRecv;
  }

  public long getBytesSent() {
    return fBytesSent;
  }

  public void setBytesSent(long aBytesSent) {
    fBytesSent = aBytesSent;
  }

  public long getDropIn() {
    return fDropIn;
  }

  public void setDropIn(long aDropIn) {
    fDropIn = aDropIn;
  }

  public long getDropOut() {
    return fDropOut;
  }

  public void setDropOut(long aDropOut) {
    fDropOut = aDropOut;
  }

  public long getErrIn() {
    return fErrIn;
  }

  public void setErrIn(long aErrIn) {
    fErrIn = aErrIn;
  }

  public long getErrOut() {
    return fErrOut;
  }

  public void setErrOut(long aErrOut) {
    fErrOut = aErrOut;
  }

  public long getPacketsRecv() {
    return fPacketsRecv;
  }

  public void setPacketsRecv(long aPacketsRecv) {
    fPacketsRecv = aPacketsRecv;
  }

  public long getPacketsSent() {
    return fPacketsSent;
  }

  public void setPacketsSent(long aPacketsSent) {
    fPacketsSent = aPacketsSent;
  }

  @Override
  public boolean equals(Object aO) {
    if (this == aO) return true;
    if (aO == null || getClass() != aO.getClass()) return false;

    NetIf netIf = (NetIf) aO;

    return fName.equals(netIf.fName);
  }

  @Override
  public int hashCode() {
    return fName.hashCode();
  }
}
