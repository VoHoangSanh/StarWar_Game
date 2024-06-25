//package game.server;
//
//import game.obj.Bullet;
//import game.obj.Enemy;
//
//import java.io.*;
//import java.net.*;
//import java.util.*;
//import java.util.concurrent.*;
//
//public class GameServer {
//    private static final int PORT = 12345;
//    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
//    private static final List<ClientHandler> clients = new ArrayList<>();
//    private static final List<Bullet> bullets = new ArrayList<>();
//    private static final List<Enemy> enemies = new ArrayList<>();
//    private static final Random random = new Random();
//
//    public static void main(String[] args) {
//        while (true) {
//            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
//                System.out.println("Game server started on port " + PORT);
//                executor.scheduleAtFixedRate(GameServer::spawnEnemy, 0, 1500, TimeUnit.MILLISECONDS);
//                executor.scheduleAtFixedRate(GameServer::updateGame, 0, 11, TimeUnit.MILLISECONDS);
//
//                while (true) {
//                    Socket socket = serverSocket.accept();
//                    ClientHandler clientHandler = new ClientHandler(socket);
//                    clients.add(clientHandler);
//                    new Thread(clientHandler).start();
//                }
//            } catch (BindException e) {
//                System.err.println("Port already in use. Retrying in 5 seconds...");
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException interruptedException) {
//                    interruptedException.printStackTrace();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private static void spawnEnemy() {
//        // Add enemy spawning logic here
//    }
//
//    private static void updateGame() {
//        // Update game logic here
//        broadcastGameState();
//    }
//
//    private static void broadcastGameState() {
//        // Serialize game state and send to all clients
//    }
//
//    private static class ClientHandler implements Runnable {
//        private Socket socket;
//        private ObjectInputStream in;
//        private ObjectOutputStream out;
//
//        public ClientHandler(Socket socket) throws IOException {
//            this.socket = socket;
//            this.in = new ObjectInputStream(socket.getInputStream());
//            this.out = new ObjectOutputStream(socket.getOutputStream());
//        }
//
//        @Override
//        public void run() {
//            try {
//                while (true) {
//                    // Handle client input
//                    Object input = in.readObject();
//                    // Update game state based on input
//                }
//            } catch (IOException | ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
