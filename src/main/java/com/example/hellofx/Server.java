package com.example.hellofx;

import javafx.scene.paint.Color;

import java.net.*;
import java.io.*;

public class Server {
    private final int portNumber = 2115;
    private ServerSocket serverSocket;
    private Socket clientSocket1;
    private Socket clientSocket2;
    private boolean running;
    private ServerQuit quit;
    private String turn = "White";
    private BoardFrame frame;
    private BoardFrame deny_frame=new BoardFrame(null, 0, 0, 0);

    public void changeTurn() {
        if (turn.equals("White")) {
            turn = "Black";
        } else {
            turn = "White";
        }
    }

    static public void main(String[] args) {
        Server server = new Server();
        ServerQuit quit = new ServerQuit(server);
        Thread thread = new Thread(quit);
        thread.start();
        server.setServerQuit(quit);
        server.start();
    }

    public void setServerQuit(ServerQuit quit) {
        this.quit = quit;
    }

    public void start() {
        deny_frame.setWin();
        running = true;
        System.out.println("Server started...(Press q to quit)");
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (running) {
            try {
                clientSocket1 = serverSocket.accept();
                System.out.println("Connected with client on port " + portNumber);
                clientSocket2 = serverSocket.accept();
                System.out.println("Connected with client on port " + portNumber);
                ObjectOutputStream out1 = new ObjectOutputStream(clientSocket1.getOutputStream());
                out1.flush();
                ObjectInputStream in1 = new ObjectInputStream(clientSocket1.getInputStream());
                ObjectOutputStream out2 = new ObjectOutputStream(clientSocket2.getOutputStream());
                out2.flush();
                ObjectInputStream in2 = new ObjectInputStream(clientSocket2.getInputStream());
                BoardFrame frame = new BoardFrame(null, 0, 0, 0);
                out1.writeObject(frame);
                out1.flush();
                frame.setTurn(1);
                out2.writeObject(frame);
                out2.flush();
                ClientDeny deny=new ClientDeny(serverSocket);
                Thread thread = new Thread(deny);
                thread.start();
                while (running) {
                    try {
                        frame = (BoardFrame) in1.readObject();
                        out2.writeObject(frame);
                        out2.flush();
                        frame = (BoardFrame) in2.readObject();
                        out1.writeObject(frame);
                        out1.flush();
                    } catch (IOException | ClassNotFoundException e) {
                        try {
                            out1.writeObject(deny_frame);
                            out1.flush();
                        } catch (IOException d) {
                        }
                        try {
                            out2.writeObject(deny_frame);
                            out2.flush();
                        } catch (IOException d) {
                        }
                        break;
                    }
                }
                deny.off();
            } catch (BindException e) {
                System.err.println("Port " + portNumber + " not available : " + e.getMessage());
                stop();
            } catch (IOException e) {
                if (running) {
                    System.err.println("Accepting client Error : " + e.getMessage());
                }
            }
        }

    }

    public void stop() {
        running = false;
        quit.stopRunning();
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.exit(1);
        }
    }
}