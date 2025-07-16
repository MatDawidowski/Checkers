package com.example.hellofx;

import javafx.scene.paint.Color;

import java.util.ArrayList;

public class PieceKing extends Piece {
    public PieceKing(Color color, int row, int col, Piece[][] pieces) {
        super(color, row, col, pieces);
    }

    void directionBeat(int x, int y, int x_copy, int y_copy, Piece[][] pieces, ArrayList<ArrayList<Point>> possibleMoves, int horizontal, int vertical, int lastHorizontal, int lastVertical) {
        if (-lastHorizontal == horizontal && -lastVertical == vertical) {
            return;
        }
        if (pieces[x + vertical][y + horizontal] != null && pieces[x + 2 * vertical][y + 2 * horizontal] == null && pieces[x + vertical][y + horizontal].getColor() == opponentColor) {
            int found = 0;
            for (ArrayList<Point> road : possibleMoves) {
                if (!(road.isEmpty())) {
                    if (road.getLast().getX() == x_copy && road.getLast().getY() == y_copy && !isPointInRoad(road, new Point(x + 2 * vertical, y + 2 * horizontal))) {
                        for (int i = 1; i <= Math.abs(x_copy - x) + 2; i++) {
                            road.add(new Point(x_copy + i * vertical, y_copy + i * horizontal));
                        }
                        found = 1;
                        break;
                    }
                }
            }
            if (found == 0 && x_copy == row && y_copy == col) {
                ArrayList<Point> n = new ArrayList<>();
                for (int i = 1; i <= Math.abs(x_copy - x) + 2; i++) {
                    n.add(new Point(x_copy + i * vertical, y_copy + i * horizontal));
                }
                possibleMoves.add(n);
                found = 1;
            }
            if (found == 1) {
                beat(x + 2 * vertical, y + 2 * horizontal, pieces, possibleMoves, vertical, horizontal);
            }
        }
    }

    void beat(int x, int y, Piece[][] pieces, ArrayList<ArrayList<Point>> possibleMoves, int vertical, int horizontal) {
        int x_copy = x;
        int y_copy = y;
        if (x <= MAX_TILE - 2) {
            x = x_copy;
            y = y_copy;
            while (y < MAX_TILE - 2 && x < MAX_TILE - 2 && pieces[x + 1][y + 1] == null) {
                x += 1;
                y += 1;
            }
            if (y <= MAX_TILE - 2 && x <= MAX_TILE - 2) {
                directionBeat(x, y, x_copy, y_copy, pieces, possibleMoves, RIGHT, DOWN, horizontal, vertical);
            }
            x = x_copy;
            y = y_copy;
            while (y > MIN_TILE + 2 && x < MAX_TILE - 2 && pieces[x + 1][y - 1] == null) {
                x += 1;
                y -= 1;
            }
            if (y >= MIN_TILE + 2 && x <= MAX_TILE - 2) {
                directionBeat(x, y, x_copy, y_copy, pieces, possibleMoves, LEFT, DOWN, horizontal, vertical);
            }
        }
        if (x >= MIN_TILE + 2) {
            x = x_copy;
            y = y_copy;
            while (y < MAX_TILE - 2 && x > MIN_TILE + 2 && pieces[x - 1][y + 1] == null) {
                x -= 1;
                y += 1;
            }
            if (y <= MAX_TILE - 2 && x >= MIN_TILE + 2) {
                directionBeat(x, y, x_copy, y_copy, pieces, possibleMoves, RIGHT, UP, horizontal, vertical);
            }
            x = x_copy;
            y = y_copy;
            while (y > MIN_TILE + 2 && x > MIN_TILE + 2 && pieces[x - 1][y - 1] == null) {
                x -= 1;
                y -= 1;
            }
            if (y >= MIN_TILE + 2 && x >= MIN_TILE + 2) {
                directionBeat(x, y, x_copy, y_copy, pieces, possibleMoves, LEFT, UP, horizontal, vertical);
            }
        }
    }

    ArrayList<Point> move(Piece[][] pieces) {
        ArrayList<Point> moves = new ArrayList<>();
        int x = row + 1;
        int y = col + 1;
        while (x <= MAX_TILE && y <= MAX_TILE && pieces[x][y] == null) {
            moves.add(new Point(x, y));
            x += 1;
            y += 1;
        }
        x = row + 1;
        y = col - 1;
        while (x <= MAX_TILE && y >= MIN_TILE && pieces[x][y] == null) {
            moves.add(new Point(x, y));
            x += 1;
            y -= 1;
        }
        x = row - 1;
        y = col - 1;
        while (x >= MIN_TILE && y >= MIN_TILE && pieces[x][y] == null) {
            moves.add(new Point(x, y));
            x -= 1;
            y -= 1;
        }
        x = row - 1;
        y = col + 1;
        while (x >= MIN_TILE && y <= MAX_TILE && pieces[x][y] == null) {
            moves.add(new Point(x, y));
            x -= 1;
            y += 1;
        }
        return moves;
    }
}
