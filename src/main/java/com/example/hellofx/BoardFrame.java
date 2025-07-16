package com.example.hellofx;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.io.Serializable;

public class BoardFrame implements Serializable {
    private Point[][] board;
    private int color; //0-white 1-black
    private boolean win = false;
    private int whiteCounter;
    private int blackCounter;
    private int whiteSeconds;
    private int blackSeconds;
    private boolean full=false;

    public void setBlackSeconds(int blackSeconds) {
        this.blackSeconds = blackSeconds;
    }

    public void setWhiteSeconds(int whiteSeconds) {
        this.whiteSeconds = whiteSeconds;
    }

    public int getBlackSeconds() {
        return blackSeconds;
    }

    public int getWhiteSeconds() {
        return whiteSeconds;
    }

    public void setPieces(Point[][] points) {
        this.board = points;
    }

    public int getTurn() {
        return color;
    }

    public Point[][] getBoard() {
        return board;
    }

    public void setTurn(int turn) {
        this.color = turn;
    }

    public boolean getWin() {
        return win;
    }

    public void setWin() {
        win = true;
    }

    public int getWhiteCounter() {
        return whiteCounter;
    }

    public int getBlackCounter() {
        return blackCounter;
    }

    public void setBlackCounter(int blackCounter) {
        this.blackCounter = blackCounter;
    }

    public void setWhiteCounter(int whiteCounter) {
        this.whiteCounter = whiteCounter;
    }

    public void setFull() {
        full = true;
    }
    public boolean getFull(){
        return full;
    }

    public BoardFrame(Point[][] pieces, int color, int whiteCounter, int blackCounter) {
        this.board = pieces;
        this.color = color;
        this.whiteCounter = whiteCounter;
        this.blackCounter = blackCounter;
    }
}
