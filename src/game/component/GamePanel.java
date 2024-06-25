package game.component;

import game.obj.Bullet; 
import game.obj.SoundShot; 
import game.obj.Enemy; 
import game.obj.Explosion; 
import game.obj.Health; 


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GamePanel extends Canvas implements Runnable {
    private ScheduledExecutorService executor;
    private int playerX = 50;
    private int playerY = 50;
    private Key key;
    private BufferedImage backgroundImage;
    private BufferedImage playerImage;
    private BufferedImage playerImageDefault;
    private BufferedImage playerImageUp;
    private BufferedImage playerImageDown;
    private BufferedImage bulletImage;
    private int moveSpeed = 4; // Tốc độ di chuyển
    private int bulletSpeed = 10; // Tốc độ bay của đạn
    private int bulletOffsetY = 20; // Offset vị trí xuất phát của viên đạn
    private int bulletOffsetX = 45; // Offset vị trí xuất phát của viên đạn
    private long lastShootTime = 0; // Thời gian lần bắn cuối cùng
    private List<Bullet> bullets = new ArrayList<>(); // Danh sách các viên đạn
    private String shootSoundFile = "/game/soundFX/shot-lazer.wav"; // Đường dẫn tới tệp âm thanh
    
    
    
    private BufferedImage enemyImage;
    private BufferedImage[] explosionFrames;
    private int enemySpeed = 3; // Tốc độ di chuyển của kẻ địch
    private List<Enemy> enemies = new ArrayList<>(); // Danh sách các kẻ địch
    private List<Explosion> explosions = new ArrayList<>(); // Danh sách các hiệu ứng nổ
    private Random random = new Random();
    
//    //Health
    private List<Health> healths = new ArrayList<>(); // Danh sách các máu của người chơi
    private boolean playerHit = false; // Biến để kiểm tra người chơi bị va chạm
    private long hitStartTime; // Thời gian bắt đầu va chạm
    private int healthCount = 3; // Số lượng máu của người chơi
    private BufferedImage healthImage;
    private int healthWidth, healthHeight;
    
    private BufferedImage flashImage; // Hình ảnh hiệu ứng flash
    private boolean showFlash; // Biến để hiển thị hiệu ứng
    private long flashStartTime; // Thời gian bắt đầu hiệu ứng

    public GamePanel() {
        key = new Key();
        setPreferredSize(new Dimension(1366, 768));
        setBackground(Color.BLUE); // Đặt màu nền ở đây
        try {
            // Sử dụng đường dẫn tương đối để tải hình ảnh từ thư mục tài nguyên
            backgroundImage = ImageIO.read(getClass().getResource("/game/image/bg-preview-big.png"));
            playerImageDefault = ImageIO.read(getClass().getResource("/game/image/player1-default.png"));
            playerImageUp = ImageIO.read(getClass().getResource("/game/image/player1-up.png"));
            playerImageDown = ImageIO.read(getClass().getResource("/game/image/player1-down.png"));
            bulletImage = ImageIO.read(getClass().getResource("/game/image/shoot2.png"));
            playerImage = playerImageDefault; // Đặt hình ảnh mặc định
            enemyImage = ImageIO.read(getClass().getResource("/game/image/asteroid.png"));
            healthImage = ImageIO.read(getClass().getResource("/game/image/health.png"));
            flashImage = ImageIO.read(getClass().getResource("/game/image/flash.png"));
            explosionFrames = new BufferedImage[] {
                ImageIO.read(getClass().getResource("/game/image/hit1.png")),
                ImageIO.read(getClass().getResource("/game/image/hit2.png")),
                ImageIO.read(getClass().getResource("/game/image/hit3.png")),
                ImageIO.read(getClass().getResource("/game/image/hit4.png"))
            };
            
             // Kích thước của máu
            healthWidth = healthImage.getWidth() / 22;
            healthHeight = healthImage.getHeight() / 22;
            int spacing = -5;

//            // Tạo các đối tượng máu của người chơi
           // Tạo các đối tượng máu của người chơi
            for (int i = 0; i < healthCount; i++) {
                healths.add(new Health(i * (healthWidth + spacing), -healthHeight - 1, healthImage, healthWidth, healthHeight, spacing));
            }
        } catch (IOException e) {
            e.printStackTrace();
            
        }
        showFlash = false;
        flashStartTime = 0;

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_W:
                        key.setKey_up(true);
                        playerImage = playerImageUp; // Đổi hình ảnh khi nhấn W
                        break;
                    case KeyEvent.VK_S:
                        key.setKey_down(true);
                        playerImage = playerImageDown; // Đổi hình ảnh khi nhấn S
                        break;
                    case KeyEvent.VK_A:
                        key.setKey_left(true);
                        break;
                    case KeyEvent.VK_D:
                        key.setKey_right(true);
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_W:
                        key.setKey_up(false);
                        playerImage = playerImageDefault; // Trả về hình ảnh mặc định khi thả W
                        break;
                    case KeyEvent.VK_S:
                        key.setKey_down(false);
                        playerImage = playerImageDefault; // Trả về hình ảnh mặc định khi thả S
                        break;
                    case KeyEvent.VK_A:
                        key.setKey_left(false);
                        break;
                    case KeyEvent.VK_D:
                        key.setKey_right(false);
                        break;
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    key.setKey_shoot(true);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    key.setKey_shoot(false);
                }
            }
        });

        setFocusable(true);
    }

    public void start() {
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(this::spawnEnemy, 0, 1500, TimeUnit.MILLISECONDS); // Tạo kẻ địch mới mỗi 2 giây
        executor.scheduleAtFixedRate(this, 0, 11, TimeUnit.MILLISECONDS); // ~90 FPS
        
    }

    public void stop() {
        executor.shutdown();
    }

    @Override
    public void run() {
        updateGame();
        render();
    }

    private void updateGame() {
        // Cập nhật logic trò chơi ở đây
        if (key.isKey_up()) {
            playerY -= moveSpeed;
        }
        if (key.isKey_down()) {
            playerY += moveSpeed;
        }
        if (key.isKey_left()) {
            playerX -= moveSpeed;
        }
        if (key.isKey_right()) {
            playerX += moveSpeed;
        }

        // Giới hạn người chơi không được di chuyển ra khỏi khung hình
        int playerWidth = playerImage != null ? playerImage.getWidth() * 2 : 50;
        int playerHeight = playerImage != null ? playerImage.getHeight() * 2 : 50;

        if (playerX < 0) {
            playerX = 0;
        }
        if (playerX + playerWidth > getWidth()) {
            playerX = getWidth() - playerWidth;
        }
        if (playerY < 0) {
            playerY = 0;
        }
        if (playerY + playerHeight > getHeight()) {
            playerY = getHeight() - playerHeight;
        }

        // Xử lý logic bắn đạn
        if (key.isKey_shoot()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShootTime >= 350) {
                bullets.add(new Bullet(playerX + playerWidth / 2 - bulletImage.getWidth() / 2 + bulletOffsetX, playerY + bulletOffsetY, bulletSpeed, bulletImage, flashImage));
                SoundShot.playShotSound(); // Phát âm thanh khi bắn
                lastShootTime = currentTime;
                showFlash = true;
                flashStartTime = currentTime;
            }
        }

        // Cập nhật vị trí của các viên đạn
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            bullet.update();
            if (bullet.isOffScreen(getHeight())) {
                bullets.remove(i);
                i--;
            }
        }
        // Kiểm tra và cập nhật hiệu ứng flash
        if (showFlash && System.currentTimeMillis() - flashStartTime >= 200) {
            showFlash = false;
        }
        // Cập nhật vị trí của các kẻ địch
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            enemy.update();
            if (enemy.isOffScreen()) {
                enemies.remove(i);
                i--;
            }
        }
        // Cập nhật các hiệu ứng nổ
        for (int i = 0; i < explosions.size(); i++) {
            Explosion explosion = explosions.get(i);
            explosion.update();
            if (explosion.isFinished()) {
                explosions.remove(i);
                i--;
            }
        }
        // Kiểm tra va chạm giữa Bullet và Enemy
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            Rectangle bulletHitbox = new Rectangle(bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight());
            for (int j = 0; j < enemies.size(); j++) {
                Enemy enemy = enemies.get(j);
                Rectangle enemyHitbox = new Rectangle(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());
                if (bulletHitbox.intersects(enemyHitbox)) {
                    // Tạo hiệu ứng nổ
                    explosions.add(new Explosion(enemy.getX(), enemy.getY(), explosionFrames, 750 / explosionFrames.length));
                    // Phát âm thanh khi bắn trúng
                    
                    // Loại bỏ Bullet và Enemy
                    bullets.remove(i);
                    enemies.remove(j);
                    i--;
                    break;
                }
            }
        }
