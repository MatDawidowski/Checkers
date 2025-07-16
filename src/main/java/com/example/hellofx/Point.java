package com.example.hellofx;

import java.io.Serializable;

public class Point implements Serializable {
    private int x;
    private int y;
    private int color;
    private boolean isKing;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setKing() {
        isKing = true;
    }

    public boolean getIsKing() {
        return isKing;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
