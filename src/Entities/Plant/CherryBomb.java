package Entities.Plant;

import Core.Game;
import Entities.Zombie.PoleVaulting;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.Timer;

public class CherryBomb extends Plants {

    private boolean exploded = false;
    
    // Explode animation frames
    private final List<Image> explodeFrames = new ArrayList<>();
    
    // Card images
    private static Image cardReady;
    private static Image cardFaded;
    
    // Drag image (kéo thả)
    private static Image dragImage;
    
    private int currentFrameIndex = 0;
    private Timer animationTimer;
    private int lastDrawnFrame = -1;
    
    public CherryBomb(int r, int c) {
        super(r, c);
        this.hp = 100;
        loadExplodeFrames();
        loadCardImages();
        loadDragImage();
    }
    
    private void loadExplodeFrames() {
        for (int i = 1; i <= 53; i++) {
            String path = "/image/plant/cherrybomb/cherrybomb/cherrybomb" + i + ".png";
            try {
                java.net.URL imgURL = getClass().getResource(path);
                if (imgURL != null) {
                    explodeFrames.add(new ImageIcon(imgURL).getImage());
                }
            } catch (Exception e) {}
        }
        if (!explodeFrames.isEmpty()) {
            exploded = true;
            startExplosionAnimation();
        }
    }
    
    private void startExplosionAnimation() {
        animationTimer = new Timer(16, e -> {
            if (currentFrameIndex < explodeFrames.size() - 1) {
                currentFrameIndex++;
            }
        });
        animationTimer.start();
    }
    
    private static void loadCardImages() {
        if (cardReady == null) {
            try {
                java.net.URL readyURL = CherryBomb.class.getResource("/image/plant/cherrybomb/cherrybombpacket/cherrybombpacket1.png");
                java.net.URL fadedURL = CherryBomb.class.getResource("/image/plant/cherrybomb/cherrybombpacket/cherrybombpacket2.png");
                if (readyURL != null) cardReady = new ImageIcon(readyURL).getImage();
                if (fadedURL != null) cardFaded = new ImageIcon(fadedURL).getImage();
            } catch (Exception e) {}
        }
    }
    
    private static void loadDragImage() {
        if (dragImage == null) {
            try {
                java.net.URL dragURL = CherryBomb.class.getResource("/image/plant/cherrybomb/cherrybomb/cherrybomb1.png");
                if (dragURL != null) dragImage = new ImageIcon(dragURL).getImage();
            } catch (Exception e) {}
        }
    }
    
    public static Image getCardReady() {
        loadCardImages();
        return cardReady;
    }
    
    public static Image getCardFaded() {
        loadCardImages();
        return cardFaded;
    }
    
    public static Image getDragImageStatic() {
        loadDragImage();
        return dragImage;
    }
    
    @Override
    public void update() {
        // Nếu chưa nổ, kiểm tra zombie trong vùng 3x3
        if (!exploded) {
            // Vùng nổ của cherry bomb: 3x3 ô xung quanh
            int startRow = Math.max(0, row - 1);
            int endRow = Math.min(Game.getInstance().grid.rows - 1, row + 1);
            int startCol = Math.max(0, col - 1);
            int endCol = Math.min(Game.getInstance().grid.cols - 1, col + 1);
            
            boolean hasZombieInRange = false;
            
            // Kiểm tra từng zombie có phần nào lấn vào vùng nổ không
            for (var z : Game.getInstance().Zombies) {
                // Hitbox của zombie
                double zombieLeft = z.x;
                double zombieRight = z.x + 60;
                
                if (z instanceof PoleVaulting) {
                    PoleVaulting pv = (PoleVaulting) z;
                    if (!pv.hasJumped()) {
                        zombieLeft = z.x + 50;
                        zombieRight = z.x + 110;
                    }
                }
                
                // Kiểm tra zombie có nằm trong vùng nổ theo hàng
                if (z.row >= startRow && z.row <= endRow) {
                    // Kiểm tra zombie có phần nào trong các cột của vùng nổ
                    for (int c = startCol; c <= endCol; c++) {
                        double mineLeft = c * 100;
                        double mineRight = c * 100 + 100;
                        
                        if (zombieRight >= mineLeft && zombieLeft <= mineRight) {
                            hasZombieInRange = true;
                            break;
                        }
                    }
                }
                if (hasZombieInRange) break;
            }
            
            if (hasZombieInRange && !exploded) {
                exploded = true;
                startExplosionAnimation();
            }
        }
    }
    
    private void explode() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        
        // Vùng nổ 3x3 ô
        int startRow = Math.max(0, row - 1);
        int endRow = Math.min(Game.getInstance().grid.rows - 1, row + 1);
        int startCol = Math.max(0, col - 1);
        int endCol = Math.min(Game.getInstance().grid.cols - 1, col + 1);
        
        // Xóa TẤT CẢ zombie có phần nào trong vùng nổ
        for (int i = Game.getInstance().Zombies.size() - 1; i >= 0; i--) {
            var z = Game.getInstance().Zombies.get(i);
            
            // Kiểm tra hàng
            if (z.row < startRow || z.row > endRow) continue;
            
            // Hitbox zombie
            double zombieLeft = z.x;
            double zombieRight = z.x + 60;
            
            if (z instanceof PoleVaulting) {
                PoleVaulting pv = (PoleVaulting) z;
                if (!pv.hasJumped()) {
                    zombieLeft = z.x + 50;
                    zombieRight = z.x + 110;
                }
            }
            
            // Kiểm tra có phần nào trong vùng nổ không
            boolean killed = false;
            for (int c = startCol; c <= endCol; c++) {
                double mineLeft = c * 100;
                double mineRight = c * 100 + 100;
                
                if (zombieRight >= mineLeft && zombieLeft <= mineRight) {
                    Game.getInstance().Zombies.remove(i);
                    killed = true;
                    System.out.println("CherryBomb killed " + z.getClass().getSimpleName() + " at row " + z.row);
                    break;
                }
            }
        }
        
        // Xóa cherry bomb
        Game.getInstance().grid.cells[row][col].plant = null;
        System.out.println("CherryBomb EXPLODED at row " + row + ", col " + col);
    }
    
    public void draw(Graphics g) {
        int drawX = col * 100 + 15;
        int drawY = row * 100 + 100 + 15;
        
        int frameToDraw = Math.min(currentFrameIndex, explodeFrames.size() - 1);
        boolean isLastTwoFrames = (frameToDraw >= explodeFrames.size() - 2);
        
        if (!explodeFrames.isEmpty() && frameToDraw >= 0 && frameToDraw < explodeFrames.size() && explodeFrames.get(frameToDraw) != null) {
            if (isLastTwoFrames) {
                g.drawImage(explodeFrames.get(frameToDraw), drawX - 220, drawY - 150, 500, 400, null);
            } else {
                g.drawImage(explodeFrames.get(frameToDraw), drawX, drawY, 70, 70, null);
            }
            lastDrawnFrame = frameToDraw;
        } else {
            g.setColor(java.awt.Color.RED);
            g.fillRect(drawX, drawY, 70, 70);
            g.setColor(java.awt.Color.BLACK);
            g.drawString("CB", drawX + 25, drawY + 35);
        }
        
        if (frameToDraw >= explodeFrames.size() - 1 && !explodeFrames.isEmpty()) {
            explode();
        }
    }
}