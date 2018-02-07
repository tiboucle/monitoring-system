package com.monitoring.controller;

import com.monitoring.model.Node;
import java.util.Map;

public interface Updater {
  void update(Node aNode, String aName, Number aValue, Map<String, String> aTags);
}
