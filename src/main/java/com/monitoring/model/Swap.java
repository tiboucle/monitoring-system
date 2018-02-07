package com.monitoring.model;

public class Swap {
  private long fFree;
  private long fIn;
  private long fOut;
  private long fTotal;
  private long fUsed;
  private double fUsedPercent;

  public long getFree() {
    return fFree;
  }

  public void setFree(long aFree) {
    fFree = aFree;
  }

  public long getIn() {
    return fIn;
  }

  public void setIn(long aIn) {
    fIn = aIn;
  }

  public long getOut() {
    return fOut;
  }

  public void setOut(long aOut) {
    fOut = aOut;
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
