package Entities.Zombie;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public abstract class Zombies {
    public double x;
    public int row;
    public int hp;
    public double speed;
    public double originalSpeed;
    public int slowTimer = 0;

    public Zombies(int r, int hp, double speed) {
        row = r;
        this.hp = hp;
        this.speed = speed;
        x = 900;
        this.originalSpeed = speed;
    }

    public void update() {
        // slow effect
        if (slowTimer > 0) {
            slowTimer--;
        } else {
            speed = originalSpeed;
        }

        // Tính cột dựa trên vị trí thực tế (bỏ +30)
        int col = (int)(x / 100);
        
        if (col >= 0 && col < Core.Game.getInstance().grid.cols && row >= 0 && row < Core.Game.getInstance().grid.rows) {
            var cell = Core.Game.getInstance().grid.cells[row][col];
            if (cell.plant != null) {
                // Tính khoảng cách từ zombie đến tâm plant
                double plantCenter = col * 100 + 50;
                double distance = Math.abs(x - plantCenter);
                
                // GIẢM GIÁ TRỊ - Càng nhỏ càng phải đến gần
                if (distance <= 0) {  // Giảm từ 30 xuống 20
                    cell.plant.hp -= 1;
                    if (cell.plant.hp <= 0) {
                        cell.plant = null;
                    }
                    return;
                }
            }
        }
        x -= speed;
    }
    
    protected Image applySlowFilter(Image originalImage) {
        if (slowTimer <= 0 || originalImage == null) return originalImage;
        
        int width = originalImage.getWidth(null);
        int height = originalImage.getHeight(null);
        if (width <= 0) width = 60;
        if (height <= 0) height = 60;
        
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, width, height, null);
        g2d.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_ATOP, 0.7f));
        g2d.setColor(new java.awt.Color(0, 200, 255, 180));
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        
        return bufferedImage;
    }
}