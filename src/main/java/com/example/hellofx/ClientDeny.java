package com.example.hellofx;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class ClientDeny implements Runnable {
    private boolean running = true;
    private ServerSocket serverSocket;

    public ClientDeny(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void run() {
        BoardFrame frame = new BoardFrame(null, 0, 0, 0);
        frame.setFull();
        try {
            serverSocket.setSoTimeout(1000);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        while (running) {
            Socket client = null;
            try {
                client = serverSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                out.flush();
                out.writeObject(frame);
                out.flush();
            } catch (SocketTimeoutException e){
            } catch (IOException e) {}
        }
        try {
            serverSocket.setSoTimeout(0);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    void off() {
        running = false;
    }
}
