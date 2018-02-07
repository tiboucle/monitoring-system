package com.monitoring.view;

import java.awt.Component;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class FxDetailPane extends GridPane{
  private final String fStyle = "-fx-background-color: #eeeeee;";

  public void addHeaderTitle(Node aChild, int col, int row) {
    add(aChild, col, row);
  }

  public void setGridPosition(int col, int row) {
    GridPane.setColumnIndex(this, col);
    GridPane.setRowIndex(this, row);
    GridPane.setMargin(this, new Insets(5, 5, 5, 5));
  }

  public FxDetailPane(int aColumn, boolean aFixed) {
    setStyle(fStyle);
    setAlignment(Pos.TOP_CENTER);
    setVgap(5);
    //setGridLinesVisible(true);
    setPadding(new Insets(5, 5, 5, 5));
    if (aFixed) {
      //setMaxHeight(200);
      //setMinHeight(200);
      //setPrefHeight(200);
      setMaxWidth(800);
      setMinWidth(600);
      setPrefWidth(600);
    }

    ColumnConstraints cv = new ColumnConstraints();
    cv.setHgrow(Priority.ALWAYS);
    cv.setFillWidth(true);
    cv.setHalignment(HPos.CENTER);
    ColumnConstraints cc = new ColumnConstraints();
    cc.setHgrow(Priority.ALWAYS);
    cc.setFillWidth(true);
    cc.setHalignment(HPos.CENTER);
    //cc.setPrefWidth(50);
    //cc.setMinWidth(50);

    RowConstraints rc = new RowConstraints();
    rc.setValignment(VPos.CENTER);
    RowConstraints rc2 = new RowConstraints();
    rc2.setValignment(VPos.TOP);

    for (int i = 0; i < aColumn; i++) {
      getColumnConstraints().add(i, cc);
    }
    //getColumnConstraints().addAll(cv, cc, cc, cc);
    getRowConstraints().addAll(rc, rc2);
  }
}
