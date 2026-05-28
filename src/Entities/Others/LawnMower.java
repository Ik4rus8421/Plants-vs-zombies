package Entities.Others;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

public class LawnMower {

    public int row;

    // Coordinates in GamePanel (can be negative)
    public double gameX;
    
    // Coordinates in LawnPanel (always positive)
    public double lawnX;

    public boolean active = false;
    public boolean used = false;

    private Image image;

    // ===== MAP CONFIG =====
    public static final int MAP_LEFT = 528;
    public static final int MAP_TOP = 91;
    public static final int CELL_SIZE = 100;

    public LawnMower(int row) {
        this.row = row;
        
        // Start from outside (negative in GamePanel)
        this.gameX = -100;           // Outside GamePanel
        this.lawnX = MAP_LEFT - 100; // 428 - corresponding in LawnPanel
        
        loadImage();
    }

    private void loadImage() {
        try {
            java.net.URL imgURL = getClass().getResource("/image/others/lawnmower/lawnmower.png");
            if (imgURL != null) {
                image = new ImageIcon(imgURL).getImage();
            }
        } catch (Exception e) {
            System.err.println("Error loading lawnmower image: " + e.getMessage());
        }
    }

    public void update() {
        if (active) {
            // UPDATE BOTH coordinates simultaneously
            gameX += 5;
            lawnX += 5;
        }
    }

    // Draw on GamePanel
    public void drawOnGamePanel(Graphics g) {
        if (active) {
            int drawX = (int)gameX;
            int drawY = row * 100 + 25;  // Y in GamePanel
            
            if (image != null) {
                g.drawImage(image, drawX, drawY, 80, 60, null);
            }
        }
    }

    // Draw on LawnPanel
    public void drawOnLawnPanel(Graphics g) {
        int drawX = (int)lawnX;
        int drawY = MAP_TOP + row * CELL_SIZE + 25;
        
        if (image != null) {
            g.drawImage(image, drawX, drawY, 80, 60, null);
        }
    }
}