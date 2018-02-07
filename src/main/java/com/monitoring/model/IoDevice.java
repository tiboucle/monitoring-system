package com.monitoring.model;

public class IoDevice {
  private String fName = "unk";
  private String fSerial;
  private long fIoTime;
  private long fReadBytes;
  private long fReadTime;
  private long fReads;
  private long fWriteBytes;
  private long fWriteTime;
  private long fWrites;

  public String getName() {
    return fName;
  }

  public void setName(String aName) {
    if (aName == null) {
      throw new NullPointerException("Name must not null");
    }
    fName = aName;
  }

  public String getSerial() {
    return fSerial;
  }

  public void setSerial(String aSerial) {
    fSerial = aSerial;
  }

  public long getIoTime() {
    return fIoTime;
  }

  public void setIoTime(long aIoTime) {
    fIoTime = aIoTime;
  }

  public long getReadBytes() {
    return fReadBytes;
  }

  public void setReadBytes(long aReadBytes) {
    fReadBytes = aReadBytes;
  }

  public long getReadTime() {
    return fReadTime;
  }

  public void setReadTime(long aReadTime) {
    fReadTime = aReadTime;
  }

  public long getReads() {
    return fReads;
  }

  public void setReads(long aReads) {
    fReads = aReads;
  }

  public long getWriteBytes() {
    return fWriteBytes;
  }

  public void setWriteBytes(long aWriteBytes) {
    fWriteBytes = aWriteBytes;
  }

  public long getWriteTime() {
    return fWriteTime;
  }

  public void setWriteTime(long aWriteTime) {
    fWriteTime = aWriteTime;
  }

  public long getWrites() {
    return fWrites;
  }

  public void setWrites(long aWrites) {
    fWrites = aWrites;
  }

  @Override
  public boolean equals(Object aO) {
    if (this == aO) return true;
    if (aO == null || getClass() != aO.getClass()) return false;

    IoDevice ioDevice = (IoDevice) aO;

    return fName.equals(ioDevice.fName);
  }

  @Override
  public int hashCode() {
    return fName.hashCode();
  }
}
