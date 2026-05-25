package Entities.Bullet;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class Bullet {
    public int row;
    public double x;
    public boolean slow = false;

    // --- NEW: Single Sequential Timeline Track ---
    protected final List<Image> animationFrames = new ArrayList<>();
    protected int currentFrameIndex = 0; // 0 = Moving, 1 = Hit, 2 = Splash

    protected int animationTimer = 0;
    protected final int frameDelay = 1
            ; // Controls how long frames 1 and 2 linger on screen

    protected boolean hasCollided = false;
    public boolean shouldRemove = false;

    public Bullet(int row, double x, boolean slow) {
        this.row = row;
        this.x = x;
        this.slow = slow;

        loadAnimationFrames();
    }

    /**
     * Loads bullet1 (moving), bullet2 (hit), and bullet3 (splash) into memory
     */
    protected void loadAnimationFrames() {
        for (int i = 1; i <= 3; i++) {
            String path = "/image/plant/bullet/pea/pea" + i + ".png";
            try {
                java.net.URL imgURL = getClass().getResource(path);
                if (imgURL != null) {
                    animationFrames.add(new ImageIcon(imgURL).getImage());
                } else {
                    System.err.println("Could not find bullet asset: " + path);
                }
            } catch (Exception e) {
                System.err.println("Error loading bullet frame " + i + ": " + e.getMessage());
            }
        }
    }

    /**
     * Call this from Game.java when it intersects a zombie's hitbox
     */
    public void explode() {
        if (!hasCollided) {
            hasCollided = true;
            currentFrameIndex = 1; // Instantly advance to frame 2 (the hitted phase)
            animationTimer = 0;
        }
    }

    /**
     * Checks if the pea is still actively traveling through the air
     */
    public boolean isMoving() {
        return !hasCollided && currentFrameIndex == 0;
    }

    public void update() {
        // Phase 1: Only fly forward if we are on frame 1 (moving pea)
        if (!hasCollided && currentFrameIndex == 0) {
            x += 5; // Original flight movement speed
        }
        // Phase 2 & 3: Handle hit and splash step updates after collision triggers
        else if (hasCollided) {
            animationTimer++;
            if (animationTimer >= frameDelay) {
                animationTimer = 0;
                currentFrameIndex++; // Steps from 1 (hit) to 2 (splash), then to 3 (dead)

                if (currentFrameIndex >= animationFrames.size()) {
                    shouldRemove = true; // Signal game loop to wipe item from list
                }
            }
        }
    }

    public void draw(Graphics g, int offsetY) {
        int drawX = (int) x;
        int drawY = row * 100 + offsetY + 20;

        if (!animationFrames.isEmpty() && currentFrameIndex < animationFrames.size() && animationFrames.get(currentFrameIndex) != null) {
            g.drawImage(animationFrames.get(currentFrameIndex), drawX, drawY, 15, 15, null);
        } else {
            // Emergency primitive drawing backup fallback
            if (!hasCollided) {
                g.setColor(java.awt.Color.BLACK);
                g.fillOval(drawX, drawY, 10, 10);
            }
        }
    }
}