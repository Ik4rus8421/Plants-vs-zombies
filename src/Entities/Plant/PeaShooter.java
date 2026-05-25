package Entities.Plant;

import Core.Game;
import Entities.Bullet.Bullet;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class PeaShooter extends Plants {
    private int timer; // Your original bullet timer

    // Separate lists for both animation states (film strips)
    private final List<Image> idleFrames;
    private final List<Image> shootingFrames;

    private int currentFrameIndex = 0;
    private int animationTimer = 0;
    private final double frameDelay = 4.25; // Controls how fast the animation loops

    // Tracks the state from the previous tick to detect transitions smoothly
    private boolean wasZombieAhead = false;

    public PeaShooter(int r, int c) {
        super(r, c); // Your original constructor call
        this.idleFrames = new ArrayList<>();
        this.shootingFrames = new ArrayList<>();

        // Load both animation sets into memory at startup
        loadIdleFrames();
        loadShootingFrames();
    }

    /**
     * Loads the 9 idle frames (flashpeashooter1.png to 9.png)
     */
    private void loadIdleFrames() {
        // Load nhiều idle frame để tạo animation đung đưa
        for (int i = 1; i <= 9; i++) {
            String path = "/image/plant/peashooter/peashooter/peashooter" + i + ".png";
            try {
                java.net.URL imgURL = getClass().getResource(path);
                if (imgURL != null) {
                    idleFrames.add(new ImageIcon(imgURL).getImage());
                }
            } catch (Exception e) {
                System.err.println("Error loading peashooter idle frame " + i + ": " + e.getMessage());
            }
        }
        
        // Nếu không có nhiều frame, dùng 1 frame tĩnh
        if (idleFrames.isEmpty()) {
            String path = "/image/plant/peashooter/peashooter/peashooter1.png";
            try {
                java.net.URL imgURL = getClass().getResource(path);
                if (imgURL != null) {
                    idleFrames.add(new ImageIcon(imgURL).getImage());
                }
            } catch (Exception e) {
                System.err.println("Error loading peashooter image: " + e.getMessage());
            }
        }
    }

    /**
     * ⚠️ NOTE: Edit this method to match your exact shooting image names!
     * Right now, it assumes a pattern of "shootpeashooter1.png" to "shootpeashooter6.png"
     */
    private void loadShootingFrames() {
        // Change the '6' below to match the exact number of shooting pictures you have!
        for (int i = 1; i <= 6; i++) {
            String path = "/image/plant/peashooter/peashootershoot/peashootershoot" + i + ".png";
            try {
                java.net.URL imgURL = getClass().getResource(path);
                if (imgURL != null) {
                    shootingFrames.add(new ImageIcon(imgURL).getImage());
                }
            } catch (Exception e) {
                System.err.println("Error loading shooting frame " + i + ": " + e.getMessage());
            }
        }
    }

    @Override
    public void update() {
        // Check if there is currently a target zombie in this row
        boolean zombieAhead = Game.getInstance().hasZombieAhead(row, col);

        // --- YOUR ORIGINAL BULLET LOGIC (UNTOUCHED) ---
        timer++;
        if (timer >= 15 && zombieAhead) {
            Game.getInstance().bullets.add(
                    new Bullet(row, col * 100 + 60, false)
            );
            timer = 0;
        }
        // -------------------------------------

        // --- SMART ANIMATION STATE MACHINE ---
        // If a zombie just entered or just left the lane, reset indices
        // so the new animation loop starts cleanly from the first image frame
        if (zombieAhead != wasZombieAhead) {
            currentFrameIndex = 0;
            animationTimer = 0;
            wasZombieAhead = zombieAhead;
        }

        // Select the active film strip based on the zombie state
        List<Image> currentFrames = zombieAhead ? shootingFrames : idleFrames;

        // Safety fallback: if shooting files aren't found or loaded yet, keep idling
        if (currentFrames.isEmpty()) {
            currentFrames = idleFrames;
        }

        // Progress animation timeline calculations
        if (!currentFrames.isEmpty()) {
            animationTimer++;
            if (animationTimer >= frameDelay) {
                animationTimer = 0;
                // Safely wraps loop based on the active animation list's size
                currentFrameIndex = (currentFrameIndex + 1) % currentFrames.size();
            }
        }
    }

    /**
     * Draws the correct frame from the active animation state
     */
    public void draw(Graphics g) {
        // Offset by 15 pixels to perfectly center
        int drawX = col * 100 + 15;
        int drawY = row * 100 + 100 + 15;

        List<Image> currentFrames = wasZombieAhead ? shootingFrames : idleFrames;
        if (currentFrames.isEmpty()) {
            currentFrames = idleFrames;
        }

        if (!currentFrames.isEmpty() && currentFrames.get(currentFrameIndex) != null) {
            // Force the image to draw exactly 70x70 pixels
            g.drawImage(currentFrames.get(currentFrameIndex), drawX, drawY, 65, 65, null);
        }
    }
}