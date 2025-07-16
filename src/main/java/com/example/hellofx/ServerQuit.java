package com.example.hellofx;

import java.util.*;

public class ServerQuit implements Runnable {
    private final Server server;
    private boolean running;

    public ServerQuit(Server server) {
        this.server = server;
        running = true;
    }

    public void run() {
        Scanner input = new Scanner(System.in);
        while (running) {
            String line = input.nextLine();
            if (line.equals("q") || !running) {
                server.stop();
                input.close();
                return;
            }
        }
    }

    public void stopRunning() {
        running = false;
    }
}
