package com.monitoring.view;

import com.google.common.collect.Iterables;
import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jidesoft.docking.DockContext;
import com.jidesoft.docking.DockableFrame;
import com.jidesoft.icons.JideIconsFactory;
import com.jidesoft.swing.JideTabbedPane;
import com.monitoring.model.Cpu;
import com.monitoring.model.Disk;
import com.monitoring.model.Load;
import com.monitoring.model.MainSystem;
import com.monitoring.model.Memory;
import com.monitoring.model.NetIf;
import com.monitoring.model.Node;
import com.monitoring.model.Swap;
import com.monitoring.util.Constants;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javax.swing.JLabel;

public class DockingComponent extends DockableFrame implements PropertyChangeListener {
  private final SimpleDateFormat fDateFormat = new SimpleDateFormat("HH:mm:ss");
  private final DecimalFormat fDecimalFormat = new DecimalFormat("0.00");
  private final OverviewMemorySwapChart fOverviewMemorySwapChart = new OverviewMemorySwapChart("Memory and Swap");
  private final OverviewCpuUsageChart fOverviewCpuUsageChart = new OverviewCpuUsageChart("Cpu Usage");
  private final OverviewCpuTimeChart fOverviewCpuTimeChart = new OverviewCpuTimeChart("Cpu Time");
  private final OverviewNetIfChart fOverviewNetIfChart = new OverviewNetIfChart("Network (Bytes)");
  private final OverviewDiskChart fOverviewDiskChart = new OverviewDiskChart("Disk");
  private final OverviewLoadChart fOverviewLoadChart = new OverviewLoadChart("Load");
  private final CpuLoadChart fCpuLoadChart = new CpuLoadChart("Load");
  private final CpuTimeChart fCpuTimeChart = new CpuTimeChart("Cpu Time");
  private final CpuUsageChart fCpuUsageChart = new CpuUsageChart("Cpu Usage");
  private final MemoryChart fMemoryChart = new MemoryChart("Memory");
  private final SwapChart fSwapChart = new SwapChart("Swap");
  private final NetIfBytesChart fNetIfBytesChart = new NetIfBytesChart("Network (Bytes)");
  private final NetIfPacketChart fNetIfPacketChart = new NetIfPacketChart("Network (Packet)");
  private final DiskChart fDiskChart = new DiskChart();
  private final FxDetailPane fOverviewCpuUsagePane = new FxDetailPane(4, false);
  private final FxDetailPane fOverviewCpuTimePane = new FxDetailPane(4, false);
  private final FxDetailPane fOverviewNetIfPane = new FxDetailPane(3, false);
  private final FxDetailPane fOverviewDiskPane = new FxDetailPane(3, false);
  private final FxDetailPane fOverviewLoadPane = new FxDetailPane(3, false);
  private final FxDetailPane fOverviewMemoryPane = new FxDetailPane(4, false);
  private final FxDetailPane fCpuLoadPane = new FxDetailPane(3, false);
  private final FxDetailPane fCpuTimePane = new FxDetailPane(4, false);
  private final FxDetailPane fCpuUsagePane = new FxDetailPane(4, false);
  private final FxDetailPane fMemoryPane = new FxDetailPane(4, false);
  private final FxDetailPane fSwapPane = new FxDetailPane(4, false);
  private final FxDetailPane fNetIfBytesPane = new FxDetailPane(3, false);
  private final FxDetailPane fNetIfPacketPane = new FxDetailPane(3, false);
  private final FxDetailPane fDiskPane = new FxDetailPane(4, true);
  private final Label fOverviewDiskTotalLabel = new Label();
  private final Label fOverviewDiskUsedLabel = new Label();
  private final Label fOverviewDiskFreeLabel = new Label();
  private final Label fOverviewMemoryTotalLabel = new Label();
  private final Label fOverviewMemoryUsedLabel = new Label();
  private final Label fOverviewMemoryFreeLabel = new Label();
  private final Label fOverviewSwapTotalLabel = new Label();
  private final Label fOverviewSwapUsedLabel = new Label();
  private final Label fOverviewSwapFreeLabel = new Label();
  private final Label fOverviewLoad1Label = new Label();
  private final Label fOverviewLoad5Label = new Label();
  private final Label fOverviewLoad15Label = new Label();
  private final Label fCpuLoad1Label = new Label();
  private final Label fCpuLoad5Label = new Label();
  private final Label fCpuLoad15Label = new Label();
  private final Label fMemoryTotalLabel = new Label();
  private final Label fMemoryUsedLabel = new Label();
  private final Label fMemoryUsedPercentLabel = new Label();
  private final Label fMemoryFreeLabel = new Label();
  private final Label fMemoryFreePercentLabel = new Label();
  private final Label fSwapTotalLabel = new Label();
  private final Label fSwapUsedLabel = new Label();
  private final Label fSwapUsedPercentLabel = new Label();
  private final Label fSwapFreeLabel = new Label();
  private final Label fSwapFreePercentLabel = new Label();
  private final MainSystem fMainSystem;
  private final String fIpAddress;
  private String fTime;

  public DockingComponent(MainSystem aMainSystem, String aIpAddress) {
    if (aIpAddress == null) {
      throw new NullPointerException("IP Address must not null");
    }
    if (aMainSystem == null) {
      throw new NullPointerException("Main System must not null");
    }
    fIpAddress = aIpAddress;
    fMainSystem = aMainSystem;
    loadComponent();
  }

  private void loadComponent() {
    Node node = fMainSystem.getByIpAddress(fIpAddress);
    setKey(fIpAddress);
    setTitle(node.getHost() + " [ " + node.getIpAddress() + " ]");
    setFrameIcon(JideIconsFactory.getImageIcon(JideIconsFactory.DockableFrame.BLANK));
    getContext().setInitMode(DockContext.STATE_HIDDEN);
    getContext().setInitSide(DockContext.DOCK_SIDE_CENTER);
    //getContext().setInitIndex(1);
    getAutohideAction().setEnabled(false);
    setAvailableButtons(
        DockableFrame.CLOSE_ACTION_TO_REMOVE
            | DockableFrame.BUTTON_MAXIMIZE
            | DockableFrame.BUTTON_FLOATING);

    JideTabbedPane tabbedPane = new JideTabbedPane();
    tabbedPane.addTab("Overview", buildOverviewChart());
    tabbedPane.addTab("Cpu", buildCpuChart());
    tabbedPane.addTab("Memory", buildMemoryChart());
    tabbedPane.addTab("Disk", buildDiskChart());
    tabbedPane.addTab("Network", buildNetworkChart());
    getContentPane().add(tabbedPane);
    setPreferredSize(new Dimension(1000, 800));
  }

