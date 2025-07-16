package com.example.hellofx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;


public class GameController {
    @FXML
    private GridPane boardGrid;

    private Button[][] fields = new Button[8][8];

    @FXML
    public void initialize() {
        int rows = 8;
        int cols = 8;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Button square = new Button();
                square.setPrefSize(64, 64);

                String color = (row + col) % 2 == 0 ? "#f0d9b5" : "#a26b44";
                square.setStyle("-fx-background-color: " + color + ";");

                boardGrid.add(square, col, row);
                fields[row][col]=square;
            }
        }
    }

}