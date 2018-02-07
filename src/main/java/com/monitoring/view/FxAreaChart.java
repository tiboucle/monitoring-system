package com.monitoring.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.GridPane;

public class FxAreaChart {
  protected final ObservableList<XYChart.Series> fListSeries = FXCollections.observableArrayList();
  private CategoryAxis fXAxis = new CategoryAxis();
  private NumberAxis fYAxis = new NumberAxis(0, 10, 1);
  private AreaChart fAreaChart = new AreaChart(fXAxis, fYAxis);

  public AreaChart getAreaChart() {
    return fAreaChart;
  }

  public ObservableList<XYChart.Series> getListSeries() {
    return fListSeries;
  }

  public void setGridPosition(int aCol, int aRow) {
    GridPane.setColumnIndex(fAreaChart, aCol);
    GridPane.setRowIndex(fAreaChart, aRow);
    GridPane.setMargin(fAreaChart, new Insets(5, 5, 5, 5));
  }

  public FxAreaChart(String aTitle, boolean aZeroRange) {
    fYAxis.setForceZeroInRange(aZeroRange);
    fYAxis.setAutoRanging(true);

    fAreaChart.setTitle(aTitle);
    fAreaChart.setAnimated(false);
    fAreaChart.setCreateSymbols(true);
    fAreaChart.setData(fListSeries);
  }

  public void setYAxisTickLabel(String aString) {
    fYAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(fYAxis, null, aString));
  }
}
