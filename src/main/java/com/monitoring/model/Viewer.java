package com.monitoring.model;

import com.monitoring.view.DockingComponent;
import java.util.HashMap;
import java.util.Map;

public class Viewer{
  private final Map<String, DockingComponent> fDockingComponent = new HashMap<String, DockingComponent>();

  public void addDockingViewer(DockingComponent aDockingComponent) {
    fDockingComponent.put(aDockingComponent.getIpAddress(), aDockingComponent);
  }

  public void removeDockingViewer(DockingComponent aDockingComponent) {
    fDockingComponent.remove(aDockingComponent.getIpAddress(), aDockingComponent);
  }

  public int getDockingViewerSize() {
    return fDockingComponent.size();
  }

  public DockingComponent getByIpAddress(String aIpAddress) {
    return fDockingComponent.get(aIpAddress);
  }
}
