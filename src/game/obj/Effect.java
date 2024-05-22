package game.obj;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.Random;

/**
 * Effect class to represent a graphical effect on the screen.
 */
public class Effect {
    private final double x;
    private final double y;
    private final double maxDistance;
    private final int maxSize;
    private final Color color;
    private final int totalEffect;
    private final float speed;
    private double currentDistance = 0;
    private ModelBoom[] booms;
    private float alpha = 1f;

    /**
     * Constructor to initialize the Effect.
     * 
     * @param x             The x-coordinate of the effect's origin.
     * @param y             The y-coordinate of the effect's origin.
     * @param totalEffect   The total number of sub-effects (booms).
     * @param maxSize       The maximum size of each sub-effect.
     * @param maxDistance   The maximum distance the effect can travel.
     * @param speed         The speed of the effect.
     * @param color         The color of the effect.
     */
    public Effect(double x, double y, int totalEffect, int maxSize, double maxDistance, float speed, Color color) {
        this.x = x;
        this.y = y;
        this.totalEffect = totalEffect;
        this.maxSize = maxSize;
        this.maxDistance = maxDistance;
        this.speed = speed;
        this.color = color;
        createRandom();
    }

    /**
     * Create random sub-effects (booms) with random sizes and angles.
     */
    private void createRandom() {
        booms = new ModelBoom[totalEffect];
        float angleIncrement = 360f / totalEffect;
        Random rand = new Random();
        for (int i = 0; i < totalEffect; i++) {
            int size = rand.nextInt(maxSize) + 1;
            float angle = i * angleIncrement + rand.nextInt((int) angleIncrement);
            booms[i] = new ModelBoom(size, angle);
        }
    }

    /**
     * Draw the effect on the provided Graphics2D object.
     * 
     * @param g2 The Graphics2D object to draw on.
     */
    public void draw(Graphics2D g2) {
        AffineTransform oldTransform = g2.getTransform();
        Composite oldComposite = g2.getComposite();
        g2.setColor(color);
        g2.translate(x, y);
        for (ModelBoom boom : booms) {
            double bx = Math.cos(Math.toRadians(boom.getAngle())) * currentDistance;
            double by = Math.sin(Math.toRadians(boom.getAngle())) * currentDistance;
            double boomSize = boom.getSize();
            double halfSize = boomSize / 2;
            if (currentDistance >= maxDistance - (maxDistance * 0.7f)) {
                alpha = (float) ((maxDistance - currentDistance) / (maxDistance * 0.7f));
            }
            alpha = Math.max(0, Math.min(alpha, 1));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.fill(new Ellipse2D.Double(bx - halfSize, by - halfSize, boomSize, boomSize));
        }
        g2.setTransform(oldTransform);
        g2.setComposite(oldComposite);
    }

    /**
     * Update the effect's state, moving it and reducing its alpha over time.
     */
    public void update() {
        currentDistance += speed;
        if (currentDistance > maxDistance) {
            currentDistance = maxDistance;
        }
        alpha -= 0.01f;
        if (alpha < 0) {
            alpha = 0;
        }
    }

    /**
     * Check if the effect is still visible.
     * 
     * @return True if the effect is still visible, false otherwise.
     */
    public boolean check() {
        return currentDistance < maxDistance;
    }

    // Class ModelBoom is assumed to be defined elsewhere with getSize() and getAngle() methods.
}
