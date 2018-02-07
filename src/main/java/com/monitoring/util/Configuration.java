package com.monitoring.util;

import com.monitoring.model.Agents;
import java.util.List;

public class Configuration {
  private String server;
  private String port;
  private List<Agents> agents;

  public String getPort() {
    return port;
  }

  public void setPort(String aPort) {
    port = aPort;
  }

  public String getServer() {
    return server;
  }

  public void setServer(String aServer) {
    server = aServer;
  }

  public List<Agents> getAgents() {
    return agents;
  }

  public void setAgents(List<Agents> agents) {
    this.agents = agents;
  }

  public String getServerUrl() {
    return new StringBuilder().append("http://" + server + ":" + port + "/").toString();
  }
}
