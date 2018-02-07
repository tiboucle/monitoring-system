package com.monitoring.controller;

import com.monitoring.model.MainSystem;
import com.monitoring.model.Node;
import com.monitoring.util.Constants;
import com.monitoring.util.QueryUtil;
import com.monitoring.view.DockingView;
import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Map;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class Util {
  private static final Logger LOG = LoggerFactory.getLogger(Util.class);
  private static int fServerStatus = 0;
  public static Number findValue(String aIpAddress, List<String> aColumns, List<List<Object>> aValues) {
    int ipIdx = aColumns.indexOf("ip");
    int valueIdx = aColumns.indexOf("value");

    for (List<Object> vals : aValues) {
      Object ipAddress = vals.get(ipIdx);
      Object value = vals.get(valueIdx);
      if (aIpAddress.equals(ipAddress)) {
        return (Double) value;
      }
    }

    return null;
  }

  public static void updateSystem(DockingView aView, InfluxDB aInfluxDB, MainSystem aSystem, String aMetricName, Updater aUpdater) {
    for (final String ipAddress : aSystem.getIpAddresses()) {
      Node node = aSystem.getByIpAddress(ipAddress);
      Query q = new Query(QueryUtil.generate(ipAddress, aMetricName), Constants.DB_NAME_TELEGRAF);
      LOG.info("Query>>> {}", q.getCommand());
      QueryResult qres = null;
      try {
        qres = aInfluxDB.query(q);
        if (fServerStatus != 1) {
          aView.propertyChange(new PropertyChangeEvent(aInfluxDB, "server_status", fServerStatus, 1));
          fServerStatus = 1;
        }
      } catch (Exception e) {
        LOG.error("Something happened, please checking error >>> {}", e.getMessage());
        if (fServerStatus != 0) {
          aView.propertyChange(new PropertyChangeEvent(aInfluxDB, "server_status", fServerStatus, 0));
          fServerStatus = 0;
        }
      }
      if (qres != null) {
        List<QueryResult.Result> results = qres.getResults();
        if (results == null) {
          continue;
        }
        for (QueryResult.Result result : results) {
          List<QueryResult.Series> rSeries = result.getSeries();
          if (rSeries == null) {
            continue;
          }
          for (QueryResult.Series series : rSeries) {
            List<String> columns = series.getColumns();
            List<List<Object>> values = series.getValues();
            String name = series.getName();
            Number value = Util.findValue(ipAddress, columns, values);
            Map<String, String> tags = series.getTags();
            aUpdater.update(node, name, value, tags);
          }
        }
      }
    }
  }
}