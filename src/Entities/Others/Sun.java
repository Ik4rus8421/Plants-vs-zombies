package Entities.Others;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class Sun {
    public int x, y;
    public int targetY;     // điểm dừng
    public int value = 50;
    public boolean falling = true;
    private int speed = 2;
    private int life = 300;

    // --- NEW: Animation Properties ---
    private final List<Image> animationFrames;
    private int currentFrameIndex = 0;
    private int animationTimer = 0;
    private final int frameDelay = 3; // Tweak this number to change how fast the sun rotates/flashes

    public Sun(int x, int startY, int targetY) {
        this.x = x;
        this.y = startY;
        this.targetY = targetY;

        // Initialize and load animation frames
        this.animationFrames = new ArrayList<>();
        loadAnimationFrames();
    }

    /**
     * Loads sun1.png and sun2.png assets safely from memory
     */
    private void loadAnimationFrames() {
        for (int i = 1; i <= 2; i++) {
            String path = "/image/others/sun/sun" + i + ".png";
            try {
                java.net.URL imgURL = getClass().getResource(path);
                if (imgURL != null) {
                    animationFrames.add(new ImageIcon(imgURL).getImage());
                } else {
                    System.err.println("Could not find file: " + path);
                }
            } catch (Exception e) {
                System.err.println("Error loading sun frame " + i + ": " + e.getMessage());
            }
        }
    }

    public boolean isDead() {
        return life <= 0;
    }

    public void update() {
        if (falling) {
            y += speed;

            if (y >= targetY) {
                y = targetY;
                falling = false;
            }
        }
        else {
            life--;
        }

        // --- NEW: Progress Animation Timeline ---
        if (!animationFrames.isEmpty()) {
            animationTimer++;
            if (animationTimer >= frameDelay) {
                animationTimer = 0;
                currentFrameIndex = (currentFrameIndex + 1) % animationFrames.size();
            }
        }
    }

    /**
     * --- NEW: Draws the active animated sun frame asset ---
     */
    public void draw(Graphics g) {
        if (!animationFrames.isEmpty()) {
            // Scales the asset image perfectly to 40x40 to align with mouse bounds
            g.drawImage(animationFrames.get(currentFrameIndex), x, y, 40, 40, null);
        } else {
            // Fallback emergency circle layer if files disappear
            g.setColor(java.awt.Color.ORANGE);
            g.fillOval(x, y, 40, 40);
        }
    }
}