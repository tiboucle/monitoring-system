package com.monitoring.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.GridPane;

public class FxLineChart extends JFXPanel {
  protected final ObservableList<XYChart.Series> fListSeries = FXCollections.observableArrayList();
  private CategoryAxis fXAxis = new CategoryAxis();
  private NumberAxis fYAxis = new NumberAxis();
  private LineChart fLineChart = new LineChart(fXAxis, fYAxis);

  public FxLineChart(String title, boolean aZeroRange) {
    fYAxis.setAutoRanging(true);
    fYAxis.setForceZeroInRange(aZeroRange);

    fLineChart.setTitle(title);
    fLineChart.setAnimated(false);
    fLineChart.setCreateSymbols(true);
    fLineChart.setData(fListSeries);
  }

  public LineChart getLineChart() {
    return fLineChart;
  }

  public ObservableList<XYChart.Series> getListSeries() {
    return fListSeries;
  }

  public void setGridPosition(int aCol, int aRow) {
    GridPane.setColumnIndex(fLineChart, aCol);
    GridPane.setRowIndex(fLineChart, aRow);
    GridPane.setMargin(fLineChart, new Insets(5, 5, 5, 5));
  }

  public void setYAxisTickLabel(String aString) {
    fYAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(fYAxis, null, aString));
  }
}
