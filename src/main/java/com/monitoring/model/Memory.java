package com.monitoring.model;

public class Memory {
  private long fAvailable;
  private double fAvailablePercent;
  private long fBuffered;
  private long fCached;
  private long fFree;
  private long fTotal;
  private long fUsed;
  private double fUsedPercent;

  public long getAvailable() {
    return fAvailable;
  }

  public void setAvailable(long aAvailable) {
    fAvailable = aAvailable;
  }

  public double getAvailablePercent() {
    return fAvailablePercent;
  }

  public void setAvailablePercent(double aAvailablePercent) {
    fAvailablePercent = aAvailablePercent;
  }

  public long getBuffered() {
    return fBuffered;
  }

  public void setBuffered(long aBuffered) {
    fBuffered = aBuffered;
  }

  public long getCached() {
    return fCached;
  }

  public void setCached(long aCached) {
    fCached = aCached;
  }

  public long getFree() {
    return fFree;
  }

  public void setFree(long aFree) {
    fFree = aFree;
  }

  public long getTotal() {
    return fTotal;
  }

  public void setTotal(long aTotal) {
    fTotal = aTotal;
  }

  public long getUsed() {
    return fUsed;
  }

  public void setUsed(long aUsed) {
    fUsed = aUsed;
  }

  public double getUsedPercent() {
    return fUsedPercent;
  }

  public void setUsedPercent(double aUsedPercent) {
    fUsedPercent = aUsedPercent;
  }
}
