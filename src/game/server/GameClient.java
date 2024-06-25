///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package game.server;
//
//import game.component.GamePanel;
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.*;
//import java.io.*;
//import java.net.*;
//
//public class GameClient extends JFrame {
//    private GamePanel gamePanel;
//    private Socket socket;
//    private ObjectOutputStream out;
//    private ObjectInputStream in;
//
//    public GameClient(String serverAddress, int serverPort) throws IOException {
//        socket = new Socket(serverAddress, serverPort);
//        out = new ObjectOutputStream(socket.getOutputStream());
//        in = new ObjectInputStream(socket.getInputStream());
//
//        gamePanel = new GamePanel(out);
//        add(gamePanel);
//        setTitle("Game Client");
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        pack();
//        setVisible(true);
//
//        new Thread(new ServerListener()).start();
//    }
//
//    private class ServerListener implements Runnable {
//        @Override
//        public void run() {
//            try {
//                while (true) {
//                    // Receive game state updates from the server
//                    Object state = in.readObject();
//                    // Update game panel based on received state
//                }
//            } catch (IOException | ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public static void main(String[] args) {
//        try {
//            new GameClient("localhost", 12345);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
