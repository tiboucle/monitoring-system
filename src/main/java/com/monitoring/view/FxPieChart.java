package com.monitoring.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.GridPane;

public class FxPieChart extends JFXPanel {
  protected final ObservableList<PieChart.Data> fListData = FXCollections.observableArrayList();
  private PieChart fPieChart;

  public PieChart getPieChart() {
    return fPieChart;
  }

  public ObservableList<PieChart.Data> getListData() {
    return fListData;
  }

  public void setGridPosition(int aCol, int aRow) {
    GridPane.setColumnIndex(fPieChart, aCol);
    GridPane.setRowIndex(fPieChart, aRow);
    GridPane.setMargin(fPieChart, new Insets(5, 5, 5, 5));
  }

  public FxPieChart(String aTitle) {
    fPieChart = new PieChart();
    fPieChart.setTitle(aTitle);
    fPieChart.setLabelsVisible(false);
    fPieChart.setPrefWidth(200);
    fPieChart.setPrefHeight(200);
    fPieChart.setData(fListData);
  }
}
