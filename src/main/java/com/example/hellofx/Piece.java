package com.example.hellofx;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.nio.file.StandardWatchEventKinds;
import java.util.ArrayList;


public class Piece {
    protected int row;
    protected int col;
    protected StackPane pieceTile;
    protected Circle piece;
    protected boolean isKing;
    protected Color color;
    protected boolean beating;
    protected Color opponentColor;
    protected Piece[][] pieces;
    protected int multiplier;
    protected ImageView crown;
    protected static final double PIECE_SIZE = CheckersApplication.TILE_SIZE / 3;
    protected static final int MIN_TILE = 0;
    protected static final int MAX_TILE = 7;

    protected static final int LEFT = -1;
    protected static final int RIGHT = 1;
    protected static final int UP = -1;
    protected static final int DOWN = 1;

    void beatingOn() {
        piece.setFill(Color.YELLOWGREEN);
        beating = true;
    }

    void beatingOff() {
        piece.setFill(color);
        beating = false;
    }

    void setRow(int x) {
        row = x;
    }

    void setCol(int y) {
        col = y;
    }

    int getRow() {
        return row;
    }

    int getCol() {
        return col;
    }

    public Piece(Color color, int row, int col, Piece[][] pieces) {
        pieceTile = new StackPane();
        piece = new Circle(PIECE_SIZE);
        piece.setFill(color);
        pieceTile.getChildren().add(piece);
        isKing = false;
        this.color = color;
        beating = false;
        if (color == Color.WHITE) {
            opponentColor = Color.BLACK;
            multiplier = -1;
        } else {
            opponentColor = Color.WHITE;
            multiplier = 1;
        }
        this.row = row;
        this.col = col;
        this.pieces = pieces;
    }

    boolean getIsKing() {
        return isKing;
    }

    Color getColor() {
        return color;
    }

    void setKing() {
        crown = new ImageView(new Image("king.png"));
        crown.setFitHeight(40);
        crown.setFitWidth(40);
        crown.setMouseTransparent(true);
        pieceTile.getChildren().add(crown);
        isKing = true;
    }

    void resizePiece(double size) {
        piece.setRadius(size);
        if (crown != null) {
            crown.setFitHeight(size * 2);
            crown.setFitWidth(size * 2);
        }
    }

    boolean isPieceOnTile(Point point, Piece[][] pieces) {
        if (pieces[point.getX()][point.getY()] == null) {
            return false;
        }
        return true;
    }

