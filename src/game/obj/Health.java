package game.obj;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Health {
    private int offsetX, offsetY;
    private BufferedImage image;
    private int width, height;
    private int spacing; // Khoảng cách giữa các Health

   public Health(int offsetX, int offsetY, BufferedImage image, int width, int height, int spacing) {
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    this.image = image;
    this.width = width;
    this.height = height;
    this.spacing = spacing; // Khởi tạo khoảng cách giữa các Health
}

    public void draw(Graphics g, int playerX, int playerY) {
        g.drawImage(image, playerX + offsetX, playerY + offsetY, width, height, null);
    }
    
    public void setOffset(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    public void setSpacing(int spacing) {
    this.spacing = spacing;
}

public void drawMultiple(Graphics g, int playerX, int playerY, int count) {
    for (int i = 0; i < count; i++) {
        int currentOffsetX = (width + spacing) * i;
        g.drawImage(image, playerX + offsetX + currentOffsetX, playerY + offsetY, width, height, null);
    }
}
}