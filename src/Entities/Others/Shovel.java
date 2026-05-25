package Entities.Others;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Shovel {
    private boolean active = false;
    private boolean dragging = false;
    private int dragX = 0, dragY = 0;
    private Image cardNormalImage;   // ShovelCard1.png (bình thường)
    private Image cardActiveImage;   // ShovelCard2.png (khi active)
    private Image dragImage;         // ShoveL.png (theo chuột)
    
    public Shovel() {
        loadImages();
    }
    
    private void loadImages() {

        java.net.URL normalURL = getClass().getResource("/image/others/shovel/shovelCard1.png");
        java.net.URL activeURL = getClass().getResource("/image/others/shovel/shovelCard2.png");
        java.net.URL dragURL = getClass().getResource("/image/others/shovel/shovel.png");
        if (normalURL != null) cardNormalImage = new ImageIcon(normalURL).getImage();
        if (activeURL != null) cardActiveImage = new ImageIcon(activeURL).getImage();
        if (dragURL != null) dragImage = new ImageIcon(dragURL).getImage();

    }
    
    public void activate() {
        this.active = true;
    }
    
    public void deactivate() {
        this.active = false;
        this.dragging = false;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void startDrag(int x, int y) {
        System.out.println("Shovel.startDrag called, active=" + active); // DEBUG
        if (active) {
            dragging = true;
            dragX = x;
            dragY = y;
            System.out.println("dragging set to true"); // DEBUG
        }
    }
    
    public void updateDrag(int x, int y) {
        if (dragging) {
            dragX = x;
            dragY = y;
        }
    }
    
    public void endDrag() {
        dragging = false;
    }
    
    public boolean isDragging() {
        return dragging;
    }
    
    public int getDragX() {
        return dragX;
    }
    
    public int getDragY() {
        return dragY;
    }
    
    public void drawCard(Graphics g, int x, int y) {
        Image img = active ? cardActiveImage : cardNormalImage;
        if (img != null) {
            g.drawImage(img, x, y, 60, 60, null);
        } else {
            // Fallback
            g.setColor(active ? Color.YELLOW : Color.GRAY);
            g.fillRect(x, y, 60, 60);
            g.setColor(Color.BLACK);
            g.drawString("Shovel", x + 5, y + 25);
        }
    }
    
    public void drawDrag(Graphics g) {
        if (dragging && dragImage != null) {
            g.drawImage(dragImage, dragX - 40, dragY - 40, 80, 80, null);
        }
    }
}