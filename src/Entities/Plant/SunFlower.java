package Entities.Plant;
import Core.Game;
import Entities.Others.Sun;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class SunFlower extends Plants {
    public int timer;

    private final List<Image> idleFrames;

    private int currentFrameIndex = 0;
    private int animationTimer = 0;
    private final int frameDelay = 2; // Adjust to speed up or slow down swaying

    public SunFlower(int r, int c) {
        super(r, c);
        this.idleFrames = new ArrayList<>();

        // Load only the idle animation frames into memory at startup
        loadIdleFrames();
    }

    /**
     * Loads your idle frames starting with sunfloweridle1.png
     */
    private void loadIdleFrames() {
        // Tweak the '8' to match your exact count of idle frames
        for (int i = 1; i <= 8; i++) {
            String path = "/image/plant/sunflower/sunflower/sunflower" + i + ".png";
            try {
                java.net.URL imgURL = getClass().getResource(path);
                if (imgURL != null) {
                    idleFrames.add(new ImageIcon(imgURL).getImage());
                }
            } catch (Exception e) {
                System.err.println("Error loading sunflower idle frame " + i + ": " + e.getMessage());
            }
        }
    }

    @Override
    public void update() {
        timer++;

        // 1. Sun Production Tracker (Keeps spawning sun objects cleanly on schedule)
        if (timer >= 100) {
            int x = col * 100 + 40;
            int startY = 0;
            int targetY = row * 100 + 140; // spawn sun from top and down to targetY

            Game.getInstance().suns.add(new Sun(x, startY, targetY));

            timer = 0;
        }

        // 2. Pure Idle Animation Loop tracking
        if (!idleFrames.isEmpty()) {
            animationTimer++;
            if (animationTimer >= frameDelay) {
                animationTimer = 0;
                // Cycles cleanly through your available idle frame index collection
                currentFrameIndex = (currentFrameIndex + 1) % idleFrames.size();
            }
        }
    }

    /**
     * Draws the animated sunflower frame at its row/col tile position
     */
    public void draw(Graphics g) {
        // Offset by 15 pixels to perfectly center a 70x70 image inside the 100x100 cell
        int drawX = col * 100 + 15;
        int drawY = row * 100 + 100 + 15;

        if (!idleFrames.isEmpty() && idleFrames.get(currentFrameIndex) != null) {
            // Force the image to draw exactly 70x70 pixels
            g.drawImage(idleFrames.get(currentFrameIndex), drawX, drawY, 65, 65, null);
        }
    }
}