  private JFXPanel buildOverviewChart() {
    JFXPanel panel = new JFXPanel();
    AnchorPane anchorPane = new AnchorPane();
    anchorPane.setId("OverviewPane");
    anchorPane.setMaxHeight(Region.USE_COMPUTED_SIZE);
    anchorPane.setMaxWidth(Region.USE_COMPUTED_SIZE);
    anchorPane.setMinHeight(Region.USE_COMPUTED_SIZE);
    anchorPane.setMinWidth(Region.USE_COMPUTED_SIZE);
    anchorPane.setPrefHeight(800);
    anchorPane.setPrefWidth(1000);

    GridPane gridPane = new GridPane();
    gridPane.setGridLinesVisible(false);
    gridPane.setPrefHeight(800);
    gridPane.setPrefWidth(1000);
    AnchorPane.setBottomAnchor(gridPane, 0.0);
    AnchorPane.setLeftAnchor(gridPane, 0.0);
    AnchorPane.setRightAnchor(gridPane, 0.0);
    AnchorPane.setTopAnchor(gridPane, 0.0);

    fOverviewLoadChart.setGridPosition(0, 0);
    LineChart loadChart = fOverviewLoadChart.getLineChart();
    fOverviewLoadPane.setGridPosition(0, 1);
    fOverviewLoadPane.add(new Label(Constants.CPU_LOAD_1), 0, 0);
    fOverviewLoadPane.add(new Label(Constants.CPU_LOAD_5), 1, 0);
    fOverviewLoadPane.add(new Label(Constants.CPU_LOAD_15), 2, 0);
    fOverviewLoadPane.add(fOverviewLoad1Label, 0, 1);
    fOverviewLoadPane.add(fOverviewLoad5Label, 1, 1);
    fOverviewLoadPane.add(fOverviewLoad15Label, 2, 1);

    fOverviewMemorySwapChart.setGridPosition(1, 0);
    PieChart memoryChart = fOverviewMemorySwapChart.getPieChart();
    fOverviewMemoryPane.setGridPosition(1, 1);
    fOverviewMemoryPane.add(new Label(Constants.MEMORY), 0, 1);
    fOverviewMemoryPane.add(new Label(Constants.SWAP), 0, 2);
    fOverviewMemoryPane.add(new Label(Constants.TOTAL), 1, 0);
    fOverviewMemoryPane.add(new Label(Constants.USED), 2, 0);
    fOverviewMemoryPane.add(new Label(Constants.FREE), 3, 0);
    fOverviewMemoryPane.add(fOverviewMemoryTotalLabel, 1, 1);
    fOverviewMemoryPane.add(fOverviewMemoryUsedLabel, 2, 1);
    fOverviewMemoryPane.add(fOverviewMemoryFreeLabel, 3, 1);
    fOverviewMemoryPane.add(fOverviewSwapTotalLabel, 1, 2);
    fOverviewMemoryPane.add(fOverviewSwapUsedLabel, 2, 2);
    fOverviewMemoryPane.add(fOverviewSwapFreeLabel, 3, 2);

    fOverviewDiskChart.setGridPosition(2, 0);
    PieChart diskChart = fOverviewDiskChart.getPieChart();
    fOverviewDiskPane.setGridPosition(2, 1);
    fOverviewDiskPane.add(new Label(Constants.TOTAL), 0, 0);
    fOverviewDiskPane.add(new Label(Constants.USED), 1, 0);
    fOverviewDiskPane.add(new Label(Constants.FREE), 2, 0);
    fOverviewDiskPane.add(fOverviewDiskTotalLabel, 0, 1);
    fOverviewDiskPane.add(fOverviewDiskUsedLabel, 1, 1);
    fOverviewDiskPane.add(fOverviewDiskFreeLabel, 2, 1);

    fOverviewCpuUsageChart.setGridPosition(0, 2);
    LineChart cpuUsageChart = fOverviewCpuUsageChart.getLineChart();
    fOverviewCpuUsagePane.setGridPosition(0, 3);
    fOverviewCpuUsagePane.add(new Label(Constants.USAGE_SYSTEM), 1, 0);
    fOverviewCpuUsagePane.add(new Label(Constants.USAGE_USER), 2, 0);
    fOverviewCpuUsagePane.add(new Label(Constants.TOTAL_CPU_USAGE), 3, 0);

    fOverviewCpuTimeChart.setGridPosition(1, 2);
    LineChart cpuTimeChart = fOverviewCpuTimeChart.getLineChart();
    fOverviewCpuTimePane.setGridPosition(1, 3);
    fOverviewCpuTimePane.add(new Label(Constants.TIME_SYSTEM), 1, 0);
    fOverviewCpuTimePane.add(new Label(Constants.TIME_USER), 2, 0);
    fOverviewCpuTimePane.add(new Label(Constants.TOTAL_CPU_TIME), 3, 0);

    fOverviewNetIfChart.setGridPosition(2, 2);
    AreaChart netIfChart = fOverviewNetIfChart.getAreaChart();
    fOverviewNetIfPane.setGridPosition(2, 3);
    fOverviewNetIfPane.add(new Label(Constants.NETWORK_RECV), 1, 0);
    fOverviewNetIfPane.add(new Label(Constants.NETWORK_SENT), 2, 0);

    ColumnConstraints column = new ColumnConstraints();
    column.setHgrow(Priority.ALWAYS);
    column.setMinWidth(100);
    column.setPrefWidth(100);

    RowConstraints rowChart = new RowConstraints();
    rowChart.setVgrow(Priority.ALWAYS);
    rowChart.setMaxHeight(Region.USE_COMPUTED_SIZE);
    rowChart.setMinHeight(Region.USE_COMPUTED_SIZE);
    rowChart.setPrefHeight(220);
    RowConstraints rowDetailShort = new RowConstraints();
    rowDetailShort.setVgrow(Priority.NEVER);
    rowDetailShort.setMaxHeight(80);
    rowDetailShort.setMinHeight(80);
    rowDetailShort.setPrefHeight(80);
    RowConstraints rowDetailLong = new RowConstraints();
    rowDetailLong.setVgrow(Priority.NEVER);
    rowDetailLong.setMaxHeight(180);
    rowDetailLong.setMinHeight(60);
    rowDetailLong.setPrefHeight(60);

    gridPane.getColumnConstraints().addAll(column, column, column);
    gridPane.getRowConstraints().addAll(rowChart, rowDetailShort, rowChart, rowDetailLong);
    gridPane.getChildren()
        .addAll(loadChart,
            fOverviewLoadPane,
            memoryChart,
            fOverviewMemoryPane,
            diskChart,
            fOverviewDiskPane,
            cpuUsageChart,
            //fOverviewCpuUsagePane,
            cpuTimeChart,
            //fOverviewCpuTimePane,
            netIfChart
            //fOverviewNetIfPane
            );

    anchorPane.getChildren().add(gridPane);
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        Scene scene = new Scene(anchorPane);
        scene.getStylesheets().add("fx/chart.css");
        panel.setScene(scene);
      }
    });

    return panel;
  }

  private JFXPanel buildCpuChart() {
    JFXPanel panel = new JFXPanel();
    AnchorPane anchorPane = new AnchorPane();
    anchorPane.setId("CpuPane");
    anchorPane.setMaxHeight(Region.USE_COMPUTED_SIZE);
    anchorPane.setMaxWidth(Region.USE_COMPUTED_SIZE);
    anchorPane.setMinHeight(Region.USE_COMPUTED_SIZE);
    anchorPane.setMinWidth(Region.USE_COMPUTED_SIZE);
    anchorPane.setPrefHeight(800);
    anchorPane.setPrefWidth(1000);

    GridPane gridPane = new GridPane();
    gridPane.setGridLinesVisible(false);
    gridPane.setPrefHeight(800);
    gridPane.setPrefWidth(1000);
    AnchorPane.setBottomAnchor(gridPane, 0.0);
    AnchorPane.setLeftAnchor(gridPane, 0.0);
    AnchorPane.setRightAnchor(gridPane, 0.0);
    AnchorPane.setTopAnchor(gridPane, 0.0);

    fCpuUsageChart.setGridPosition(0, 0);
    LineChart cpuUsageChart = fCpuUsageChart.getLineChart();
    fCpuUsagePane.setGridPosition(1, 0);
    fCpuUsagePane.add(new Label(Constants.USAGE_SYSTEM), 1, 0);
    fCpuUsagePane.add(new Label(Constants.USAGE_USER), 2, 0);
    fCpuUsagePane.add(new Label(Constants.TOTAL_CPU_USAGE), 3, 0);

    fCpuTimeChart.setGridPosition(0, 1);
    LineChart cpuTimeChart = fCpuTimeChart.getLineChart();
    fCpuTimePane.setGridPosition(1, 1);
    fCpuTimePane.add(new Label(Constants.TIME_SYSTEM), 1, 0);
    fCpuTimePane.add(new Label(Constants.TIME_USER), 2, 0);
    fCpuTimePane.add(new Label(Constants.TOTAL_CPU_TIME), 3, 0);

    fCpuLoadChart.setGridPosition(0, 2);
    LineChart loadChart = fCpuLoadChart.getLineChart();
    fCpuLoadPane.setGridPosition(1, 2);
    fCpuLoadPane.add(new Label(Constants.CPU_LOAD_1), 0, 0);
    fCpuLoadPane.add(new Label(Constants.CPU_LOAD_5), 1, 0);
    fCpuLoadPane.add(new Label(Constants.CPU_LOAD_15), 2, 0);
    fCpuLoadPane.add(fCpuLoad1Label, 0, 1);
    fCpuLoadPane.add(fCpuLoad5Label, 1, 1);
    fCpuLoadPane.add(fCpuLoad15Label, 2, 1);

    ColumnConstraints columnChart = new ColumnConstraints();
    columnChart.setHgrow(Priority.ALWAYS);
    columnChart.setMinWidth(300);
    columnChart.setPrefWidth(800);
    ColumnConstraints columnDetail = new ColumnConstraints();
    columnDetail.setHgrow(Priority.ALWAYS);
    columnDetail.setMinWidth(100);
    columnDetail.setPrefWidth(200);

    RowConstraints row = new RowConstraints();
    row.setVgrow(Priority.ALWAYS);
    row.setMaxHeight(Region.USE_COMPUTED_SIZE);
    row.setMinHeight(Region.USE_COMPUTED_SIZE);
    row.setPrefHeight(220);

    gridPane.getColumnConstraints().addAll(columnChart, columnDetail);
    gridPane.getRowConstraints().addAll(row, row, row);
    gridPane.getChildren()
        .addAll(
            cpuUsageChart,
            fCpuUsagePane,
            cpuTimeChart,
            fCpuTimePane,
            loadChart,
            fCpuLoadPane);

    anchorPane.getChildren().add(gridPane);
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        Scene scene = new Scene(anchorPane);
        scene.getStylesheets().add("fx/chart.css");
        panel.setScene(scene);
      }
    });
    return panel;
  }

  private JFXPanel buildMemoryChart() {
    JFXPanel panel = new JFXPanel();
    AnchorPane anchorPane = new AnchorPane();
    anchorPane.setId("MemoryPane");
    anchorPane.setMaxHeight(Region.USE_COMPUTED_SIZE);
    anchorPane.setMaxWidth(Region.USE_COMPUTED_SIZE);
    anchorPane.setMinHeight(Region.USE_COMPUTED_SIZE);
    anchorPane.setMinWidth(Region.USE_COMPUTED_SIZE);
    anchorPane.setPrefHeight(800);
    anchorPane.setPrefWidth(1000);

    GridPane gridPane = new GridPane();
    gridPane.setGridLinesVisible(false);
    gridPane.setPrefHeight(800);
    gridPane.setPrefWidth(1000);
    AnchorPane.setBottomAnchor(gridPane, 0.0);
    AnchorPane.setLeftAnchor(gridPane, 0.0);
    AnchorPane.setRightAnchor(gridPane, 0.0);
    AnchorPane.setTopAnchor(gridPane, 0.0);

    fMemoryChart.setGridPosition(0, 0);
    AreaChart memoryChart = fMemoryChart.getAreaChart();
    fMemoryPane.setGridPosition(1, 0);
    fMemoryPane.add(new Label(Constants.USED), 1, 0);
    fMemoryPane.add(new Label(Constants.FREE), 2, 0);
    fMemoryPane.add(new Label(Constants.TOTAL), 3, 0);
    fMemoryPane.add(new Label("Value"), 0, 1);
    fMemoryPane.add(fMemoryUsedLabel, 1, 1);
    fMemoryPane.add(fMemoryFreeLabel, 2, 1);
    fMemoryPane.add(fMemoryTotalLabel, 3, 1);
    fMemoryPane.add(new Label("Percentage"), 0, 2);
    fMemoryPane.add(fMemoryUsedPercentLabel, 1, 2);
    fMemoryPane.add(fMemoryFreePercentLabel, 2, 2);

    fSwapChart.setGridPosition(0, 1);
    AreaChart swapChart = fSwapChart.getAreaChart();
    fSwapPane.setGridPosition(1, 1);
    fSwapPane.add(new Label(Constants.USED), 1, 0);
    fSwapPane.add(new Label(Constants.FREE), 2, 0);
    fSwapPane.add(new Label(Constants.TOTAL), 3, 0);
    fSwapPane.add(new Label("Value"), 0, 1);
    fSwapPane.add(fSwapUsedLabel, 1, 1);
    fSwapPane.add(fSwapFreeLabel, 2, 1);
    fSwapPane.add(fSwapTotalLabel, 3, 1);
    fSwapPane.add(new Label("Percentage"), 0, 2);
    fSwapPane.add(fSwapUsedPercentLabel, 1, 2);
    fSwapPane.add(fSwapFreePercentLabel, 2, 2);

    ColumnConstraints columnChart = new ColumnConstraints();
    columnChart.setHgrow(Priority.ALWAYS);
    columnChart.setMinWidth(300);
    columnChart.setPrefWidth(800);
    ColumnConstraints columnDetail = new ColumnConstraints();
    columnDetail.setHgrow(Priority.ALWAYS);
    columnDetail.setMinWidth(100);
    columnDetail.setPrefWidth(200);

    RowConstraints row = new RowConstraints();
    row.setVgrow(Priority.ALWAYS);
    row.setMaxHeight(Region.USE_COMPUTED_SIZE);
    row.setMinHeight(Region.USE_COMPUTED_SIZE);
    row.setPrefHeight(220);

    gridPane.getColumnConstraints().addAll(columnChart, columnDetail);
    gridPane.getRowConstraints().addAll(row, row);
    gridPane.getChildren()
        .addAll(memoryChart,
            fMemoryPane,
            swapChart,
            fSwapPane);

    anchorPane.getChildren().add(gridPane);
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        Scene scene = new Scene(anchorPane);
        scene.getStylesheets().add("fx/chart.css");
        panel.setScene(scene);
      }
    });

    return panel;
  }

  private JFXPanel buildNetworkChart() {
    JFXPanel panel = new JFXPanel();
    AnchorPane anchorPane = new AnchorPane();
    anchorPane.setId("NetworkPane");
    anchorPane.setMaxHeight(Region.USE_COMPUTED_SIZE);
    anchorPane.setMaxWidth(Region.USE_COMPUTED_SIZE);
    anchorPane.setMinHeight(Region.USE_COMPUTED_SIZE);
    anchorPane.setMinWidth(Region.USE_COMPUTED_SIZE);
    anchorPane.setPrefHeight(800);
    anchorPane.setPrefWidth(1000);

    GridPane gridPane = new GridPane();
    gridPane.setGridLinesVisible(false);
    gridPane.setPrefHeight(800);
    gridPane.setPrefWidth(1000);
    AnchorPane.setBottomAnchor(gridPane, 0.0);
    AnchorPane.setLeftAnchor(gridPane, 0.0);
    AnchorPane.setRightAnchor(gridPane, 0.0);
    AnchorPane.setTopAnchor(gridPane, 0.0);

    fNetIfBytesChart.setGridPosition(0, 0);
    AreaChart netIfBytesChart = fNetIfBytesChart.getAreaChart();
    fNetIfBytesPane.setGridPosition(1, 0);
    fNetIfBytesPane.add(new Label(Constants.NETWORK_RECV), 1, 0);
    fNetIfBytesPane.add(new Label(Constants.NETWORK_SENT), 2, 0);

    fNetIfPacketChart.setGridPosition(0, 1);
    AreaChart netIfPacketChart = fNetIfPacketChart.getAreaChart();
    fNetIfPacketPane.setGridPosition(1, 1);
    fNetIfPacketPane.add(new Label(Constants.NETWORK_RECV), 1, 0);
    fNetIfPacketPane.add(new Label(Constants.NETWORK_SENT), 2, 0);

    ColumnConstraints columnChart = new ColumnConstraints();
    columnChart.setHgrow(Priority.ALWAYS);
    columnChart.setMinWidth(300);
    columnChart.setPrefWidth(800);

    ColumnConstraints columnDetail = new ColumnConstraints();
    columnDetail.setHgrow(Priority.ALWAYS);
    columnDetail.setMinWidth(100);
    columnDetail.setPrefWidth(200);

    RowConstraints row = new RowConstraints();
    row.setVgrow(Priority.ALWAYS);
    row.setMaxHeight(Region.USE_COMPUTED_SIZE);
    row.setMinHeight(Region.USE_COMPUTED_SIZE);
    row.setPrefHeight(220);

    gridPane.getColumnConstraints().addAll(columnChart, columnDetail);
    gridPane.getRowConstraints().addAll(row, row);
    gridPane.getChildren()
        .addAll(netIfBytesChart,
            fNetIfBytesPane,
            netIfPacketChart,
            fNetIfPacketPane);

    anchorPane.getChildren().add(gridPane);
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        Scene scene = new Scene(anchorPane);
        scene.getStylesheets().add("fx/chart.css");
        panel.setScene(scene);
      }
    });

    return panel;
  }

  private JFXPanel buildDiskChart() {
    JFXPanel panel = new JFXPanel();
    AnchorPane anchorPane = new AnchorPane();
    anchorPane.setId("DiskPane");
    anchorPane.setMaxHeight(Region.USE_COMPUTED_SIZE);
    anchorPane.setMaxWidth(Region.USE_COMPUTED_SIZE);
    anchorPane.setMinHeight(Region.USE_COMPUTED_SIZE);
    anchorPane.setMinWidth(Region.USE_COMPUTED_SIZE);
    anchorPane.setPrefHeight(800);
    anchorPane.setPrefWidth(1000);

    GridPane gridPane = new GridPane();
    gridPane.setGridLinesVisible(false);
    gridPane.setPrefHeight(800);
    gridPane.setPrefWidth(1000);
    AnchorPane.setBottomAnchor(gridPane, 0.0);
    AnchorPane.setLeftAnchor(gridPane, 0.0);
    AnchorPane.setRightAnchor(gridPane, 0.0);
    AnchorPane.setTopAnchor(gridPane, 0.0);

    GridPane.setColumnIndex(fDiskChart, 0);
    GridPane.setRowIndex(fDiskChart, 0);
    GridPane.setMargin(fDiskChart, new Insets(5, 5, 5, 5));

    fDiskPane.setGridPosition(0, 1);
    fDiskPane.add(new Label(Constants.USED), 1, 0);
    fDiskPane.add(new Label(Constants.FREE), 2, 0);
    fDiskPane.add(new Label(Constants.TOTAL), 3, 0);

    ColumnConstraints column = new ColumnConstraints();
    column.setHgrow(Priority.ALWAYS);
    column.setMinWidth(Region.USE_COMPUTED_SIZE);
    column.setPrefWidth(Region.USE_COMPUTED_SIZE);
    column.setHalignment(HPos.CENTER);

    RowConstraints rowChart = new RowConstraints();
    rowChart.setVgrow(Priority.NEVER);
    rowChart.setMaxHeight(Region.USE_COMPUTED_SIZE);
    rowChart.setMinHeight(Region.USE_COMPUTED_SIZE);
    rowChart.setPrefHeight(250);

    RowConstraints rowDetail = new RowConstraints();
    rowDetail.setVgrow(Priority.NEVER);
    rowDetail.setMaxHeight(200);
    rowDetail.setMinHeight(200);
    rowDetail.setPrefHeight(200);

    gridPane.getColumnConstraints().addAll(column);
    gridPane.getRowConstraints().addAll(rowChart, rowDetail);
    gridPane.getChildren().addAll(fDiskChart, fDiskPane);
    anchorPane.getChildren().add(gridPane);

    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        Scene scene = new Scene(anchorPane);
        scene.getStylesheets().add("fx/chart.css");
        panel.setScene(scene);
      }
    });
    return panel;
  }

  private double getPercentage(double aTotal, double aValue) {
    double percentage = (aValue/ aTotal) * 100;
    return Double.parseDouble(fDecimalFormat.format(percentage));
  }

  private double round(double aValue) {
    return Double.parseDouble(fDecimalFormat.format(aValue));
  }

  private String toStringWithPercent(double aValue) {
    return String.valueOf(fDecimalFormat.format(aValue)) + " %";
  }

  private String toStringWithoutPercent(double aValue) {
    return String.valueOf(fDecimalFormat.format(aValue));
  }

  private String toStringValue(double aValue) {
    return String.valueOf(fDecimalFormat.format(aValue));
  }

  private String readableSize(long aSize) {
    if (aSize <= 0) {
      return "0";
    }
    final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
    int digitGroups = (int) (Math.log10(aSize) / Math.log10(1024));
    return fDecimalFormat.format(aSize / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
  }

  private double convertSize(long aSize) {
    if (aSize <= 0) {
      return 0.0;
    }
    int digitGroups = (int) (Math.log10(aSize) / Math.log10(1024));
    return Double.parseDouble(fDecimalFormat.format(aSize / Math.pow(1024, digitGroups)));
  }

  private String unitSize(long aSize) {
    if (aSize <= 0) {
      return "";
    }
    final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
    int digitGroups = (int) (Math.log10(aSize) / Math.log10(1024));
    return units[digitGroups];
  }

  private double toMegaBytes(long aSize) {
    return Double.parseDouble(fDecimalFormat.format(aSize / Math.pow(1024, 2)));
  }

  public String getIpAddress() {
    return fIpAddress;
  }

  @Override
  public void propertyChange(final PropertyChangeEvent evt) {
    if (evt.getSource() instanceof Node) {
      final Node node = (Node) evt.getSource();

      Platform.runLater(new Runnable() {
        @Override
        public void run() {
          // Cpu Total
          updateCpuTotal(node, evt);

          // Per Cpu usage and time
          updatePerCpu(node, evt);

          // Memory
          updateMemory(node, evt);

          // Swap
          updateSwap(node, evt);

          // Load Average
          updateLoad(node, evt);

          // Disk
          updateDisk(node, evt);

          // Network
          updateNetIf(node, evt);
        }
      });
    } else if (evt.getSource() instanceof Date) {
      fTime = fDateFormat.format(evt.getNewValue());
    }
  }

  private void updateDisk(Node aNode, PropertyChangeEvent aEvt) {
    fOverviewDiskChart.updateChart(aNode, aEvt);
    fDiskChart.updateChart(aNode, aEvt);
  }

  private void updateLoad(Node aNode, PropertyChangeEvent aEvt) {
    fOverviewLoadChart.updateChart(aNode, aEvt);
    fCpuLoadChart.updateChart(aNode, aEvt);
  }

  private void updateSwap(Node aNode, PropertyChangeEvent aEvt) {
    fSwapChart.updateChart(aNode, aEvt);
  }

  private void updateMemory(Node aNode, PropertyChangeEvent aEvt) {
    fOverviewMemorySwapChart.updateChart(aNode, aEvt);
    fMemoryChart.updateChart(aNode, aEvt);
  }

  private void updatePerCpu(Node aNode, PropertyChangeEvent aEvt) {
    fOverviewCpuUsageChart.updateChart(aNode, aEvt);
    fOverviewCpuTimeChart.updateChart(aNode, aEvt);
    fCpuTimeChart.updateChart(aNode, aEvt);
    fCpuUsageChart.updateChart(aNode, aEvt);
  }

  private void updateNetIf(Node aNode, PropertyChangeEvent evt) {
    fOverviewNetIfChart.updateChart(aNode, evt);
    fNetIfBytesChart.updateChart(aNode, evt);
    fNetIfPacketChart.updateChart(aNode, evt);
  }

  private void updateCpuTotal(Node aNode, PropertyChangeEvent evt) {
    //Cpu cpuTotal = aNode.getCpuByName("cpu-total");
    //if (cpuTotal != null) {
    //  if ("cpu-total".equalsIgnoreCase(cpuTotal.getName())) {
    //    if (evt.getPropertyName().equalsIgnoreCase(Constants.CPU_USAGE_SYSTEM)) {
    //      double usageSystem = cpuTotal.getUsageSystem();
    //      fListCpuUsageSystem.add(new XYChart.Data(fTime, usageSystem));
    //      fSeriesCpuUsageSystem.setName("Cpu Usage System: " + toStringWithPercent(usageSystem));
    //      if (fListCpuUsageSystem.size() > getLimitOfPoint(aNode.getCpuSize())) {
    //        fListCpuUsageSystem.remove(0, getNumberOfPoint(fListCpuUsageSystem.size(), aNode.getCpuSize()));
    //      }
    //    }
    //    if (evt.getPropertyName().equalsIgnoreCase(Constants.CPU_USAGE_USER)) {
    //      double usageUser = cpuTotal.getUsageUser();
    //      fListCpuUsageUser.add(new XYChart.Data(fTime, usageUser));
    //      fSeriesCpuUsageUser.setName("Cpu Usage User: " + toStringWithPercent(usageUser));
    //      if (fListCpuUsageUser.size() > getLimitOfPoint(aNode.getCpuSize())) {
    //        fListCpuUsageUser.remove(0, getNumberOfPoint(fListCpuUsageUser.size(), aNode.getCpuSize()));
    //      }
    //    }
    //  }
    //}
  }

  private int getNumberOfPoint(int aSizeAllData, int aSizeResource) {
    return aSizeAllData - (aSizeResource * Constants.MAX_DATA);
  }

  private int getLimitOfPoint(int aSizeResource) {
    return aSizeResource * Constants.MAX_DATA;
  }

  private void appendRowDataDetail(ArrayList<ArrayList<JLabel>> aDetailValues, FormBuilder aBuilder, String aName,
      int index, int aColumn) {
    ArrayList<JLabel> values = new ArrayList<JLabel>();
    aDetailValues.add(values);
    aBuilder.appendRows("p");
    JLabel title = new JLabel();
    title.setName(aName);
    title.setText(aName);
    values.add(title);
    aBuilder.add(title).xy(1, 3 + (2 * index)).add(":").xy(3, 3 + (2 * index));
    for (int i = 0; i < aColumn; i++) {
      JLabel value = new JLabel();
      value.setName(aName);
      value.setText("Waiting...");
      values.add(value);
      aBuilder.add(value).xy(5 + (2 * i), 3 + (2 * index), CellConstraints.CENTER, CellConstraints.CENTER);
    }
    aBuilder.appendRows("$lgap");
  }

  private class MetricNameComparator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
      String string1 = (String) o1;
      String string2 = (String) o2;
      return string1.compareTo(string2);
    }
  }

  private class OverviewCpuUsageChart extends FxLineChart implements ChartUpdater {
    private final ArrayList<String> fPerCpuNames = new ArrayList<>();
    private final ArrayList<Label> fListUsageSystemDetail = new ArrayList<>();
    private final ArrayList<Label> fListUsageUserDetail = new ArrayList<>();
    private final ArrayList<Label> fListUsageTotalDetail = new ArrayList<>();
    private int fCountCpu;
    private int fCountCpuDetail;
    private int fCountCpuToPane;

    public OverviewCpuUsageChart(String aTitle) {
      super(aTitle, false);
      super.setYAxisTickLabel("%");
    }

    @Override
    public void updateChart(Node aNode, PropertyChangeEvent aEvt) {
      if (aNode.getCpuSize() > 0 &&
          (Constants.CPU_USAGE_SYSTEM.equalsIgnoreCase(aEvt.getPropertyName()) ||
              Constants.CPU_USAGE_USER.equalsIgnoreCase(aEvt.getPropertyName()))) {
        for (String cpuName : aNode.getCpus()) {
          Cpu cpu = aNode.getCpuByName(cpuName);
          if (cpu.getName().equalsIgnoreCase("cpu-total")) {
            continue;
          }
          if (fCountCpu < aNode.getCpuSize() - 1) {
            XYChart.Series fOverviewPerCpuUsageSeries = new XYChart.Series();
            fListSeries.addAll(fOverviewPerCpuUsageSeries);
            fPerCpuNames.add(cpuName);
            fCountCpu++;
          }
          if (fListSeries != null && fPerCpuNames.size() == aNode.getCpuSize() - 1) {
            Collections.sort(fPerCpuNames, new MetricNameComparator());
            for (int i = 0; i < fPerCpuNames.size(); i++) {
              String name = fPerCpuNames.get(i);
              if (cpu.getName().equalsIgnoreCase(name)) {
                double usageTotalPerCpu = cpu.getUsageSystem() + cpu.getUsageUser();
                fListSeries.get(i).setName(cpu.getName() + " " + toStringWithPercent(usageTotalPerCpu));
                fListSeries.get(i).getData().addAll(new XYChart.Data(fTime, usageTotalPerCpu));
                /////update value detail pane
                if (fListUsageSystemDetail.size() == aNode.getCpuSize() - 1) {
                  fListUsageSystemDetail.get(i).setText(toStringWithoutPercent(cpu.getUsageSystem()));
                  fListUsageUserDetail.get(i).setText(toStringWithoutPercent(cpu.getUsageUser()));
                  fListUsageTotalDetail.get(i).setText(toStringWithoutPercent(usageTotalPerCpu));
                }
                /////
              }
              if (fListSeries.get(i).getData().size() > getLimitOfPoint(aNode.getCpuSize())) {
                fListSeries.get(i)
                    .getData()
                    .remove(0, getNumberOfPoint(fListSeries.get(i).getData().size(), aNode.getCpuSize()));
              }
            }
            /////create value detail pane
            if (fPerCpuNames.size() == aNode.getCpuSize() - 1) {
              Collections.sort(fPerCpuNames, new MetricNameComparator());
              for (int i = 0; i < fPerCpuNames.size(); i++) {
                if (fCountCpuDetail < aNode.getCpuSize() - 1) {
                  fListUsageSystemDetail.add(new Label(".."));
                  fListUsageUserDetail.add(new Label(".."));
                  fListUsageTotalDetail.add(new Label(".."));
                  fCountCpuDetail++;
                }
              }
              for (int i = 0; i < fListUsageSystemDetail.size(); i++) {
                if (fCountCpuToPane < aNode.getCpuSize() - 1) {
                  fOverviewCpuUsagePane.add(new Label(fPerCpuNames.get(i)), 0, i + 1);
                  fOverviewCpuUsagePane.add(fListUsageSystemDetail.get(i), 1, i + 1);
                  fOverviewCpuUsagePane.add(fListUsageUserDetail.get(i), 2, i + 1);
                  fOverviewCpuUsagePane.add(fListUsageTotalDetail.get(i), 3, i + 1);
                  fCountCpuToPane++;
                }
              }
            }
          }
        }
      }
    }
  }

  private class OverviewCpuTimeChart extends FxLineChart implements ChartUpdater {
    private final ArrayList<String> fPerCpuNames = new ArrayList<>();
    private final ArrayList<Label> fListTimeSystemDetail = new ArrayList<>();
    private final ArrayList<Label> fListTimeUserDetail = new ArrayList<>();
    private final ArrayList<Label> fListTimeTotalDetail = new ArrayList<>();
    private int fCountCpu;
    private int fCountCpuDetail;
    private int fCountCpuToPane;

    public OverviewCpuTimeChart(String aTitle) {
      super(aTitle, false);
    }

    @Override
    public void updateChart(Node aNode, PropertyChangeEvent aEvt) {
      if (aNode.getCpuSize() > 0 &&
          (Constants.CPU_TIME_SYSTEM.equalsIgnoreCase(aEvt.getPropertyName()) ||
              Constants.CPU_TIME_USER.equalsIgnoreCase(aEvt.getPropertyName()))) {

        for (String cpuName : aNode.getCpus()) {
          Cpu cpu = aNode.getCpuByName(cpuName);
          if (cpu.getName().equalsIgnoreCase("cpu-total")) {
            continue;
          }
          if (fCountCpu < aNode.getCpuSize() - 1) {
            XYChart.Series fOverviewPerCpuTimeSeries = new XYChart.Series();
            fListSeries.addAll(fOverviewPerCpuTimeSeries);
            fPerCpuNames.add(cpuName);
            fCountCpu++;
          }
          if (fListSeries != null && fPerCpuNames.size() == aNode.getCpuSize() - 1) {
            Collections.sort(fPerCpuNames, new MetricNameComparator());
            for (int i = 0; i < fPerCpuNames.size(); i++) {
              String name = fPerCpuNames.get(i);
              if (cpu.getName().equalsIgnoreCase(name)) {
                double timeTotalPerCpu = cpu.getTimeSystem() + cpu.getTimeUser();
                fListSeries.get(i).setName(cpu.getName() + " " + toStringWithoutPercent(timeTotalPerCpu));
                fListSeries.get(i).getData().addAll(new XYChart.Data(fTime, timeTotalPerCpu));
                /////update value detail pane
                if (fListTimeSystemDetail.size() == aNode.getCpuSize() - 1) {
                  fListTimeSystemDetail.get(i).setText(toStringWithoutPercent(cpu.getTimeSystem()));
                  fListTimeUserDetail.get(i).setText(toStringWithoutPercent(cpu.getTimeUser()));
                  fListTimeTotalDetail.get(i).setText(toStringWithoutPercent(timeTotalPerCpu));
                }
                /////
              }
              if (fListSeries.get(i).getData().size() > getLimitOfPoint(aNode.getCpuSize())) {
                fListSeries.get(i)
                    .getData()
                    .remove(0, getNumberOfPoint(fListSeries.get(i).getData().size(), aNode.getCpuSize()));
              }
            }

            /////create value detail pane
            if (fPerCpuNames.size() == aNode.getCpuSize() - 1) {
              Collections.sort(fPerCpuNames, new MetricNameComparator());
              for (int i = 0; i < fPerCpuNames.size(); i++) {
                if (fCountCpuDetail < aNode.getCpuSize() - 1) {
                  fListTimeSystemDetail.add(new Label(".."));
                  fListTimeUserDetail.add(new Label(".."));
                  fListTimeTotalDetail.add(new Label(".."));
                  fCountCpuDetail++;
                }
              }
              for (int i = 0; i < fListTimeSystemDetail.size(); i++) {
                if (fCountCpuToPane < aNode.getCpuSize() - 1) {
                  fOverviewCpuTimePane.add(new Label(fPerCpuNames.get(i)), 0, i + 1);
                  fOverviewCpuTimePane.add(fListTimeSystemDetail.get(i), 1, i + 1);
                  fOverviewCpuTimePane.add(fListTimeUserDetail.get(i), 2, i + 1);
                  fOverviewCpuTimePane.add(fListTimeTotalDetail.get(i), 3, i + 1);
                  fCountCpuToPane++;
                }
              }
            }
          }
        }
      }
    }
  }

  private class OverviewNetIfChart extends FxAreaChart implements ChartUpdater {
    private final ArrayList<String> fNetIfNames = new ArrayList<>();
    private final ArrayList<Label> fListNetIfRecvDetail = new ArrayList<>();
    private final ArrayList<Label> fListNetIfSentDetail = new ArrayList<>();
    private final XYChart.Series fNetIfRecvSeries = new XYChart.Series();
    private final XYChart.Series fNetIfSentSeries = new XYChart.Series();
    private int fCountNetIf;
    private int fCountNetIfList;
    private int fCountNetIfToPane;

    public OverviewNetIfChart(String aTitle) {
      super(aTitle, false);
    }

    @Override
    public void updateChart(Node aNode, PropertyChangeEvent aEvt) {
      if (aNode.getNetIfSize() > 0 &&
          (Constants.NET_BYTES_RECV.equalsIgnoreCase(aEvt.getPropertyName()) ||
              Constants.NET_BYTES_SENT.equalsIgnoreCase(aEvt.getPropertyName()))) {
        long bytesRecvAmount = 0;
        long bytesSentAmount = 0;
        Iterable<String> netIfs = Iterables.filter(aNode.getNetIfs(), value -> {
          return value.matches(Constants.ETH_IF);
        });
        for (String interfaceName : netIfs) {
          NetIf netIf = aNode.getNetIfByName(interfaceName);
          //if (!interfaceName.matches("eth(.*)")) {
          //  continue;
          //}
          if (fCountNetIf < aNode.getNetIfSize() && !fNetIfNames.contains(interfaceName)) {
            fNetIfNames.add(interfaceName);
            fOverviewNetIfPane.add(new Label(interfaceName),0 , fCountNetIf+1);
            for (int i = 0; i < fNetIfNames.size(); i++) {
              if (fCountNetIfList < aNode.getNetIfSize()) {
                fListNetIfRecvDetail.add(new Label(".."));
                fListNetIfSentDetail.add(new Label(".."));
                fCountNetIfList++;
              }
            }
            for (int i = 0; i < fListNetIfRecvDetail.size(); i++) {
              if (fCountNetIfToPane < aNode.getNetIfSize()) {
                fOverviewNetIfPane.add(fListNetIfRecvDetail.get(i), 1, i+1);
                fOverviewNetIfPane.add(fListNetIfSentDetail.get(i), 2, i+1);
                fCountNetIfToPane++;
              }
            }
            fCountNetIf++;
          }
          Collections.sort(fNetIfNames, new MetricNameComparator());
          for (int i = 0; i < fNetIfNames.size(); i++) {
            String name = fNetIfNames.get(i);
            if (netIf.getName().equalsIgnoreCase(name)) {
              long bytesRecv = netIf.getBytesRecv();
              long bytesSent = netIf.getBytesSent();
              fListNetIfRecvDetail.get(i).setText(readableSize(bytesRecv));
              fListNetIfSentDetail.get(i).setText(readableSize(bytesSent));
            }
          }
          if (Constants.NET_BYTES_RECV.equalsIgnoreCase(aEvt.getPropertyName())) {
            bytesRecvAmount += netIf.getBytesRecv();
          }
          if (Constants.NET_BYTES_SENT.equalsIgnoreCase(aEvt.getPropertyName())) {
            bytesSentAmount += netIf.getBytesSent();
          }
        }

        // CHART
        if (fListSeries.size() == 0) {
          fListSeries.addAll(fNetIfRecvSeries, fNetIfSentSeries);
        }
        if (aEvt.getPropertyName().equalsIgnoreCase(Constants.NET_BYTES_RECV)) {
          double MBRecvAmount = round(bytesRecvAmount / (1024 * 1024));
          XYChart.Series series = fListSeries.get(0);
          series.getData().add(new XYChart.Data(fTime, MBRecvAmount));
          series.setName("Received: " + MBRecvAmount + "MB");
          if (series.getData().size() > getLimitOfPoint(aNode.getNetIfSize())) {
            series.getData().remove(0, getNumberOfPoint(series.getData().size(), aNode.getNetIfSize()));
          }
        }
        if (aEvt.getPropertyName().equalsIgnoreCase(Constants.NET_BYTES_SENT)) {
          double MBSentAmount = round(bytesSentAmount / (1024 * 1024));
          XYChart.Series series = fListSeries.get(1);
          series.getData().add(new XYChart.Data(fTime, MBSentAmount));
          series.setName("Sent: " + MBSentAmount + "MB");
          if (series.getData().size() > getLimitOfPoint(aNode.getNetIfSize())) {
            series.getData().remove(0, getNumberOfPoint(series.getData().size(), aNode.getNetIfSize()));
          }
        }
      }
    }
  }

  private class OverviewLoadChart extends FxLineChart implements ChartUpdater {
    private final XYChart.Series fLoad1Series = new XYChart.Series();
    private final XYChart.Series fLoad5Series = new XYChart.Series();
    private final XYChart.Series fLoad15Series = new XYChart.Series();

    public OverviewLoadChart(String aTitle) {
      super(aTitle, false);
      super.setYAxisTickLabel("%");
    }

    @Override
    public void updateChart(Node aNode, PropertyChangeEvent aEvt) {
      Load load = aNode.getLoad();
      if (load != null &&
          (Constants.SYSTEM_LOAD_1.equalsIgnoreCase(aEvt.getPropertyName()) ||
              Constants.SYSTEM_LOAD_5.equalsIgnoreCase(aEvt.getPropertyName()) ||
              Constants.SYSTEM_LOAD_15.equalsIgnoreCase(aEvt.getPropertyName()))) {

        // CHART
        if (fListSeries.size() == 0) {
          fListSeries.addAll(fLoad1Series, fLoad5Series, fLoad15Series);
        }
        if (aEvt.getPropertyName().equalsIgnoreCase(Constants.SYSTEM_LOAD_1)) {
          double load1 = load.getLoad1();
          fOverviewLoad1Label.setText(String.valueOf(load1));
          fLoad1Series.setName("Load 1: " + toStringWithPercent(load1));
          fLoad1Series.getData().add(new XYChart.Data(fTime, load1));
          if (fLoad1Series.getData().size() > getLimitOfPoint(1)) {
            fLoad1Series.getData().remove(0, getNumberOfPoint(fLoad1Series.getData().size(), 1));
          }
        }
        if (aEvt.getPropertyName().equalsIgnoreCase(Constants.SYSTEM_LOAD_5)) {
          double load5 = load.getLoad5();
          fOverviewLoad5Label.setText(String.valueOf(load5));
          fLoad5Series.setName("Load 5: " + toStringWithPercent(load5));
          fLoad5Series.getData().add(new XYChart.Data(fTime, load5));
          if (fLoad5Series.getData().size() > getLimitOfPoint(1)) {
            fLoad5Series.getData().remove(0, getNumberOfPoint(fLoad5Series.getData().size(), 1));
          }
        }
        if (aEvt.getPropertyName().equalsIgnoreCase(Constants.SYSTEM_LOAD_15)) {
          double load15 = load.getLoad15();
          String loadAvg15 = String.valueOf(load15);
          fOverviewLoad15Label.setText(loadAvg15);
          fLoad15Series.setName("Load 15: " + toStringWithPercent(load15));
          fLoad15Series.getData().add(new XYChart.Data(fTime, load15));
          if (fLoad15Series.getData().size() > getLimitOfPoint(1)) {
            fLoad15Series.getData().remove(0, getNumberOfPoint(fLoad15Series.getData().size(), 1));
          }
        }
      }
    }
  }

  private class OverviewDiskChart extends FxPieChart implements ChartUpdater {
    private final List<String> fDiskNames = new ArrayList<>();
    private int fCountDisk;
    private double fDiskTotal;
    private double fDiskUsed;
    private double fDiskFree;

    public OverviewDiskChart(String aTitle) {
      super(aTitle);
    }

    @Override
    public void updateChart(Node aNode, PropertyChangeEvent aEvt) {
      if (aNode.getDiskSize() > 0 &&
          (Constants.DISK_TOTAL.equalsIgnoreCase(aEvt.getPropertyName()) ||
              Constants.DISK_USED.equalsIgnoreCase(aEvt.getPropertyName()) ||
              Constants.DISK_FREE.equalsIgnoreCase(aEvt.getPropertyName()))) {

        fDiskTotal = 0;
        fDiskUsed = 0;
        fDiskFree = 0;
        if (fDiskNames.size() != aNode.getDiskSize()) {
          fCountDisk = 0;
          fListData.clear();
          fDiskNames.clear();
          aNode.getDiskPaths().forEach(fDiskNames::add);
        }
        Collections.sort(fDiskNames, new MetricNameComparator());
        for (String path : fDiskNames) {
          Disk disk = aNode.getDiskByPath(path);
          if (fCountDisk < aNode.getDiskSize()) {
            PieChart.Data diskData = new PieChart.Data(path, disk.getTotal());
            fListData.add(diskData);
            fDiskTotal = fDiskTotal + disk.getTotal();
            fDiskUsed = fDiskUsed + disk.getUsed();
            fDiskFree = fDiskFree + disk.getFree();
            fOverviewDiskTotalLabel.setText(readableSize((long) fDiskTotal));
            fOverviewDiskUsedLabel.setText(readableSize((long) fDiskUsed));
            fOverviewDiskFreeLabel.setText(readableSize((long) fDiskFree));
            fCountDisk++;
          } else {
            fDiskTotal = fDiskTotal + disk.getTotal();
            fDiskUsed = fDiskUsed + disk.getUsed();
            fDiskFree = fDiskFree + disk.getFree();
            if (Constants.DISK_TOTAL.equalsIgnoreCase(aEvt.getPropertyName())) {
              fOverviewDiskTotalLabel.setText(readableSize((long) fDiskTotal));
            }
            if (Constants.DISK_USED.equalsIgnoreCase(aEvt.getPropertyName())) {
              fOverviewDiskUsedLabel.setText(readableSize((long) fDiskUsed));
            }
            if (Constants.DISK_FREE.equalsIgnoreCase(aEvt.getPropertyName())) {
              fOverviewDiskFreeLabel.setText(readableSize((long) fDiskFree));
            }
          }
        }
      }
    }
  }

  private class OverviewMemorySwapChart extends FxPieChart implements ChartUpdater {
    private PieChart.Data fMemoryData;
    private PieChart.Data fSwapData;

    public OverviewMemorySwapChart(String aTitle) {
      super(aTitle);
    }

    @Override
    public void updateChart(Node aNode, PropertyChangeEvent aEvt) {
      Memory memory = aNode.getMemory();
      if (memory != null &&
          (aEvt.getPropertyName().equalsIgnoreCase(Constants.MEM_TOTAL) ||
              aEvt.getPropertyName().equalsIgnoreCase(Constants.MEM_USED) ||
              aEvt.getPropertyName().equalsIgnoreCase(Constants.MEM_FREE))) {

        if (aEvt.getPropertyName().equalsIgnoreCase(Constants.MEM_TOTAL)) {
          String totalSize = readableSize(memory.getTotal());
          double total = convertSize(memory.getTotal());
          long oldTotal = (long) aEvt.getOldValue();
          long newTotal = (long) aEvt.getNewValue();
          fOverviewMemoryTotalLabel.setText(totalSize);
          if (oldTotal != newTotal) {
            if (fListData.contains(fMemoryData)) {
              fListData.remove(fMemoryData);
            }
            fMemoryData = new PieChart.Data("Memory", total);
            fListData.addAll(fMemoryData);
          }
        }
        if (aEvt.getPropertyName().equalsIgnoreCase(Constants.MEM_USED)) {
          String used = readableSize(memory.getUsed());
          fOverviewMemoryUsedLabel.setText(used);
        }
        if (aEvt.getPropertyName().equalsIgnoreCase(Constants.MEM_FREE)) {
          String free = readableSize(memory.getTotal() - memory.getUsed());
          fOverviewMemoryFreeLabel.setText(free);
        }
      }

      Swap swap = aNode.getSwap();
      if (swap != null &&
          (aEvt.getPropertyName().equalsIgnoreCase(Constants.SWAP_TOTAL) ||
              aEvt.getPropertyName().equalsIgnoreCase(Constants.SWAP_USED) ||
              aEvt.getPropertyName().equalsIgnoreCase(Constants.SWAP_FREE))) {

        if (aEvt.getPropertyName().equalsIgnoreCase(Constants.SWAP_TOTAL)) {
          String totalSize = readableSize(swap.getTotal());
          double total = convertSize(swap.getTotal());
          long oldTotal = (long) aEvt.getOldValue();
          long newTotal = (long) aEvt.getNewValue();
          fOverviewSwapTotalLabel.setText(totalSize);
          if (oldTotal != newTotal) {
            if (fListData.contains(fSwapData)) {
              fListData.remove(fSwapData);
            }
            fSwapData = new PieChart.Data("Swap", total);
            fListData.addAll(fSwapData);
          }
        }
        if (aEvt.getPropertyName().equalsIgnoreCase(Constants.SWAP_USED)) {
          String used = readableSize(swap.getUsed());
          fOverviewSwapUsedLabel.setText(used);
        }
        if (aEvt.getPropertyName().equalsIgnoreCase(Constants.SWAP_FREE)) {
          String free = readableSize(swap.getTotal() - swap.getUsed());
          fOverviewSwapFreeLabel.setText(free);
        }
      }
    }
  }

  private class CpuLoadChart extends FxLineChart implements ChartUpdater {
    private final XYChart.Series fLoad1Series = new XYChart.Series();
    private final XYChart.Series fLoad5Series = new XYChart.Series();
    private final XYChart.Series fLoad15Series = new XYChart.Series();

    public CpuLoadChart(String aTitle) {
      super(aTitle, false);
      super.setYAxisTickLabel("%");
    }

    @Override
    public void updateChart(Node aNode, PropertyChangeEvent aEvt) {
      Load load = aNode.getLoad();
      if (load != null &&
          (Constants.SYSTEM_LOAD_1.equalsIgnoreCase(aEvt.getPropertyName()) ||
              Constants.SYSTEM_LOAD_5.equalsIgnoreCase(aEvt.getPropertyName()) ||
              Constants.SYSTEM_LOAD_15.equalsIgnoreCase(aEvt.getPropertyName()))) {

        // CHART
        if (fListSeries.size() == 0) {
          fListSeries.addAll(fLoad1Series, fLoad5Series, fLoad15Series);
        }
        if (aEvt.getPropertyName().equalsIgnoreCase(Constants.SYSTEM_LOAD_1)) {
          double load1 = load.getLoad1();
          fCpuLoad1Label.setText(String.valueOf(load1));
          fLoad1Series.setName("Load 1: " + toStringWithPercent(load1));
          fLoad1Series.getData().add(new XYChart.Data(fTime, load1));
          if (fLoad1Series.getData().size() > getLimitOfPoint(1)) {
            fLoad1Series.getData().remove(0, getNumberOfPoint(fLoad1Series.getData().size(), 1));
          }
        }
        if (aEvt.getPropertyName().equalsIgnoreCase(Constants.SYSTEM_LOAD_5)) {
          double load5 = load.getLoad5();
          fCpuLoad5Label.setText(String.valueOf(load5));
          fLoad5Series.setName("Load 5: " + toStringWithPercent(load5));
          fLoad5Series.getData().add(new XYChart.Data(fTime, load5));
          if (fLoad5Series.getData().size() > getLimitOfPoint(1)) {
            fLoad5Series.getData().remove(0, getNumberOfPoint(fLoad5Series.getData().size(), 1));
          }
        }
        if (aEvt.getPropertyName().equalsIgnoreCase(Constants.SYSTEM_LOAD_15)) {
          double load15 = load.getLoad15();
          String loadAvg15 = String.valueOf(load15);
          fCpuLoad15Label.setText(loadAvg15);
          fLoad15Series.setName("Load 15: " + toStringWithPercent(load15));
          fLoad15Series.getData().add(new XYChart.Data(fTime, load15));
          if (fLoad15Series.getData().size() > getLimitOfPoint(1)) {
            fLoad15Series.getData().remove(0, getNumberOfPoint(fLoad15Series.getData().size(), 1));
          }
        }
      }
    }
  }

  private class CpuTimeChart extends FxLineChart implements ChartUpdater {
    private final ArrayList<String> fPerCpuNames = new ArrayList<>();
    private final ArrayList<Label> fListTimeSystemDetail = new ArrayList<>();
    private final ArrayList<Label> fListTimeUserDetail = new ArrayList<>();
    private final ArrayList<Label> fListTimeTotalDetail = new ArrayList<>();
    private int fCountCpu;
    private int fCountCpuDetail;
    private int fCountCpuToPane;

    public CpuTimeChart(String aTitle) {
      super(aTitle, false);
    }

    @Override
    public void updateChart(Node aNode, PropertyChangeEvent aEvt) {
      if (aNode.getCpuSize() > 0 &&
          (Constants.CPU_TIME_SYSTEM.equalsIgnoreCase(aEvt.getPropertyName()) ||
              Constants.CPU_TIME_USER.equalsIgnoreCase(aEvt.getPropertyName()))) {
        for (String cpuName : aNode.getCpus()) {
          Cpu cpu = aNode.getCpuByName(cpuName);
          if (cpu.getName().equalsIgnoreCase("cpu-total")) {
            continue;
          }
          if (fCountCpu < aNode.getCpuSize() - 1) {
            XYChart.Series fOverviewPerCpuTimeSeries = new XYChart.Series();
            fListSeries.addAll(fOverviewPerCpuTimeSeries);
            fPerCpuNames.add(cpuName);
            fCountCpu++;
          }
          if (fListSeries != null && fPerCpuNames.size() == aNode.getCpuSize() - 1) {
            Collections.sort(fPerCpuNames, new MetricNameComparator());
            for (int i = 0; i < fPerCpuNames.size(); i++) {
              String name = fPerCpuNames.get(i);
              if (cpu.getName().equalsIgnoreCase(name)) {
                double timeTotalPerCpu = cpu.getTimeSystem() + cpu.getTimeUser();
                fListSeries.get(i).setName(cpu.getName() + " " + toStringWithoutPercent(timeTotalPerCpu));
                fListSeries.get(i).getData().addAll(new XYChart.Data(fTime, timeTotalPerCpu));
                /////update value detail pane
                if (fListTimeSystemDetail.size() == aNode.getCpuSize() - 1) {
                  fListTimeSystemDetail.get(i).setText(toStringWithoutPercent(cpu.getTimeSystem()));
                  fListTimeUserDetail.get(i).setText(toStringWithoutPercent(cpu.getTimeUser()));
                  fListTimeTotalDetail.get(i).setText(toStringWithoutPercent(timeTotalPerCpu));
                }
                /////
              }
              if (fListSeries.get(i).getData().size() > getLimitOfPoint(aNode.getCpuSize())) {
                fListSeries.get(i)
                    .getData()
                    .remove(0, getNumberOfPoint(fListSeries.get(i).getData().size(), aNode.getCpuSize()));
              }
            }

            /////create value detail pane
            if (fPerCpuNames.size() == aNode.getCpuSize() - 1) {
              Collections.sort(fPerCpuNames, new MetricNameComparator());
              for (int i = 0; i < fPerCpuNames.size(); i++) {
                if (fCountCpuDetail < aNode.getCpuSize() - 1) {
                  fListTimeSystemDetail.add(new Label(".."));
                  fListTimeUserDetail.add(new Label(".."));
                  fListTimeTotalDetail.add(new Label(".."));
                  fCountCpuDetail++;
                }
              }
              for (int i = 0; i < fListTimeSystemDetail.size(); i++) {
                if (fCountCpuToPane < aNode.getCpuSize() - 1) {
                  fCpuTimePane.add(new Label(fPerCpuNames.get(i)), 0, i + 1);
                  fCpuTimePane.add(fListTimeSystemDetail.get(i), 1, i + 1);
                  fCpuTimePane.add(fListTimeUserDetail.get(i), 2, i + 1);
                  fCpuTimePane.add(fListTimeTotalDetail.get(i), 3, i + 1);
                  fCountCpuToPane++;
                }
              }
            }
          }
        }
      }
    }
  }

  private class CpuUsageChart extends FxLineChart implements ChartUpdater {
    private final ArrayList<String> fPerCpuNames = new ArrayList<>();
    private final ArrayList<Label> fListUsageSystemDetail = new ArrayList<>();
    private final ArrayList<Label> fListUsageUserDetail = new ArrayList<>();
    private final ArrayList<Label> fListUsageTotalDetail = new ArrayList<>();
    private int fCountCpu;
    private int fCountCpuDetail;
    private int fCountCpuToPane;

    public CpuUsageChart(String aTitle) {
      super(aTitle, false);
      super.setYAxisTickLabel("%");
    }

    @Override
    public void updateChart(Node aNode, PropertyChangeEvent aEvt) {
      if (aNode.getCpuSize() > 0 &&
          (Constants.CPU_USAGE_SYSTEM.equalsIgnoreCase(aEvt.getPropertyName()) ||
              Constants.CPU_USAGE_USER.equalsIgnoreCase(aEvt.getPropertyName()))) {
        for (String cpuName : aNode.getCpus()) {
          Cpu cpu = aNode.getCpuByName(cpuName);
          if (cpu.getName().equalsIgnoreCase("cpu-total")) {
            continue;
          }
          if (fCountCpu < aNode.getCpuSize() - 1) {
            XYChart.Series fOverviewPerCpuUsageSeries = new XYChart.Series();
            fListSeries.addAll(fOverviewPerCpuUsageSeries);
            fPerCpuNames.add(cpuName);
            fCountCpu++;
          }
          if (fListSeries != null && fPerCpuNames.size() == aNode.getCpuSize() - 1) {
            Collections.sort(fPerCpuNames, new MetricNameComparator());
            for (int i = 0; i < fPerCpuNames.size(); i++) {
              String name = fPerCpuNames.get(i);
              if (cpu.getName().equalsIgnoreCase(name)) {
                double usageTotalPerCpu = cpu.getUsageSystem() + cpu.getUsageUser();
                fListSeries.get(i).setName(cpu.getName() + " " + toStringWithPercent(usageTotalPerCpu));
                fListSeries.get(i).getData().addAll(new XYChart.Data(fTime, usageTotalPerCpu));
                /////update value detail pane
                if (fListUsageSystemDetail.size() == aNode.getCpuSize() - 1) {
                  fListUsageSystemDetail.get(i).setText(toStringWithoutPercent(cpu.getUsageSystem()));
                  fListUsageUserDetail.get(i).setText(toStringWithoutPercent(cpu.getUsageUser()));
                  fListUsageTotalDetail.get(i).setText(toStringWithoutPercent(usageTotalPerCpu));
                }
                /////
              }
              if (fListSeries.get(i).getData().size() > getLimitOfPoint(aNode.getCpuSize())) {
                fListSeries.get(i)
                    .getData()
                    .remove(0, getNumberOfPoint(fListSeries.get(i).getData().size(), aNode.getCpuSize()));
              }
            }
            /////create value detail pane
            if (fPerCpuNames.size() == aNode.getCpuSize() - 1) {
              Collections.sort(fPerCpuNames, new MetricNameComparator());
              for (int i = 0; i < fPerCpuNames.size(); i++) {
                if (fCountCpuDetail < aNode.getCpuSize() - 1) {
                  fListUsageSystemDetail.add(new Label(".."));
                  fListUsageUserDetail.add(new Label(".."));
                  fListUsageTotalDetail.add(new Label(".."));
                  fCountCpuDetail++;
                }
              }
              for (int i = 0; i < fListUsageSystemDetail.size(); i++) {
                if (fCountCpuToPane < aNode.getCpuSize() - 1) {
                  fCpuUsagePane.add(new Label(fPerCpuNames.get(i)), 0, i + 1);
                  fCpuUsagePane.add(fListUsageSystemDetail.get(i), 1, i + 1);
                  fCpuUsagePane.add(fListUsageUserDetail.get(i), 2, i + 1);
                  fCpuUsagePane.add(fListUsageTotalDetail.get(i), 3, i + 1);
                  fCountCpuToPane++;
                }
              }
            }
          }
        }
      }
    }
  }

  private class MemoryChart extends FxAreaChart implements ChartUpdater {
    private final XYChart.Series fTotalSeries = new XYChart.Series();
    private final XYChart.Series fUsedSeries = new XYChart.Series();

    public MemoryChart(String aTitle) {
      super(aTitle, true);
      super.setYAxisTickLabel(" ");
    }

    @Override
    public void updateChart(Node aNode, PropertyChangeEvent aEvt) {
      Memory memory = aNode.getMemory();
      if (memory != null &&
          (Constants.MEM_TOTAL.equalsIgnoreCase(aEvt.getPropertyName()) ||
              Constants.MEM_USED.equalsIgnoreCase(aEvt.getPropertyName()) ||
              Constants.MEM_FREE.equalsIgnoreCase(aEvt.getPropertyName()))) {

        // CHART
        if (fListSeries.size() == 0) {
          fListSeries.addAll(fTotalSeries, fUsedSeries);
        }
        if (aEvt.getPropertyName().equalsIgnoreCase(Constants.MEM_TOTAL)) {
          long total = memory.getTotal();
          double totalSize = toMegaBytes(memory.getTotal());
          fMemoryTotalLabel.setText(readableSize(total));
          fTotalSeries.setName("Total : " + readableSize(total));
          fTotalSeries.getData().add(new XYChart.Data(fTime, totalSize));
          if (fTotalSeries.getData().size() > getLimitOfPoint(1)) {
            fTotalSeries.getData().remove(0, getNumberOfPoint(fTotalSeries.getData().size(), 1));
          }
        }
        if (aEvt.getPropertyName().equalsIgnoreCase(Constants.MEM_USED)) {
          long used = memory.getUsed();
          double totalSize = toMegaBytes(memory.getUsed());
          double percentageUsed = getPercentage(memory.getTotal(), memory.getUsed());
          fMemoryUsedLabel.setText(readableSize(used));
          fMemoryUsedPercentLabel.setText("(" + toStringWithPercent(percentageUsed) + ")");
          fUsedSeries.setName("Used : " + readableSize(used));
          fUsedSeries.getData().add(new XYChart.Data(fTime, totalSize));
          if (fUsedSeries.getData().size() > getLimitOfPoint(1)) {
            fUsedSeries.getData().remove(0, getNumberOfPoint(fUsedSeries.getData().size(), 1));
          }
        }
        if (aEvt.getPropertyName().equalsIgnoreCase(Constants.MEM_FREE)) {
          long free = memory.getTotal() - memory.getUsed();
          double percentageFree = getPercentage(memory.getTotal(), memory.getTotal() - memory.getUsed());
          fMemoryFreeLabel.setText(readableSize(free));
          fMemoryFreePercentLabel.setText("(" + toStringWithPercent(percentageFree) + ")");
        }
      }
    }
  }

  private class SwapChart extends FxAreaChart implements ChartUpdater {
    private final XYChart.Series fTotalSeries = new XYChart.Series();
    private final XYChart.Series fUsedSeries = new XYChart.Series();

    public SwapChart(String aTitle) {
      super(aTitle, true);
      super.setYAxisTickLabel(" ");
    }

    @Override
    public void updateChart(Node aNode, PropertyChangeEvent aEvt) {
      Swap swap = aNode.getSwap();
      if (swap != null &&
          (Constants.SWAP_TOTAL.equalsIgnoreCase(aEvt.getPropertyName()) ||
              Constants.SWAP_USED.equalsIgnoreCase(aEvt.getPropertyName()) ||
              Constants.SWAP_FREE.equalsIgnoreCase(aEvt.getPropertyName()))) {

        // CHART
        if (fListSeries.size() == 0) {
          fListSeries.addAll(fTotalSeries, fUsedSeries);
        }
        if (aEvt.getPropertyName().equalsIgnoreCase(Constants.SWAP_TOTAL)) {
          long total = swap.getTotal();
          double totalSize = toMegaBytes(swap.getTotal());
          fSwapTotalLabel.setText(readableSize(total));
          fTotalSeries.setName("Total : " + readableSize(total));
          fTotalSeries.getData().add(new XYChart.Data(fTime, totalSize));
          if (fTotalSeries.getData().size() > getLimitOfPoint(1)) {
            fTotalSeries.getData().remove(0, getNumberOfPoint(fTotalSeries.getData().size(), 1));
          }
        }
        if (aEvt.getPropertyName().equalsIgnoreCase(Constants.SWAP_USED)) {
          long used = swap.getUsed();
          double totalSize = toMegaBytes(swap.getUsed());
          double percentageUsed = getPercentage(swap.getTotal(), swap.getUsed());
          fSwapUsedLabel.setText(readableSize(used));
          fSwapUsedPercentLabel.setText("(" + toStringWithPercent(percentageUsed) + ")");
          fUsedSeries.setName("Used : " + readableSize(used));
          fUsedSeries.getData().add(new XYChart.Data(fTime, totalSize));
          if (fUsedSeries.getData().size() > getLimitOfPoint(1)) {
            fUsedSeries.getData().remove(0, getNumberOfPoint(fUsedSeries.getData().size(), 1));
          }
        }
        if (aEvt.getPropertyName().equalsIgnoreCase(Constants.SWAP_FREE)) {
          long free = swap.getTotal() - swap.getUsed();
          double percentageFree = getPercentage(swap.getTotal(), swap.getTotal() - swap.getUsed());
          fSwapFreeLabel.setText(readableSize(free));
          fSwapFreePercentLabel.setText("(" + toStringWithPercent(percentageFree) + ")");
        }
      }
    }
  }

  private class NetIfBytesChart extends FxAreaChart implements ChartUpdater {
    private final ArrayList<String> fNetIfNames = new ArrayList<>();
    private final ArrayList<Label> fListNetIfRecvDetail = new ArrayList<>();
    private final ArrayList<Label> fListNetIfSentDetail = new ArrayList<>();
    private final XYChart.Series fNetIfRecvSeries = new XYChart.Series();
    private final XYChart.Series fNetIfSentSeries = new XYChart.Series();
    private int fCountNetIf;
    private int fCountNetIfList;
    private int fCountNetIfToPane;

    public NetIfBytesChart(String aTitle) {
      super(aTitle, true);
      super.setYAxisTickLabel(" ");
    }

    @Override
    public void updateChart(Node aNode, PropertyChangeEvent aEvt) {
      if (aNode.getNetIfSize() > 0 &&
          (Constants.NET_BYTES_RECV.equalsIgnoreCase(aEvt.getPropertyName()) ||
              Constants.NET_BYTES_SENT.equalsIgnoreCase(aEvt.getPropertyName()))) {
        long bytesRecvAmount = 0;
        long bytesSentAmount = 0;
        Iterable<String> netIfs = Iterables.filter(aNode.getNetIfs(), value -> {
          return value.matches(Constants.ETH_IF);
        });
        for (String interfaceName : netIfs) {
          NetIf netIf = aNode.getNetIfByName(interfaceName);
          //if (!interfaceName.matches("eth(.*)")) {
          //  continue;
          //}
          if (fCountNetIf < aNode.getNetIfSize() && !fNetIfNames.contains(interfaceName)) {
            fNetIfNames.add(interfaceName);
            fNetIfBytesPane.add(new Label(interfaceName),0 , fCountNetIf+1);
            for (int i = 0; i < fNetIfNames.size(); i++) {
              if (fCountNetIfList < aNode.getNetIfSize()) {
                fListNetIfRecvDetail.add(new Label(".."));
                fListNetIfSentDetail.add(new Label(".."));
                fCountNetIfList++;
              }
            }
            for (int i = 0; i < fListNetIfRecvDetail.size(); i++) {
              if (fCountNetIfToPane < aNode.getNetIfSize()) {
                fNetIfBytesPane.add(fListNetIfRecvDetail.get(i), 1, i + 1);
                fNetIfBytesPane.add(fListNetIfSentDetail.get(i), 2, i + 1);
                fCountNetIfToPane++;
              }
            }
            fCountNetIf++;
          }
          Collections.sort(fNetIfNames, new MetricNameComparator());
          for (int i = 0; i < fNetIfNames.size(); i++) {
            String name = fNetIfNames.get(i);
            if (netIf.getName().equalsIgnoreCase(name)) {
              long bytesRecv = netIf.getBytesRecv();
              long bytesSent = netIf.getBytesSent();
              fListNetIfRecvDetail.get(i).setText(readableSize(bytesRecv));
              fListNetIfSentDetail.get(i).setText(readableSize(bytesSent));
            }
          }
          if (Constants.NET_BYTES_RECV.equalsIgnoreCase(aEvt.getPropertyName())) {
            bytesRecvAmount += netIf.getBytesRecv();
          }
          if (Constants.NET_BYTES_SENT.equalsIgnoreCase(aEvt.getPropertyName())) {
            bytesSentAmount += netIf.getBytesSent();
          }
        }

        // CHART
        if (fListSeries.size() == 0) {
          fListSeries.addAll(fNetIfRecvSeries, fNetIfSentSeries);
        }
        if (aEvt.getPropertyName().equalsIgnoreCase(Constants.NET_BYTES_RECV)) {
          double MBRecvAmount = round(bytesRecvAmount / (1024 * 1024));
          XYChart.Series series = fListSeries.get(0);
          series.getData().add(new XYChart.Data(fTime, MBRecvAmount));
          series.setName("Received: " + MBRecvAmount + "MB");
          if (series.getData().size() > getLimitOfPoint(aNode.getNetIfSize())) {
            series.getData().remove(0, getNumberOfPoint(series.getData().size(), aNode.getNetIfSize()));
          }
        }
        if (aEvt.getPropertyName().equalsIgnoreCase(Constants.NET_BYTES_SENT)) {
          double MBSentAmount = round(bytesSentAmount / (1024 * 1024));
          XYChart.Series series = fListSeries.get(1);
          series.getData().add(new XYChart.Data(fTime, MBSentAmount));
          series.setName("Sent: " + MBSentAmount + "MB");
          if (series.getData().size() > getLimitOfPoint(aNode.getNetIfSize())) {
            series.getData().remove(0, getNumberOfPoint(series.getData().size(), aNode.getNetIfSize()));
          }
        }
      }
    }
  }

  private class NetIfPacketChart extends FxAreaChart implements ChartUpdater {
    private final ArrayList<String> fNetIfNames = new ArrayList<>();
    private final ArrayList<Label> fListNetIfRecvDetail = new ArrayList<>();
    private final ArrayList<Label> fListNetIfSentDetail = new ArrayList<>();
    private final XYChart.Series fNetIfRecvSeries = new XYChart.Series();
    private final XYChart.Series fNetIfSentSeries = new XYChart.Series();
    private int fCountNetIf;
    private int fCountNetIfList;
    private int fCountNetIfToPane;

    public NetIfPacketChart(String aTitle) {
      super(aTitle, true);
      super.setYAxisTickLabel(" ");
    }

    @Override
    public void updateChart(Node aNode, PropertyChangeEvent aEvt) {
      if (aNode.getNetIfSize() > 0 &&
          (Constants.NET_PACKETS_RECV.equalsIgnoreCase(aEvt.getPropertyName()) ||
              Constants.NET_PACKETS_SENT.equalsIgnoreCase(aEvt.getPropertyName()))) {
        long packetRecvAmount = 0;
        long packetSentAmount = 0;
        Iterable<String> netIfs = Iterables.filter(aNode.getNetIfs(), value -> {
          return value.matches(Constants.ETH_IF);
        });
        for (String interfaceName : netIfs) {
          NetIf netIf = aNode.getNetIfByName(interfaceName);
          //if (!interfaceName.matches("eth(.*)")) {
          //  continue;
          //}
          if (fCountNetIf < aNode.getNetIfSize() && !fNetIfNames.contains(interfaceName)) {
            fNetIfNames.add(interfaceName);
            fNetIfPacketPane.add(new Label(interfaceName),0 , fCountNetIf+1);
            for (int i = 0; i < fNetIfNames.size(); i++) {
              if (fCountNetIfList < aNode.getNetIfSize()) {
                fListNetIfRecvDetail.add(new Label(".."));
                fListNetIfSentDetail.add(new Label(".."));
                fCountNetIfList++;
              }
            }
            for (int i = 0; i < fListNetIfRecvDetail.size(); i++) {
              if (fCountNetIfToPane < aNode.getNetIfSize()) {
                fNetIfPacketPane.add(fListNetIfRecvDetail.get(i), 1, i+1);
                fNetIfPacketPane.add(fListNetIfSentDetail.get(i), 2, i+1);
                fCountNetIfToPane++;
              }
            }
            fCountNetIf++;
          }
          Collections.sort(fNetIfNames, new MetricNameComparator());
          for (int i = 0; i < fNetIfNames.size(); i++) {
            String name = fNetIfNames.get(i);
            if (netIf.getName().equalsIgnoreCase(name)) {
              long packetRecv = netIf.getPacketsRecv();
              long packetSent = netIf.getPacketsSent();
              fListNetIfRecvDetail.get(i).setText(String.valueOf(packetRecv));
              fListNetIfSentDetail.get(i).setText(String.valueOf(packetSent));
            }
          }
          if (Constants.NET_PACKETS_RECV.equalsIgnoreCase(aEvt.getPropertyName())) {
            packetRecvAmount += netIf.getPacketsRecv();
          }
          if (Constants.NET_PACKETS_SENT.equalsIgnoreCase(aEvt.getPropertyName())) {
            packetSentAmount += netIf.getPacketsSent();
          }
        }

        // CHART
        if (fListSeries.size() == 0) {
          fListSeries.addAll(fNetIfRecvSeries, fNetIfSentSeries);
        }
        if (aEvt.getPropertyName().equalsIgnoreCase(Constants.NET_PACKETS_RECV)) {
          XYChart.Series series = fListSeries.get(0);
          series.getData().add(new XYChart.Data(fTime, packetRecvAmount));
          series.setName("Received: " + packetRecvAmount + " Packets");
          if (series.getData().size() > getLimitOfPoint(aNode.getNetIfSize())) {
            series.getData().remove(0, getNumberOfPoint(series.getData().size(), aNode.getNetIfSize()));
          }
        }
        if (aEvt.getPropertyName().equalsIgnoreCase(Constants.NET_PACKETS_SENT)) {
          XYChart.Series series = fListSeries.get(1);
          series.getData().add(new XYChart.Data(fTime, packetSentAmount));
          series.setName("Sent: " + packetSentAmount + " Packets");
          if (series.getData().size() > getLimitOfPoint(aNode.getNetIfSize())) {
            series.getData().remove(0, getNumberOfPoint(series.getData().size(), aNode.getNetIfSize()));
          }
        }
      }
    }
  }

  private class DiskChart extends HBox implements ChartUpdater {
    private final ArrayList<Label> fListUsedDetail = new ArrayList<>();
    private final ArrayList<Label> fListFreeDetail = new ArrayList<>();
    private final ArrayList<Label> fListTotalDetail = new ArrayList<>();
    private final List<String> fDiskNames = new ArrayList<>();
    private int fCountDisk;
    private int fCountDiskDetail;
    private int fCountDiskToPane;
    private double fDiskTotal;
    private double fDiskUsed;
    private double fDiskFree;

    @Override
    public void updateChart(Node aNode, PropertyChangeEvent aEvt) {
      if (aNode.getDiskSize() > 0 &&
          (Constants.DISK_TOTAL.equalsIgnoreCase(aEvt.getPropertyName()) ||
              Constants.DISK_USED.equalsIgnoreCase(aEvt.getPropertyName()) ||
              Constants.DISK_FREE.equalsIgnoreCase(aEvt.getPropertyName()))) {

        for (String path : aNode.getDiskPaths()) {
          Disk disk = aNode.getDiskByPath(path);
          ObservableList<PieChart.Data> dataList = FXCollections.observableArrayList();
          if (fCountDisk < aNode.getDiskSize()) {
            dataList.add(new PieChart.Data("Used ", 0));
            dataList.add(new PieChart.Data("Free ", 0));
            PieChart diskChart = new PieChart(dataList);
            diskChart.setId(path);
            diskChart.setTitle(path);
            diskChart.setCache(true);
            diskChart.setLabelsVisible(false);
            diskChart.setPrefHeight(200);
            this.getChildren().add(diskChart);
            //Scene scene = fDiskPanel.getScene();
            //if (scene == null) {
            //  fDiskPanel.setScene(fDiskScene);
            //}
            fDiskNames.add(path);
            fDiskTotal = fDiskTotal + disk.getTotal();
            fDiskUsed = fDiskUsed + disk.getUsed();
            fDiskFree = fDiskFree + disk.getFree();

            fCountDisk++;
          }

          Collections.sort(fDiskNames, new MetricNameComparator());
          if (fDiskNames.size() == aNode.getDiskSize()) {
            /////create value detail pane
            for (int i = 0; i < fDiskNames.size(); i++) {
              if (fCountDiskDetail < aNode.getDiskSize()) {
                fListUsedDetail.add(new Label(".."));
                fListFreeDetail.add(new Label(".."));
                fListTotalDetail.add(new Label(".."));
                fCountDiskDetail++;
              }
            }
            for (int i = 0; i < fListUsedDetail.size(); i++) {
              if (fCountDiskToPane < aNode.getDiskSize()) {
                fDiskPane.add(new Label(fDiskNames.get(i)), 0, i + 1);
                fDiskPane.add(fListUsedDetail.get(i), 1, i + 1);
                fDiskPane.add(fListFreeDetail.get(i), 2, i + 1);
                fDiskPane.add(fListTotalDetail.get(i), 3, i + 1);
                fCountDiskToPane++;
              }
            }
          }

          for (int i = 0; i < this.getChildren().size(); i++) {
            PieChart updatedChart = (PieChart) this.getChildren().get(i);
            if (path.equalsIgnoreCase(fDiskNames.get(i))) {
              ObservableList<PieChart.Data> data = updatedChart.getData();
              double percentageUsed = getPercentage(disk.getTotal(), disk.getUsed());
              double percentageFree = getPercentage(disk.getTotal(), disk.getFree());
              if (Constants.DISK_TOTAL.equalsIgnoreCase(aEvt.getPropertyName())) {
                updatedChart.setTitle(path + "\n( " + readableSize(disk.getTotal()) + " )");
              }
              if (Constants.DISK_USED.equalsIgnoreCase(aEvt.getPropertyName())) {
                if (data.get(0).getPieValue() != disk.getUsed()) {
                  data.set(0, new PieChart.Data("Used " + readableSize(disk.getUsed()) + " (" + toStringWithPercent(percentageUsed) + ")", disk.getUsed()));
                }
              }
              if (Constants.DISK_FREE.equalsIgnoreCase(aEvt.getPropertyName())) {
                if (data.get(1).getPieValue() != disk.getFree()) {
                  data.set(1, new PieChart.Data("Free " + readableSize(disk.getFree()) + " (" + toStringWithPercent(percentageFree) + ")", disk.getFree()));
                }
              }
              /////update value detail pane
              if (fListUsedDetail.size() == aNode.getDiskSize()) {
                fListUsedDetail.get(i).setText(readableSize(disk.getUsed()) + " (" + toStringWithPercent(percentageUsed) + ")");
                fListFreeDetail.get(i).setText(readableSize(disk.getFree()) + " (" + toStringWithPercent(percentageFree) + ")");
                fListTotalDetail.get(i).setText(readableSize(disk.getTotal()));
              }
              /////
            }
          }
        }
      }
    }
  }
}
