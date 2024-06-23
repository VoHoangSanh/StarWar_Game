package game.obj;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Health {
    private int x, y;
    private BufferedImage image;

    public Health(int x, int y, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }

    public void draw(Graphics g) {
        g.drawImage(image, x, y, null);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }
}