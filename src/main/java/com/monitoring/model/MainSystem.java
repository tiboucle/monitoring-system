package com.monitoring.model;

import java.util.HashMap;
import java.util.Map;

public class MainSystem {
  private final Map<String, Node> fNodes = new HashMap<String, Node>();

  public void addNode(Node aNode) {
    fNodes.put(aNode.getIpAddress(), aNode);
  }

  public void removeNode(Node aNode) {
    fNodes.remove(aNode.getIpAddress(), aNode);
  }

  public int getNodeSize() {
    return fNodes.size();
  }

  public Iterable<String> getIpAddresses() {
    return fNodes.keySet();
  }

  public Node getByIpAddress(String aIpAddress) {
    return fNodes.get(aIpAddress);
  }
}
