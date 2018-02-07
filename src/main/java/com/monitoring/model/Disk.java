package com.monitoring.model;

import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

public class Disk {
  private String fPath = "/";
  private String fType;
  private long fTotal;
  private long fUsed;
  private long fFree;

  public String getPath() {
    return fPath;
  }

  public void setPath(String aPath) {
    if (aPath == null) {
      throw new NullPointerException("Path must not null");
    }
    fPath = aPath;
  }

  public String getType() {
    return fType;
  }

  public void setType(String aType) {
    fType = aType;
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

  public long getFree() {
    return fFree;
  }

  public void setFree(long aFree) {
    fFree = aFree;
  }

  @Override
  public boolean equals(Object aO) {
    if (this == aO) return true;
    if (aO == null || getClass() != aO.getClass()) return false;

    Disk disk = (Disk) aO;

    return fPath.equals(disk.fPath);
  }

  @Override
  public int hashCode() {
    return fPath.hashCode();
  }
}
