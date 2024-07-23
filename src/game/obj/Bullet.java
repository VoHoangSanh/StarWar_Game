package game.obj;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Bullet {
    private int x, y;
    private int speed;
    private BufferedImage image;
    private BufferedImage flashImage;
    private boolean showFlash;
    private long flashStartTime;
    
     private boolean isBigBullet;

    public Bullet(int x, int y, int speed, BufferedImage image, BufferedImage flashImage) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.image = image;
        this.flashImage = flashImage;
        this.showFlash = true;
        this.flashStartTime = System.currentTimeMillis();
    }
    
    public Bullet(int x, int y, int speed, BufferedImage image, BufferedImage flashImage, boolean isBigBullet) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.image = image;
        this.flashImage = flashImage;
        this.isBigBullet = isBigBullet;
    }

    public void update() {
        x += speed; // Đạn bay lên trên
    }

    public void draw(Graphics g) {
        // Vẽ viên đạn
          int width = image.getWidth() * 3; // Nhân đôi chiều rộng
          int height = image.getHeight() * 3; // Nhân đôi chiều cao
          g.drawImage(image, x, y, width, height, null);

          

        // Vẽ hitbox màu xanh lá
        // g.setColor(Color.GREEN);
        // g.drawRect(x, y, width, height);
    }
    
    
     public Rectangle getHitbox() {
        return new Rectangle(x, y, isBigBullet ? image.getWidth() * 2 : image.getWidth(), 
                             isBigBullet ? image.getHeight() * 2 : image.getHeight());
    }
    

    public boolean isOffScreen(int screenHeight) {
        return y + image.getHeight() * 2 < 0; // Kiểm tra với chiều cao nhân đôi
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return image.getWidth() * 2;
    }

    public int getHeight() {
        return image.getHeight() * 2;
    }
}