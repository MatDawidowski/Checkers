package com.example.hellofx;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class EndGameListener implements Runnable {
    private boolean running = true;
    private boolean whiteWin = false;
    private boolean blackWin = false;
    private CheckersApplication app;
    private Timer whiteTimer;
    private Timer blackTimer;
    private Text endText;

    public void winNotification(String winText) {
        blackTimer.timerOff();
        whiteTimer.timerOff();
        Platform.runLater(() -> {
            endText.setText(winText);
            app.getStage().setScene(app.getEndScene());
            running = false;
            app.gameReset();
        });
    }

    public EndGameListener(CheckersApplication app, Timer blackTimer, Timer whiteTimer, Text endText) {
        this.app = app;
        this.whiteTimer = whiteTimer;
        this.blackTimer = blackTimer;
        this.endText = endText;
    }

    public void EndGameListenerOff() {
        running = false;
        blackTimer.timerOff();
        whiteTimer.timerOff();
    }

    public void whiteWinSet() {
        whiteWin = true;
    }

    public void blackWinSet() {
        blackWin = true;
    }

    public void run() {
        while (running) {
            if (whiteTimer.getSeconds() <= 0 || app.getWhiteCounter() <= 0 || blackWin) {
                winNotification("Black wins");
            } else if (blackTimer.getSeconds() <= 0 || app.getBlackCounter() <= 0 || whiteWin) {
                winNotification("White Wins");
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("Thread Interrupted!");
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
