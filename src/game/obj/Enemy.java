package game.obj;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Enemy {
    private int x, y;
    private int speed;
    private BufferedImage image;

    public Enemy(int x, int y, int speed, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.image = image;
    }

    public void update() {
        x -= speed; // Di chuyển từ phải sang trái
    }

    public void draw(Graphics g) {
        int width = image.getWidth() * 2; // Nhân đôi chiều rộng
        int height = image.getHeight() * 2; // Nhân đôi chiều cao
        g.drawImage(image, x, y, width, height, null);

//        // Vẽ hitbox màu xanh lá
//        g.setColor(Color.GREEN);
//        g.drawRect(x, y, width, height);
    }

    public boolean isOffScreen() {
        return x + image.getWidth() * 2 < 0; // Kiểm tra với chiều rộng nhân đôi
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