//        // Kiểm tra va chạm giữa Player và Enemy
        Rectangle playerHitbox = new Rectangle(playerX, playerY, playerImage.getWidth() * 3, playerImage.getHeight() * 3);
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            Rectangle enemyHitbox = new Rectangle(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());
            if (playerHitbox.intersects(enemyHitbox)) {
                // Bắt đầu hiệu ứng nhấp nháy
                SoundShot.playExplosionSound();
                playerHit = true;
                hitStartTime = System.currentTimeMillis();
                enemies.remove(i);
                i--;
                break;
            }
        }

      // Xử lý hiệu ứng nhấp nháy khi người chơi bị va chạm
        if (playerHit) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - hitStartTime >= 750) {
                playerHit = false;
                if (healths.size() > 0) {
                    healths.remove(healths.size() - 1); // Loại bỏ một máu của người chơi
                }
            }
        }
        
    }
    
    private void spawnEnemy() {
        int x = getWidth(); // Vị trí xuất phát theo trục X (bên phải màn hình)
        int y = random.nextInt(getHeight() - enemyImage.getHeight() * 2); // Vị trí xuất phát ngẫu nhiên theo trục Y
        enemies.add(new Enemy(x, y, enemySpeed, enemyImage));
    }

    private void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        // Vẽ các đối tượng trò chơi ở đây
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
        if (playerImage != null) {
            int playerWidth = playerImage.getWidth() * 3; // Phóng to gấp đôi chiều rộng
            int playerHeight = playerImage.getHeight() * 3; // Phóng to gấp đôi chiều cao
            g.drawImage(playerImage, playerX, playerY, playerWidth, playerHeight, null); // Vẽ người chơi với kích thước mới
        } else {
            g.setColor(Color.WHITE);
            g.fillRect(playerX, playerY, 50, 50); // Vẽ người chơi nếu hình ảnh không tải được
        }
        

        // Vẽ các viên đạn
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }
        
        // Vẽ các kẻ địch
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }
        
        // Vẽ các hiệu ứng nổ
        for (Explosion explosion : explosions) {
            explosion.draw(g);
        }
        
        // Vẽ máu của người chơi bám theo người chơi
        for (Health health : healths) {
            health.draw(g, playerX, playerY);
        }
      // Hiển thị hiệu ứng flash khi bắn đạn
        if (showFlash && System.currentTimeMillis() - flashStartTime < 500) { // Thời gian hiển thị là 0.5 giây (500ms)
            int playerWidth = playerImage != null ? playerImage.getWidth() * 3 : 50; // Cập nhật giá trị cho playerWidth

            // Tăng kích thước của hiệu ứng "flash.png"
            int flashWidth = flashImage.getWidth() + 14; // Nhân đôi chiều rộng
            int flashHeight = flashImage.getHeight() +14; // Nhân đôi chiều cao

            int flashX = playerX + playerWidth / 2 - flashWidth / 2+ bulletOffsetX;
            int flashY = playerY + bulletOffsetY - 5; // Điều chỉnh vị trí Y lên phía trên 20 đơn vị

            g.drawImage(flashImage, flashX, flashY, flashWidth, flashHeight, null); // Vẽ hiệu ứng "flash.png" với kích thước tăng lên
        }
       
        

        g.dispose();
        bs.show();
    }
}