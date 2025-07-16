package com.example.hellofx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CheckersApplication extends Application {
    public static int windowHeight = 600;
    public static int windowWidth = windowHeight * 16 / 9;
    public static final int SIZE = 8;
    public static double TILE_SIZE = (double) windowHeight / 10;
    private int fontSize = 40;

    private Rectangle[][] tiles = new Rectangle[8][8];
    private Piece[][] pieces = new Piece[8][8];
    private Color turn = Color.WHITE;

    private int whiteCounter = 12;
    private int blackCounter = 12;
    private final int whiteSecondsStatic = 300;
    private final int blackSecondsStatic = 300;
    private int whiteSeconds = whiteSecondsStatic;
    private int blackSeconds = blackSecondsStatic;

    private GridPane grid;
    private Stage stage;
    private Group game;
    private Scene startScene;
    private Scene gameScene;
    private Scene endScene;
    private Scene ipScene;
    private Timer blackTimer;
    private Timer whiteTimer;
    private EndGameListener endGame;
    private Text turnText;
    private Text multiplayerColorGUI;
    private Text whiteTimerGUI;
    private Text blackTimerGUI;
    private VBox items;
    private Text whiteInfo;
    private Text blackInfo;
    private Text endText;
    private boolean gameStarted = true;
    private boolean botGame = false;
    private boolean multiplayerGame = false;
    private Color multiplayerColor;
    private Client client;
    private BoardFrame frame;
    boolean connectClicked = false;

    private final String whiteTurn = "White Turn";
    private final String blackTurn = "Black Turn";


    public Stage getStage() {
        return stage;
    }

    public Group getGroup() {
        return game;
    }

    public Scene getEndScene() {
        return endScene;
    }

    public int getWhiteCounter() {
        return whiteCounter;
    }

    public int getBlackCounter() {
        return blackCounter;
    }

    void stageConfigure() {
        stage.setTitle("Checkers");
        stage.setMinWidth(windowWidth);
        stage.setMinHeight(windowHeight);
        stage.setOnCloseRequest(windowEvent -> {
            if (multiplayerGame) {
                client.stop();
            }
        });
    }

    void resize() {
        double size = stage.getWidth() * 0.48 / 8;
        if (stage.getHeight() * 0.80 / 8 < size) {
            size = stage.getHeight() * 0.80 / 8;
        }
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                tiles[i][j].setHeight(size);
                tiles[i][j].setWidth(size);
                if (pieces[i][j] != null) {
                    pieces[i][j].resizePiece(size / 3);
                }
            }
        }
        turnText.setX(stage.getWidth() * 2 / 3);
        turnText.setY(100);
        turnText.setFont(new Font("Verdana", fontSize * size / TILE_SIZE));
        multiplayerColorGUI.setX(stage.getWidth() * 2 / 3);
        multiplayerColorGUI.setY(50);
        multiplayerColorGUI.setFont(new Font("Verdana", fontSize * size / TILE_SIZE));
        items.setLayoutX(stage.getWidth() * 5 / 9);
        items.setLayoutY(stage.getHeight() * 2 / 7);
        whiteTimer.resize(fontSize * size / TILE_SIZE);
        blackTimer.resize(fontSize * size / TILE_SIZE);
        whiteInfo.setFont(new Font("Verdana", fontSize * size / TILE_SIZE));
        blackInfo.setFont(new Font("Verdana", fontSize * size / TILE_SIZE));
    }

    Button buttonCreate(String text) {
        Button button = new Button(text);
        button.setFont(new Font("Arial", 18));
        button.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 10;");
        return button;
    }


    void startSceneConfigure() {
        Button startButton = buttonCreate("Local GAME");
        Button multiplyerButton = buttonCreate("Multiplayer GAME");
        Button botButton = buttonCreate("Bot GAME");
        Button exitButton = buttonCreate("EXIT");
        Label label = new Label("CHECKERS");
        label.setFont(new Font("Arial", 28));
        label.setTextFill(Color.DARKBLUE);

        HBox buttonRow = new HBox(20, startButton, multiplyerButton, botButton);
        buttonRow.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20, label, buttonRow, exitButton);
        layout.setAlignment(Pos.CENTER);


        startScene = new Scene(layout, windowWidth, windowHeight, Color.GREEN);
        startButton.setOnAction(actionEvent -> {
            stage.setScene(gameScene);
            gameStarted = true;
            maxBeatCheck();
            whiteTimer.start();
            botGame = false;
        });

        botButton.setOnAction(actionEvent -> {
            stage.setScene(gameScene);
            gameStarted = true;
            maxBeatCheck();
            whiteTimer.start();
            botGame = true;
        });

        multiplyerButton.setOnAction(actionEvent -> {
            stage.setScene(ipScene);
        });

        exitButton.setOnAction(actionEvent -> {
            Platform.exit();
            System.exit(0);
        });
    }

    Point[][] boardTransformToSend(Piece[][] pieces) {
        Point[][] points = new Point[8][8];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (pieces[i][j] == null) {
                    points[i][j] = null;
                    continue;
                }
                points[i][j] = new Point(pieces[i][j].getRow(), pieces[i][j].getCol());
                if (pieces[i][j].getColor() == Color.WHITE) {
                    points[i][j].setColor(0);
                } else {
                    points[i][j].setColor(1);
                }
                if (pieces[i][j].getIsKing()) {
                    points[i][j].setKing();
                }
            }
        }
        return points;
    }

    void boardUpdate(Point[][] points) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (pieces[i][j] != null) {
                    grid.getChildren().remove(pieces[i][j].getStackPane());
                    pieces[i][j] = null;
                }
            }
        }
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (points[i][j] != null) {
                    Piece piece = new Piece(getColor(points[i][j].getColor()), points[i][j].getX(), points[i][j].getY(), pieces);
                    grid.add(piece.getStackPane(), points[i][j].getY(), points[i][j].getX());
                    pieces[points[i][j].getX()][points[i][j].getY()] = piece;
                    if (points[i][j].getIsKing()) {
                        pieceUpgradeToPieceKing(piece);
                    }
                }
            }
        }

    }

    Color getColor(int color) {
        if (color == 0) {
            return Color.WHITE;
        }
        return Color.BLACK;
    }

    void ipSceneConfigure() {
        Button connectButton = buttonCreate("Connect");
        TextField textField = new TextField();
        textField.setMaxWidth(200);
        Label label1 = new Label("Enter server ip:");
        label1.setFont(new Font("Arial", 28));
        label1.setTextFill(Color.DARKBLUE);
        Label label2 = new Label("Waiting for a player");
        label2.setFont(new Font("Arial", 40));
        label2.setTextFill(Color.RED);
        label2.setVisible(false);
        VBox layout1 = new VBox(20, label2, label1, textField, connectButton);
        layout1.setAlignment(Pos.CENTER);
        ipScene = new Scene(layout1, gameScene.getWidth(), gameScene.getHeight(), Color.GREEN);
        connectButton.setOnAction(actionEvent -> {
            if (connectClicked) {
                return;
            }
            connectClicked = true;
            client = new Client(textField.getText());
            label2.setVisible(true);
            Thread thread1 = new Thread(() -> {
                try {
                    client.start();
                    multiplayerGame = true;
                    client.receiveFrame();
                    frame = client.getFrame();
                    if (frame.getFull()){
                        endGame.winNotification("Server busy");
                        connectClicked = false;
                        label2.setVisible(false);
                        return;
                    }
                    if (frame.getWin()) {
                        String text;
                        if (multiplayerColor == Color.WHITE) {
                            text = "White Wins";
                        } else {
                            text = "Black Wins";
                        }
                        endGame.winNotification(text);
                        connectClicked = false;
                        label2.setVisible(false);
                        return;
                    }
                    multiplayerColor = getColor(frame.getTurn());
                    Platform.runLater(() -> {
                        stage.setScene(gameScene);
                        connectClicked = false;
                        label2.setVisible(false);
                        gameStarted = true;
                        whiteTimer.start();
                        if (multiplayerColor == Color.WHITE) {
                            maxBeatCheck();
                            multiplayerColorGUI.setText("Your Color: WHITE");
                            multiplayerColorGUI.setFill(Color.WHITE);
                        } else {
                            multiplayerColorGUI.setText("Your Color: BLACK");
                            multiplayerColorGUI.setFill(Color.BLACK);
                        }
                        multiplayerColorGUI.setVisible(true);
                    });
                    if (multiplayerColor == Color.BLACK) {
                        client.receiveFrame();
                        frame = client.getFrame();
                        whiteTimer.setSeconds(frame.getWhiteSeconds());
                        blackTimer.setSeconds(frame.getBlackSeconds());
                        if (frame.getFull()){
                            endGame.winNotification("Server busy");
                            connectClicked = false;
                            label2.setVisible(false);
                            return;
                        }
                        if (frame.getWin()) {
                            String text;
                            if (multiplayerColor == Color.WHITE) {
                                text = "White Wins";
                            } else {
                                text = "Black Wins";
                            }
                            endGame.winNotification(text);
                            connectClicked = false;
                            label2.setVisible(false);
                            return;
                        }
                        Platform.runLater(() -> {
                            boardUpdate(frame.getBoard());
                            turnChange();
                            maxBeatCheck();
                        });
                    }
                } catch (ConnectException ef) {
                    endGame.winNotification("Server offline");
                    connectClicked = false;
                    label2.setVisible(false);
                } catch (IOException e) {
                    endGame.winNotification("Server offline");
                    connectClicked = false;
                    label2.setVisible(false);
                } catch (RuntimeException e) {}
            });
            thread1.setDaemon(true);
            thread1.start();
        });
    }

    void gameSceneConfigure() {
        game = new Group();
        gameScene = new Scene(game, startScene.getWidth(), startScene.getHeight(), Color.GREEN);

        multiplayerColorGUI = new Text(stage.getWidth() * 2 / 3, 50, "");
        multiplayerColorGUI.setFont(new Font("Verdana", fontSize));

        turnText = new Text(stage.getWidth() * 2 / 3, 100, "White Turn");
        turnText.setFont(new Font("Verdana", fontSize));
        turnText.setFill(Color.WHITE);

        grid = new GridPane();
        grid.setLayoutX(50);
        grid.setLayoutY(50);

        board_Init();
        game.getChildren().add(grid);

    }

    void endSceneConfigure() {
        Button resetButton = buttonCreate("RESET");

        endText = new Text("");
        endText.setFont(new Font("Verdana", 80));
        endText.setFill(Color.RED);

        Label label1 = new Label("CHECKERS");
        label1.setFont(new Font("Arial", 28));
        label1.setTextFill(Color.DARKBLUE);
        VBox layout1 = new VBox(20, endText, label1, resetButton);
        layout1.setAlignment(Pos.CENTER);
        endScene = new Scene(layout1, gameScene.getWidth(), gameScene.getHeight(), Color.GREEN);
        resetButton.setOnAction(actionEvent -> {
            stage.setScene(startScene);
            gameReset();
        });
    }

    Text timerCreate(int height) {
        Text TimerGUI = new Text("");
        TimerGUI.setFont(new Font("Verdana", fontSize));
        TimerGUI.setFill(Color.WHITE);
        return TimerGUI;
    }

    void timersConfigure() {
        blackTimer = new Timer(blackSecondsStatic, blackTimerGUI);
        Thread thread1 = new Thread(blackTimer);
        thread1.start();
        whiteTimer = new Timer(whiteSecondsStatic, whiteTimerGUI);
        Thread thread2 = new Thread(whiteTimer);
        thread2.start();
    }

    void endGameListenerConfigure() {
        endGame = new EndGameListener(this, blackTimer, whiteTimer, endText);
        Thread endGameThread = new Thread(endGame);
        endGameThread.start();
    }

    void timerBox_Init() {
        Button returnButton = buttonCreate("RETURN");
        returnButton.setOnAction(actionEvent -> {
            gameReset();
            stage.setScene(startScene);
        });
        whiteInfo = new Text("White time");
        whiteInfo.setFont(new Font("Verdana", fontSize));
        whiteInfo.setFill(Color.WHITE);
        VBox whiteBox = new VBox(20, whiteInfo, whiteTimerGUI);
        whiteBox.setAlignment(Pos.CENTER);
        blackInfo = new Text("Black time");
        blackInfo.setFont(new Font("Verdana", fontSize));
        blackInfo.setFill(Color.WHITE);
        VBox blackBox = new VBox(20, blackInfo, blackTimerGUI);
        blackBox.setAlignment(Pos.CENTER);
        HBox timerBox = new HBox(20, whiteBox, blackBox);
        timerBox.setStyle("-fx-background-color: rgba(255,0,0,1); -fx-padding: 10; -fx-background-radius: 10;");
        items = new VBox(20, multiplayerColorGUI, turnText, timerBox, returnButton);
        items.setAlignment(Pos.CENTER);
        items.setLayoutX(stage.getWidth() * 4 / 7);
        items.setLayoutY(stage.getHeight() * 2 / 7);
        game.getChildren().add(items);
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;

        stageConfigure();

        stage.setOnCloseRequest((WindowEvent event) -> endGame.EndGameListenerOff());

        startSceneConfigure();

        gameSceneConfigure();

        endSceneConfigure();

        ipSceneConfigure();

        blackTimerGUI = timerCreate(300);
        game.getChildren().add(blackTimerGUI);
        whiteTimerGUI = timerCreate(500);
        game.getChildren().add(whiteTimerGUI);
        timersConfigure();

        stage.setScene(startScene);
        stage.show();

        endGameListenerConfigure();

        timerBox_Init();

        stage.widthProperty().addListener((obs, oldW, newW) -> resize());
        stage.heightProperty().addListener((obs, oldH, newH) -> resize());

    }

    public void boardReset() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (pieces[row][col] != null) {
                    grid.getChildren().remove(pieces[row][col].getStackPane());
                    pieces[row][col] = null;
                }
                if ((row + col) % 2 != 0) {
                    if (row < 3) {
                        Piece piece = new Piece(Color.BLACK, row, col, pieces);
                        grid.add(piece.getStackPane(), col, row);
                        pieces[row][col] = piece;
                    } else if (row > 4) {
                        Piece piece = new Piece(Color.WHITE, row, col, pieces);
                        grid.add(piece.getStackPane(), col, row);
                        pieces[row][col] = piece;
                    }
                }
            }
        }
    }

    public void gameReset() {
        endGame.EndGameListenerOff();
        gameStarted = false;
        if (multiplayerGame) {
            multiplayerGame = false;
            client.stop();
        }
        botGame = false;
        multiplayerColorGUI.setVisible(false);
        boardReset();
        turn = Color.WHITE;
        whiteCounter = 12;
        blackCounter = 12;

        turnText.setText(whiteTurn);
        turnText.setFill(Color.WHITE);

        timersConfigure();
        endGameListenerConfigure();
    }

    void board_Init() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Rectangle tile = new Rectangle(TILE_SIZE, TILE_SIZE);

                if ((row + col) % 2 == 0) {
                    tile.setFill(Color.BEIGE);
                } else {
                    tile.setFill(Color.BROWN);
                }
                grid.add(tile, col, row);
                tiles[row][col] = tile;
                if ((row + col) % 2 != 0) {
                    if (row < 3) {
                        Piece piece = new Piece(Color.BLACK, row, col, pieces);
                        grid.add(piece.getStackPane(), col, row);
                        pieces[row][col] = piece;
                    } else if (row > 4) {
                        Piece piece = new Piece(Color.WHITE, row, col, pieces);
                        grid.add(piece.getStackPane(), col, row);
                        pieces[row][col] = piece;
                    }
                }
            }
        }
    }

    int piecesOnRoad(ArrayList<Point> road) {
        int counter = 0;
        for (Point point : road) {
            if (pieces[point.getX()][point.getY()] != null) {
                counter++;
            }
        }
        return counter;
    }

    int maxPiecesOnRoad(ArrayList<ArrayList<Point>> roads) {
        int max_counter = 0;
        for (ArrayList<Point> road : roads) {
            int counter = 0;
            for (Point point : road) {
                if (pieces[point.getX()][point.getY()] != null) {
                    counter++;
                }
            }
            if (counter > max_counter) {
                max_counter = counter;
            }
        }
        return max_counter;
    }

    int maxBeatings() {
        int max_pices = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (pieces[i][j] != null && pieces[i][j].getColor() == turn) {
                    int piecesOnRoad = maxPiecesOnRoad(pieces[i][j].beatFind(pieces));
                    if (piecesOnRoad > max_pices) {
                        max_pices = piecesOnRoad;
                    }
                }
                if (pieces[i][j] != null) {
                    pieces[i][j].getPiece().setDisable(true);
                }
            }
        }
        return max_pices;
    }

    void maxBeatPieceSetClickable(int max_pieces) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (pieces[i][j] != null && pieces[i][j].getColor() == turn) {
                    int piecesOnRoad = maxPiecesOnRoad(pieces[i][j].beatFind(pieces));
                    if (piecesOnRoad == max_pieces) {
                        Piece pieceToClick = pieces[i][j];
                        if (!(botGame && turn == Color.BLACK)) {
                            pieceToClick.beatingOn();
                            pieceToClick.getPiece().setDisable(false);
                            pieceToClick.getPiece().setOnMouseClicked(event -> pieceAction(pieceToClick));
                        }
                    }
                }
            }
        }
    }

    int pieceWithMovesSetClickable() {
        int move_counter = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (pieces[i][j] != null && pieces[i][j].getColor() == turn) {
                    Piece pieceToClick = pieces[i][j];
                    ArrayList<Point> moves = pieceToClick.move(pieces);
                    move_counter += moves.size();
                    if (!(botGame && turn == Color.BLACK)) {
                        pieceToClick.getPiece().setDisable(false);
                        pieceToClick.getPiece().setOnMouseClicked(event -> pieceAction(pieceToClick));
                    }
                }
            }
        }
        return move_counter;
    }

    void maxBeatCheck() {
        if (!gameStarted) {
            return;
        }
        tilesReset();
        int max_pieces = maxBeatings();
        if (max_pieces > 0) {
            maxBeatPieceSetClickable(max_pieces);
        } else {
            int move_counter = pieceWithMovesSetClickable();
            if (move_counter <= 0) {
                if (turn == Color.BLACK && blackCounter > 0) {
                    endGame.whiteWinSet();
                } else if (whiteCounter > 0) {
                    endGame.blackWinSet();
                }
            }
        }
    }

    void resetColor() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (pieces[i][j] != null) {
                    pieces[i][j].beatingOff();
                }
            }
        }
    }

    void turnChange() {
        if (turn == Color.WHITE) {
            turn = Color.BLACK;
            turnText.setFill(Color.BLACK);
            turnText.setText(blackTurn);
            whiteTimer.stop();
            blackTimer.start();
        } else {
            turn = Color.WHITE;
            turnText.setFill(Color.WHITE);
            turnText.setText(whiteTurn);
            blackTimer.stop();
            whiteTimer.start();
        }
    }

    void pieceUpgradeToPieceKing(Piece piece) {
        grid.getChildren().remove(piece.getStackPane());
        PieceKing pieceKing = new PieceKing(piece.getColor(), piece.getRow(), piece.getCol(), pieces);
        pieces[piece.getRow()][piece.getCol()] = pieceKing;
        pieceKing.setKing();
        grid.add(pieceKing.getStackPane(), pieceKing.getCol(), pieceKing.getRow());
        pieceKing.getPiece().setOnMouseClicked(e -> pieceAction(pieceKing));
    }

    void removeLastPosition(Piece piece, int x, int y) {
        grid.getChildren().remove(piece.getStackPane());
        pieces[piece.getRow()][piece.getCol()] = null;
        grid.add(piece.getStackPane(), y, x);
        pieces[x][y] = piece;
        piece.setRow(x);
        piece.setCol(y);
    }

    void pieceBeatAction(Piece piece, ArrayList<Point> road) {
        tilesReset();
        for (Point point : road) {
            if (pieces[point.getX()][point.getY()] != null) {
                if (pieces[point.getX()][point.getY()].getColor() == Color.BLACK) {
                    blackCounter -= 1;
                } else {
                    whiteCounter -= 1;
                }
                grid.getChildren().remove(pieces[point.getX()][point.getY()].getStackPane());
                pieces[point.getX()][point.getY()] = null;
            }
        }
        removeLastPosition(piece, road.getLast().getX(), road.getLast().getY());
        turnChange();
        if (piece.getRow() == 0 && piece.getColor() == Color.WHITE) {
            pieceUpgradeToPieceKing(piece);
        }
        if (piece.getRow() == SIZE - 1 && piece.getColor() == Color.BLACK) {
            pieceUpgradeToPieceKing(piece);
        }
        resetColor();
        if (botGame && turn == Color.BLACK) {
            bestAction();
        } else if (multiplayerGame && !(turn == multiplayerColor)) {
            client.sendFrame(boardTransformToSend(pieces), whiteTimer.getSeconds(), blackTimer.getSeconds());
            Thread thread1 = new Thread(() -> {
                client.receiveFrame();
                frame = client.getFrame();
                whiteTimer.setSeconds(frame.getWhiteSeconds());
                blackTimer.setSeconds(frame.getBlackSeconds());
                if (frame.getWin()) {
                    String text;
                    if (multiplayerColor == Color.WHITE) {
                        text = "White Wins";
                    } else {
                        text = "Black Wins";
                    }
                    endGame.winNotification(text);
                    return;
                }
                Platform.runLater(() -> {
                    boardUpdate(frame.getBoard());
                    turnChange();
                    maxBeatCheck();
                });
            });
            thread1.setDaemon(true);
            thread1.start();
        } else {
            maxBeatCheck();
        }
        clickSwitch(0);
    }

    void pieceMoveAction(Piece piece, Point point) {
        tilesReset();
        removeLastPosition(piece, point.getX(), point.getY());
        turnChange();
        if (piece.getRow() == 0 && piece.getColor() == Color.WHITE && !piece.isKing) {
            pieceUpgradeToPieceKing(piece);
        }
        if (piece.getRow() == SIZE - 1 && piece.getColor() == Color.BLACK && !piece.isKing) {
            pieceUpgradeToPieceKing(piece);
        }
        if (botGame && turn == Color.BLACK) {
            bestAction();
        } else if (multiplayerGame && !(turn == multiplayerColor)) {
            client.sendFrame(boardTransformToSend(pieces), whiteTimer.getSeconds(), blackTimer.getSeconds());
            Thread thread1 = new Thread(() -> {
                client.receiveFrame();
                frame = client.getFrame();
                whiteTimer.setSeconds(frame.getWhiteSeconds());
                blackTimer.setSeconds(frame.getBlackSeconds());
                if (frame.getWin()) {
                    String text;
                    if (multiplayerColor == Color.WHITE) {
                        text = "White Wins";
                    } else {
                        text = "Black Wins";
                    }
                    endGame.winNotification(text);
                    return;
                }
                Platform.runLater(() -> {
                    boardUpdate(frame.getBoard());
                    turnChange();
                    maxBeatCheck();
                });
            });
            thread1.setDaemon(true);
            thread1.start();
        } else {
            maxBeatCheck();
        }
        clickSwitch(0);
    }

    void bestAction() {
        if (turn != Color.BLACK) {
            return;
        }
        HashMap<Piece, ArrayList<ArrayList<Point>>> allBeats = new HashMap<>();
        int beatsCounter = 0;
        int beats = 0;
        boolean end = false;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Piece piece = pieces[i][j];
                if (piece == null || piece.getColor() == Color.WHITE) {
                    continue;
                }
                ArrayList<ArrayList<Point>> roads = piece.beatFind(pieces);
                ArrayList<ArrayList<Point>> targetRoads = new ArrayList<>();
                for (ArrayList<Point> road : roads) {
                    beats++;
                    Piece[][] pieces_copy = new Piece[8][8];
                    for (int m = 0; m < SIZE; m++) {
                        for (int n = 0; n < SIZE; n++) {
                            pieces_copy[m][n] = pieces[m][n];
                        }
                    }
                    for (Point point : road) {
                        pieces_copy[point.getX()][point.getY()] = null;
                    }
                    pieces_copy[piece.getRow()][piece.getCol()] = null;
                    pieces_copy[road.getLast().getX()][road.getLast().getY()] = piece;
                    if (!(piece.isBeated(pieces_copy, road.getLast().getX(), road.getLast().getY()))) {
                        beatsCounter++;
                        targetRoads.add(road);
                    }
                }
                allBeats.put(piece, targetRoads);
            }
        }
        Random rand = new Random();

        int randomNumber;
        int counter = 0;
        if (beatsCounter > 0) {
            randomNumber = rand.nextInt(beatsCounter);
            for (Piece piece : allBeats.keySet()) {
                for (int i = 0; i < allBeats.get(piece).size(); i++) {
                    if (counter == randomNumber) {
                        pieceBeatAction(piece, allBeats.get(piece).get(i));
                        end = true;
                        break;
                    }
                    counter++;
                }
                if (end) {
                    break;
                }
            }
        }
        if (end) {
            return;
        }
        if (beats > 0) {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    Piece piece = pieces[i][j];
                    if (piece == null || piece.getColor() == Color.WHITE) {
                        continue;
                    }
                    ArrayList<ArrayList<Point>> roads = piece.beatFind(pieces);
                    if (!(roads.isEmpty())) {
                        pieceBeatAction(piece, roads.getFirst());
                        end = true;
                        break;
                    }
                }
                if (end) {
                    break;
                }
            }
            end = true;
        }
        if (end) {
            return;
        }
        int movesCounter = 0;
        int move = 0;
        HashMap<Piece, ArrayList<Point>> allMoves = new HashMap<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Piece piece = pieces[i][j];
                if (piece == null || piece.getColor() == Color.WHITE) {
                    continue;
                }
                ArrayList<Point> moves = piece.move(pieces);
                move += moves.size();
                ArrayList<Point> targetPoints = new ArrayList<>();
                for (Point point : moves) {
                    Piece[][] pieces_copy = new Piece[8][8];
                    for (int m = 0; m < SIZE; m++) {
                        for (int n = 0; n < SIZE; n++) {
                            pieces_copy[m][n] = pieces[m][n];
                        }
                    }
                    pieces_copy[piece.getRow()][piece.getCol()] = null;
                    pieces_copy[point.getX()][point.getY()] = piece;
                    if (!(piece.isBeated(pieces_copy, point.getX(), point.getY()))) {
                        targetPoints.add(point);
                        movesCounter++;
                    }
                }
                if (!targetPoints.isEmpty()) {
                    allMoves.put(piece, targetPoints);
                }

            }
        }
        counter = 0;
        if (movesCounter > 0) {
            randomNumber = rand.nextInt(movesCounter);
            for (Piece piece : allMoves.keySet()) {
                for (int i = 0; i < allMoves.get(piece).size(); i++) {
                    if (counter == randomNumber) {
                        pieceMoveAction(piece, allMoves.get(piece).get(i));
                        end = true;
                        break;
                    }
                    counter++;
                }
                if (end) {
                    break;
                }
            }
        }
        if (end) {
            return;
        }
        if (move == 0) {
            if (turn == Color.BLACK && blackCounter > 0) {
                endGame.whiteWinSet();
            } else if (whiteCounter > 0) {
                endGame.blackWinSet();
            }
        } else {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    Piece piece = pieces[i][j];
                    if (piece == null || piece.getColor() == Color.WHITE) {
                        continue;
                    }
                    ArrayList<Point> moves = piece.move(pieces);
                    if (!moves.isEmpty()) {
                        pieceMoveAction(piece, moves.getFirst());
                        end = true;
                        break;
                    }
                }
                if (end) {
                    break;
                }
            }
        }
    }

    void pieceAction(Piece piece) {
        if (!(turn == piece.getColor())) {
            return;
        }
        tilesReset();
        ArrayList<ArrayList<Point>> roads = piece.beatFind(pieces);
        int max = 0;
        for (ArrayList<Point> road : roads) {
            if (piecesOnRoad(road) > max) {
                max = piecesOnRoad(road);
            }
        }
        for (ArrayList<Point> road : roads) {
            if (piecesOnRoad(road) == max) {
                for (int i = 0; i < road.size() - 1; i++) {
                    if (tiles[road.get(i).getX()][road.get(i).getY()].getFill() != Color.RED) {
                        tiles[road.get(i).getX()][road.get(i).getY()].setFill(Color.PALEVIOLETRED);
                    }
                }
                tiles[road.getLast().getX()][road.getLast().getY()].setFill(Color.RED);
                if (botGame && piece.getColor() == Color.BLACK) {
                    pieceBeatAction(piece, road);
                    break;
                } else {
                    tiles[road.getLast().getX()][road.getLast().getY()].setDisable(false);
                    tiles[road.getLast().getX()][road.getLast().getY()].setOnMouseClicked(event -> pieceBeatAction(piece, road));
                }
            }
        }
        if (roads.isEmpty()) {
            ArrayList<Point> moves = piece.move(pieces);
            for (Point point : moves) {
                tiles[point.getX()][point.getY()].setFill(Color.BLUE);
                if (botGame && piece.getColor() == Color.BLACK) {
                    pieceMoveAction(piece, point);
                    break;
                } else {
                    tiles[point.getX()][point.getY()].setDisable(false);
                    tiles[point.getX()][point.getY()].setOnMouseClicked(event -> pieceMoveAction(piece, point));
                }
            }
        }
    }

    void clickSwitch(int state) {
        boolean s = false;
        if (state == 0) {
            s = true;
        }
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                tiles[i][j].setDisable(s);
            }
        }
    }

    void tilesReset() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if ((row + col) % 2 == 0) {
                    tiles[row][col].setFill(Color.BEIGE);
                } else {
                    tiles[row][col].setFill(Color.BROWN);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
