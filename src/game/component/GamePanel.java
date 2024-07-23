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
import java.io.InputStream;
import java.io.IOException;
import java.awt.FontFormatException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GamePanel extends Canvas implements Runnable {
    
    // dan to 
    private BufferedImage bigBulletImage;
    private int bigBulletSpeed = 8; // Tốc độ đạn to
    private long lastBigShootTime = 0; // Thời gian bắn đạn to cuối cùng
    private int bigBulletCooldown = 1000; // Thời gian chờ giữa các lần bắn đạn to (1 giây)
    
     private int score = 0;
     private int highScore = 0;
    //Trạng thái
    private ScheduledExecutorService executor;
    private int playerX = 50;
    private int playerY = 50;
    private Key key;
    private int moveSpeed = 4; // Tốc độ di chuyển
    private int bulletSpeed = 10; // Tốc độ bay của đạn
    private int bulletOffsetY = 20; // Offset vị trí xuất phát của viên đạn
    private int bulletOffsetX = 45; // Offset vị trí xuất phát của viên đạn
    private long lastShootTime = 0; // Thời gian lần bắn cuối cùng
    private int enemySpeed = 3; // Tốc độ di chuyển của kẻ địch
    private boolean showFlash; // Biến để hiển thị hiệu ứng
    private long flashStartTime; // Thời gian bắt đầu hiệu ứng
    private int spacing = -5;
    private boolean playerHit = false; // Biến để kiểm tra người chơi bị va chạm
    private long hitStartTime; // Thời gian bắt đầu va chạm
    private int healthCount = 3; // Số lượng máu của người chơi
    private Font upheavalFont;
    
    // Game objects
    private List<Bullet> bullets = new ArrayList<>(); // Danh sách các viên đạn
    private List<Enemy> enemies = new ArrayList<>(); // Danh sách các kẻ địch
    private List<Explosion> explosions = new ArrayList<>(); // Danh sách các hiệu ứng nổ
    private List<Health> healths = new ArrayList<>(); // Danh sách các máu của người chơi
    
    //Hình ảnh
    private BufferedImage backgroundImage;
    private BufferedImage playerImage;
    private BufferedImage playerImageDefault;
    private BufferedImage playerImageUp;
    private BufferedImage playerImageDown;
    private BufferedImage bulletImage;
    private BufferedImage gameOverImage;
    private BufferedImage enemyImage;
    private BufferedImage[] explosionFrames;
    private BufferedImage[] explosionPlayerFrames;
    private BufferedImage healthImage;
    private BufferedImage flashImage;
    
    private Random random = new Random();
    
    
    private int yesX, noX, messageY;
    private int healthWidth, healthHeight;
    
    
   
    

    public GamePanel() {
        key = new Key();
        setPreferredSize(new Dimension(1366, 768));
        setBackground(Color.BLUE);
        try {
             // Tải font chữ từ file
            // Tải font chữ từ file
            InputStream fontStream = getClass().getResourceAsStream("/game/font/upheavtt.ttf");
            try {
                upheavalFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(Font.PLAIN, 24);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(upheavalFont);
            } catch (FontFormatException e) {
                e.printStackTrace();
            }
            
            backgroundImage = ImageIO.read(getClass().getResource("/game/image/bg-preview-big.png"));
            playerImageDefault = ImageIO.read(getClass().getResource("/game/image/player1-default.png"));
            playerImageUp = ImageIO.read(getClass().getResource("/game/image/player1-up.png"));
            playerImageDown = ImageIO.read(getClass().getResource("/game/image/player1-down.png"));
            bulletImage = ImageIO.read(getClass().getResource("/game/image/shoot2.png"));
            bigBulletImage = ImageIO.read(getClass().getResource("/game/image/big_bullet.png"));
            playerImage = playerImageDefault;
            enemyImage = ImageIO.read(getClass().getResource("/game/image/asteroid.png"));
            healthImage = ImageIO.read(getClass().getResource("/game/image/health.png"));
            flashImage = ImageIO.read(getClass().getResource("/game/image/flash.png"));
           
            explosionFrames = new BufferedImage[] {
                ImageIO.read(getClass().getResource("/game/image/hit1.png")),
                ImageIO.read(getClass().getResource("/game/image/hit2.png")),
                ImageIO.read(getClass().getResource("/game/image/hit3.png")),
                ImageIO.read(getClass().getResource("/game/image/hit4.png"))
            };
            
             explosionPlayerFrames = new BufferedImage[] {
                ImageIO.read(getClass().getResource("/game/image/explosion1.png")),
                ImageIO.read(getClass().getResource("/game/image/explosion2.png")),
                ImageIO.read(getClass().getResource("/game/image/explosion3.png")),
                ImageIO.read(getClass().getResource("/game/image/explosion4.png")),
                ImageIO.read(getClass().getResource("/game/image/explosion5.png"))
                
            };
            
            
             // Kích thước của máu
            healthWidth = healthImage.getWidth() / 22;
            healthHeight = healthImage.getHeight() / 22;
           
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
                }else if (SwingUtilities.isRightMouseButton(e)) {
                    key.setKey_bigShoot(true);
                 }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    key.setKey_shoot(false);
                }else if (SwingUtilities.isRightMouseButton(e)) {
                    key.setKey_bigShoot(false);
                }
            }
            @Override
        
            public void mouseClicked(MouseEvent e) {
                if (healths.size() <= 0) { // Chỉ xử lý khi đang ở màn hình Game Over
                    int x = e.getX();
                    int y = e.getY();

                    // Cập nhật điều kiện kiểm tra cho phù hợp với vị trí và kích thước thực tế của "Có" và "Không"
                    if (x >= yesX && x <= yesX + 50 && y >= messageY + 30 && y <= messageY + 80) {
                        // Người chơi chọn "Có"
                        restartGame();
                    } else if (x >= noX && x <= noX + 50 && y >= messageY + 30 && y <= messageY + 80) {
                        // Người chơi chọn "Không"
                        System.exit(0);
                    }
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
    private void restartGame() {
    // Khởi tạo lại các biến và danh sách
       score = 0;
    healths = new ArrayList<>();
    for (int i = 0; i < healthCount; i++) {
        healths.add(new Health(i * (healthWidth + spacing), -healthHeight - 1, healthImage, healthWidth, healthHeight, spacing));
    }
    enemies.clear();
    bullets.clear();
    explosions.clear();
    playerX = 50;
    playerY = 50;
    // Có thể cần thêm các lệnh khởi tạo khác
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
        
        // Xử lý logic bắn đạn to
        if (key.isKey_bigShoot()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastBigShootTime >= bigBulletCooldown) {
                bullets.add(new Bullet(playerX + playerWidth / 2 - bigBulletImage.getWidth() / 2 + bulletOffsetX, 
                                       playerY + bulletOffsetY, bigBulletSpeed, bigBulletImage, flashImage, true));
                SoundShot.playShotSound(); // Phát âm thanh khi bắn đạn to (cần thêm method này vào class SoundShot)
                lastBigShootTime = currentTime;
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
                    
                    //tang diem
                     increaseScore(10);
                    
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
                explosions.add(new Explosion(enemy.getX()-100, enemy.getY()-50, explosionPlayerFrames, 750 / explosionPlayerFrames.length));
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
     

    private void showGameOverScreen(Graphics g) {
    int newWidth = getWidth() * 2 / 4;
    int newHeight = getHeight() * 2 / 4;
    int x = (getWidth() - newWidth) / 2;
    int y = (getHeight() - newHeight) / 4;
    
    g.setColor(Color.WHITE);
    
    // Vẽ chữ "GAME OVER"
    Font gameOverFont = upheavalFont.deriveFont(140f);
    g.setFont(gameOverFont);
    FontMetrics fmGameOver = g.getFontMetrics();
    String gameOverText = "GAME OVER";
    int gameOverWidth = fmGameOver.stringWidth(gameOverText);
    int gameOverX = (getWidth() - gameOverWidth) / 2;
    int gameOverY = y + newHeight / 2;
    g.drawString(gameOverText, gameOverX, gameOverY);
    //ve diem cao nhat
    
    
    updateHighScore();

    // Hiển thị điểm cao nhất
    g.setFont(upheavalFont.deriveFont(30f));
    String highScoreText = "High Score: " + highScore;
    g.drawString(highScoreText, (getWidth() - g.getFontMetrics().stringWidth(highScoreText)) / 2, messageY + 140);
    
    
    // Vẽ thông báo "Restart Game?"
    Font messageFont = upheavalFont.deriveFont(50f);
    g.setFont(messageFont);
    FontMetrics fmMessage = g.getFontMetrics();
    String message = "Restart Game?";
    int messageWidth = fmMessage.stringWidth(message);
    messageY = gameOverY + 80;
    int messageX = (getWidth() - messageWidth) / 2;
    g.drawString(message, messageX, messageY);
    
    // Vẽ lựa chọn "Yes" và "No"
    Font optionFont = upheavalFont.deriveFont(40f);
    g.setFont(optionFont);
    FontMetrics fmOption = g.getFontMetrics();
    
    String yesOption = "Yes";
    String noOption = "No";
    int optionWidth = fmOption.stringWidth(yesOption) + fmOption.stringWidth(noOption);
    int spacing = 100; // Khoảng cách giữa "Yes" và "No"
    
    yesX = (getWidth() - optionWidth - spacing) / 2;
    noX = yesX + fmOption.stringWidth(yesOption) + spacing;
    
    g.drawString(yesOption, yesX, messageY + 80);
    g.drawString(noOption, noX, messageY + 80);
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
        if (healths.size() > 0) {
        // Vẽ trò chơi như bình thường
    } else {
        // Hiển thị màn hình Game Over
        showGameOverScreen(g);
        
       
    }
        // ve diem
        g.setColor(Color.WHITE);
        g.setFont(upheavalFont.deriveFont(30f));
        String scoreText = "Score: " + score;
        g.drawString(scoreText, 20, 40); // Vẽ điểm ở góc trên bên trái

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

            g.drawImage(flashImage, flashX, flashY, flashWidth, flashHeight, null);
        }
       
        

        g.dispose();
        bs.show();
    }
    
    //tinh diem
    private void increaseScore(int points) {
    score += points;
    }
        //diem cao nhat
    private void updateHighScore() {
        if (score > highScore) {
            highScore = score;
        }
    }
    
    
    
}