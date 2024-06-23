package game.obj;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Explosion {
    private int x, y;
    private BufferedImage[] frames;
    private int currentFrame;
    private long startTime;
    private long frameDuration;

    public Explosion(int x, int y, BufferedImage[] frames, long frameDuration) {
        this.x = x;
        this.y = y;
        this.frames = frames;
        this.frameDuration = frameDuration;
        this.startTime = System.currentTimeMillis();
        this.currentFrame = 0;
    }

    public void update() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - startTime >= frameDuration) {
            currentFrame++;
            startTime = currentTime;
        }
    }

    public void draw(Graphics g) {
        if (currentFrame < frames.length) {
            int width = frames[currentFrame].getWidth() * 5;
            int height = frames[currentFrame].getHeight() * 5;
            g.drawImage(frames[currentFrame], x, y, width, height, null);
        }
    }

    public boolean isFinished() {
        return currentFrame >= frames.length;
    }
}