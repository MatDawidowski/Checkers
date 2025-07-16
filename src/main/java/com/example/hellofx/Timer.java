package com.example.hellofx;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Timer implements Runnable {
    private int seconds;
    private boolean running = false;
    private boolean end = false;
    Text timerGUI;

    public Timer(int seconds, Text timerGUI) {
        this.seconds = seconds;
        this.timerGUI = timerGUI;
        timerGUI.setVisible(true);
    }

    void timerOff() {
        running = false;
        end = true;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    int getSeconds() {
        return seconds;
    }

    void stop() {
        running = false;
    }

    void start() {
        running = true;
    }

    void resize(double size) {
        timerGUI.setFont(new Font("Verdana", size));
    }

    public void run() {
        while (true) {
            if (end) {
                break;
            }
            int min = seconds / 60;
            int sec = seconds % 60;
            timerGUI.setText(String.format("%02d", min) + ":" + String.format("%02d", sec));
            if (seconds <= 0) {
                end = true;
            }
            try {
                int con = 0;
                for (int i = 0; i < 10; i++) {
                    if (!running) {
                        con = 1;
                        break;
                    }
                    Thread.sleep(100);
                }
                if (con == 1) {
                    continue;
                }
            } catch (InterruptedException e) {
                System.out.println("Thread Interrupted!");
                Thread.currentThread().interrupt();
            }
            seconds--;
        }
    }
}
