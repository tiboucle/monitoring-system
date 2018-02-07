package com.monitoring.model;

import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public class Cpu {
  private String fName;
  double fTimeUser;
  double fTimeSystem;
  double fUsageUser;
  double fUsageSystem;

  public String getName() {
    return fName;
  }

  public void setName(String aName) {
    fName = aName;
  }

  public double getTimeUser() {
    return fTimeUser;
  }

  public void setTimeUser(double aTimeUser) {
    fTimeUser = aTimeUser;
  }

  public double getTimeSystem() {
    return fTimeSystem;
  }

  public void setTimeSystem(double aTimeSystem) {
    fTimeSystem = aTimeSystem;
  }

  public double getUsageUser() {
    return fUsageUser;
  }

  public void setUsageUser(double aUsageUser) {
    fUsageUser = aUsageUser;
  }

  public double getUsageSystem() {
    return fUsageSystem;
  }

  public void setUsageSystem(double aUsageSystem) {
    fUsageSystem = aUsageSystem;
  }

  @Override
  public boolean equals(Object aO) {
    if (this == aO) return true;
    if (aO == null || getClass() != aO.getClass()) return false;

    Cpu cpu = (Cpu) aO;

    return fName.equals(cpu.fName);
  }

  @Override
  public int hashCode() {
    return fName.hashCode();
  }

  @Override
  public String toString() {
    return "Cpu{" +
        "Id=" + fName +
        ", TimeUser=" + fTimeUser +
        ", TimeSystem=" + fTimeSystem +
        ", UsageUser=" + fUsageUser +
        ", UsageSystem=" + fUsageSystem +
        '}';
  }
}
