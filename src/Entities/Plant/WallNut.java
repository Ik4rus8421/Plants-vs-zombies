package Entities.Plant;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class WallNut extends Plants {

    // Animation Phase Frame Containers
    private final List<Image> phase1Frames = new ArrayList<>();
    private final List<Image> phase2Frames = new ArrayList<>();
    private final List<Image> phase3Frames = new ArrayList<>();

    private int currentFrameIndex = 0;
    private int animationTimer = 0;
    private final int frameDelay = 10; // Controls animation loop breathing speed

    // Tracks phase changes to reset frame indices safely
    private int previousPhase = 1;

    public WallNut(int r, int c) {
        super(r, c);
        this.hp = 500; // 🛠️ Correctly updates the shared health pool from the parent class
        loadAnimationFrames();
    }

    private void loadAnimationFrames() {
        // Phase 1: 5 frames for healthy state loop
        for (int i = 1; i <= 5; i++) {
            phase1Frames.add(getSprite("/image/plant/walnut/walnut/walnut" + i + ".png"));
        }
        // Phase 2: 5 frames for medium-damage crack transition (one-way sequential)
        for (int i = 1; i <= 5; i++) {
            phase2Frames.add(getSprite("/image/plant/walnut/walnutd/walnutd" + i + ".png"));
        }
        // Phase 3: 5 frames for high-damage final state sequence (one-way sequential)
        for (int i = 1; i <= 5; i++) {
            phase3Frames.add(getSprite("/image/plant/walnut/walnutdd/walnutdd" + i + ".png"));
        }
    }

    private Image getSprite(String path) {
        try {
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL != null) return new ImageIcon(imgURL).getImage();
            System.err.println("Wall-Nut texture missing: " + path);
        } catch (Exception e) {
            System.err.println("Error reading Wall-Nut asset: " + path);
        }
        return null;
    }

    /**
     * Determines the current structural stage threshold based on remaining hit points
     */
    private int getCurrentPhase() {
        if (hp > 333) return 1;      // Phase 1: Healthy (Above 66% HP)
        else if (hp > 166) return 2; // Phase 2: Cracking (33% to 66% HP)
        else return 3;               // Phase 3: Broken/Crying (Below 33% HP)
    }

    @Override
    public void update() {
        int currentPhase = getCurrentPhase();

        // Reset tracking timers if a threshold is passed to play the next phase smoothly
        if (currentPhase != previousPhase) {
            previousPhase = currentPhase;
            currentFrameIndex = 0;
            animationTimer = 0;
        }

        animationTimer++;
        if (animationTimer >= frameDelay) {
            animationTimer = 0;

            if (currentPhase == 1) {
                // Phase 1: Continuous breathing/idling loop
                if (!phase1Frames.isEmpty()) {
                    currentFrameIndex = (currentFrameIndex + 1) % phase1Frames.size();
                }
            }
            else if (currentPhase == 2) {
                // Phase 2: One-way step progression (locks on final cracked frame)
                if (!phase2Frames.isEmpty() && currentFrameIndex < phase2Frames.size() - 1) {
                    currentFrameIndex++;
                }
            }
            else {
                // Phase 3: One-way step progression (locks on final heavily broken frame)
                if (!phase3Frames.isEmpty() && currentFrameIndex < phase3Frames.size() - 1) {
                    currentFrameIndex++;
                }
            }
        }
    }

    public void draw(Graphics g) {
        // Center a 70x70 image inside the 100x100 grid space
        int drawX = col * 100 + 15;
        int drawY = row * 100 + 100 + 15;

        int currentPhase = getCurrentPhase();
        List<Image> activeFrames;

        if (currentPhase == 1)      activeFrames = phase1Frames;
        else if (currentPhase == 2) activeFrames = phase2Frames;
        else                        activeFrames = phase3Frames;

        if (!activeFrames.isEmpty() && currentFrameIndex < activeFrames.size() && activeFrames.get(currentFrameIndex) != null) {
            g.drawImage(activeFrames.get(currentFrameIndex), drawX, drawY, 70, 70, null);
        } else {
            // Fallback boxes change color values smoothly so you can debug asset loading
            if (currentPhase == 1)      g.setColor(new java.awt.Color(205, 133, 63)); // Wood Brown
            else if (currentPhase == 2) g.setColor(new java.awt.Color(244, 164, 96)); // Sandy/Scratched Brown
            else                        g.setColor(java.awt.Color.ORANGE);             // Cracking Alert Orange

            g.fillRect(drawX, drawY, 70, 70);
        }
    }
}