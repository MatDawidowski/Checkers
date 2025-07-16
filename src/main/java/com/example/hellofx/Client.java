package com.example.hellofx;


import java.net.*;
import java.io.*;
import java.util.*;

public class Client {
    private String hostName;
    private final int portNumber = 2115;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Scanner input;
    private boolean running;
    private BoardFrame frame = new BoardFrame(null, -1, 0, 0);

    public Client(String ip) {
        hostName = ip;
    }

    void setIp(String ip) {
        hostName = ip;
    }

    public void start() throws IOException {
        running = true;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(hostName, portNumber), 3000); // 3s timeout
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

        } catch (ConnectException e) {
            System.out.println("Serwer odrzucił połączenie (prawdopodobnie pełny)");
            throw e;
        } catch (SocketTimeoutException e) {
            System.out.println("Timeout - serwer nie odpowiada");
            throw e;
        } catch (IOException e) {
            System.out.println("Inny błąd: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            throw e;
        }
    }

    public void sendFrame(Point[][] points, int whiteSeconds, int blackSeconds) {
        frame.setPieces(points);
        frame.setWhiteSeconds(whiteSeconds);
        frame.setBlackSeconds(blackSeconds);
        try {
            out.writeObject(frame);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void receiveFrame() {
        try {
            frame = (BoardFrame) in.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public BoardFrame getFrame() {
        return frame;
    }

    public void stopRunning() {
        running = false;
    }

    public void stop() {
        running = false;
        try {
            socket.close();
            out.close();
            in.close();
        } catch (IOException | NullPointerException e) {
            System.out.println("Closing Client Error : " + e.getMessage());
        }
    }
}
