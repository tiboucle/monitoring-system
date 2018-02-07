package com.monitoring.view;

import com.monitoring.model.Node;
import java.beans.PropertyChangeEvent;

public interface ChartUpdater {
  void updateChart(Node aNode, PropertyChangeEvent evt);
}