    boolean isBeated(Piece[][] pieces, int x, int y) {
        for (int i = 0; i < CheckersApplication.SIZE; i++) {
            for (int j = 0; j < CheckersApplication.SIZE; j++) {
                Piece piece = pieces[i][j];
                if (piece == null || piece.getColor() == color) {
                    continue;
                }
                ArrayList<ArrayList<Point>> roads = piece.beatFind(pieces);
                for (ArrayList<Point> road : roads) {
                    for (Point point : road) {
                        if (x > MIN_TILE) {
                            if (y > MIN_TILE) {
                                if (point.getX() == x - 1 && point.getY() == y - 1) {
                                    return true;
                                }
                            }
                            if (y < MAX_TILE) {
                                if (point.getX() == x - 1 && point.getY() == y + 1) {
                                    return true;
                                }
                            }
                        }
                        if (x < MAX_TILE) {
                            if (y > MIN_TILE) {
                                if (point.getX() == x + 1 && point.getY() == y - 1) {
                                    return true;
                                }
                            }
                            if (y < MAX_TILE) {
                                if (point.getX() == x + 1 && point.getY() == y + 1) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    void setStackPane(StackPane s) {
        pieceTile = s;
    }

    StackPane getStackPane() {
        return pieceTile;
    }

    Circle getPiece() {
        return piece;
    }

    ArrayList<ArrayList<Point>> beatFind(Piece[][] pieces) {
        ArrayList<ArrayList<Point>> roads = new ArrayList<>();
        beat(row, col, pieces, roads, 0, 0);
        int max = 0;
        for (ArrayList<Point> road : roads) {
            int onRoad = piecesOnRoad(road, pieces);
            if (onRoad > max) {
                max = onRoad;
            }
        }
        int i = 0;
        while (true) {
            if (i >= roads.size()) {
                break;
            }
            if (piecesOnRoad(roads.get(i), pieces) != max) {
                roads.remove(i);
                i--;
            }
            i++;
        }
        return roads;
    }

    int piecesOnRoad(ArrayList<Point> road, Piece[][] pieces) {
        int counter = 0;
        for (Point point : road) {
            if (pieces[point.getX()][point.getY()] != null) {
                counter++;
            }
        }
        return counter;
    }

    boolean isPointInRoad(ArrayList<Point> road, Point point) {
        for (Point p : road) {
            if (p.getX() == point.getX() && p.getY() == point.getY()) {
                return true;
            }
        }
        return false;
    }

    void directionBeat(int x, int y, Piece[][] pieces, ArrayList<ArrayList<Point>> possibleMoves, int horizontal, int vertical, int lastHorizontal, int lastVertical) {
        if (-lastHorizontal == horizontal && -lastVertical == vertical) {
            return;
        }
        if (pieces[x + vertical][y + horizontal] != null && pieces[x + 2 * vertical][y + 2 * horizontal] == null && pieces[x + vertical][y + horizontal].getColor() == opponentColor) {
            int found = 0;
            for (ArrayList<Point> road : possibleMoves) {
                if (!(road.isEmpty())) {
                    if (road.getLast().getX() == x && road.getLast().getY() == y && !isPointInRoad(road, new Point(x + 2 * vertical, y + 2 * horizontal))) {
                        ArrayList<Point> newRoad = new ArrayList<>(road);
                        possibleMoves.add(newRoad);
                        road.add(new Point(x + vertical, y + horizontal));
                        road.add(new Point(x + 2 * vertical, y + 2 * horizontal));
                        found = 1;
                        break;
                    }
                }
            }
            if (found == 0 && x == row && y == col) {
                ArrayList<Point> n = new ArrayList<>();
                n.add(new Point(x + vertical, y + horizontal));
                n.add(new Point(x + 2 * vertical, y + 2 * horizontal));
                possibleMoves.add(n);
                found = 1;
            }
            if (found == 1) {
                beat(x + 2 * vertical, y + 2 * horizontal, pieces, possibleMoves, vertical, horizontal);
            }
        }
    }

    void beat(int x, int y, Piece[][] pieces, ArrayList<ArrayList<Point>> possibleMoves, int vertical, int horizontal) {
        if (x <= MAX_TILE - 2) {
            if (y <= MAX_TILE - 2) {
                directionBeat(x, y, pieces, possibleMoves, RIGHT, DOWN, horizontal, vertical);
            }
            if (y >= MIN_TILE + 2) {
                directionBeat(x, y, pieces, possibleMoves, LEFT, DOWN, horizontal, vertical);
            }
        }
        if (x >= MIN_TILE + 2) {
            if (y <= MAX_TILE - 2) {
                directionBeat(x, y, pieces, possibleMoves, RIGHT, UP, horizontal, vertical);
            }
            if (y >= MIN_TILE + 2) {
                directionBeat(x, y, pieces, possibleMoves, LEFT, UP, horizontal, vertical);
            }
        }
    }


    void directionMove(int vertical, ArrayList<Point> moves) {
        if (col < MAX_TILE) {
            if (pieces[row + vertical][col + 1] == null) {
                moves.add(new Point(row + vertical, col + 1));
            }
        }
        if (col > MIN_TILE) {
            if (pieces[row + vertical][col - 1] == null) {
                moves.add(new Point(row + vertical, col - 1));
            }
        }
    }

    ArrayList<Point> move(Piece[][] pieces) {
        ArrayList<Point> moves = new ArrayList<>();
        if (color == Color.BLACK) {
            if (row < MAX_TILE) {
                directionMove(DOWN, moves);
            }
        } else {
            if (row > MIN_TILE) {
                directionMove(UP, moves);
            }
        }
        return moves;
    }
}

