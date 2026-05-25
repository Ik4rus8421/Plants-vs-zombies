package Entities.Plant;

import Core.Game;
import Entities.Bullet.SlowBullet;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.Timer;

public class SnowPea extends Plants {
    private final List<Image> idleFrames = new ArrayList<>();
    private final List<Image> shootingFrames = new ArrayList<>();
    
    private int currentFrameIndex = 0;
    private boolean isShooting = false;
    private boolean hasShotInThisCycle = false;
    
    private static Image cardReady;
    private static Image cardFaded;
    private static Image dragImage;
    
    private Timer gameTimer;
    private int idleFrameDelay = 2;
    private float shootFrameDelay = 18.75f;
    private float frameCounter = 0;
    
    public SnowPea(int r, int c) {
        super(r, c);
        this.hp = 100;
        loadIdleFrames();
        loadShootingFrames();
        loadCardImages();
        loadDragImage();
        startGameTimer();
    }
    
    public void endGameTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
            gameTimer = null;
        }
    }
    
    private void loadIdleFrames() {
        for (int i = 1; i <= 25; i++) {
            String path = "/image/plant/snowpea/snowpea/snowpea" + i + ".png";
            try {
                java.net.URL imgURL = getClass().getResource(path);
                if (imgURL != null) {
                    idleFrames.add(new ImageIcon(imgURL).getImage());
                }
            } catch (Exception e) {}
        }
        if (idleFrames.isEmpty()) {
            try {
                java.net.URL imgURL = getClass().getResource("/image/plant/snowpea/snowpea/snowpea1.png");
                if (imgURL != null) {
                    idleFrames.add(new ImageIcon(imgURL).getImage());
                }
            } catch (Exception e) {}
        }
    }
    
    private void loadShootingFrames() {
        for (int i = 1; i <= 4; i++) {
            String path = "/image/plant/snowpea/snowpeashoot/snowpeashoot" + i + ".png";
            try {
                java.net.URL imgURL = getClass().getResource(path);
                if (imgURL != null) {
                    shootingFrames.add(new ImageIcon(imgURL).getImage());
                }
            } catch (Exception e) {}
        }
        if (shootingFrames.isEmpty() && !idleFrames.isEmpty()) {
            shootingFrames.add(idleFrames.get(0));
        }
    }
    
    private static void loadCardImages() {
        if (cardReady == null) {
            try {
                java.net.URL readyURL = SnowPea.class.getResource("/image/plant/snowpea/snowpeapacket/snowpeapacket1.png");
                java.net.URL fadedURL = SnowPea.class.getResource("/image/plant/snowpea/snowpeapacket/snowpeapacket2.png");
                if (readyURL != null) cardReady = new ImageIcon(readyURL).getImage();
                if (fadedURL != null) cardFaded = new ImageIcon(fadedURL).getImage();
            } catch (Exception e) {}
        }
    }
    
    private static void loadDragImage() {
        if (dragImage == null) {
            try {
                java.net.URL dragURL = SnowPea.class.getResource("/image/plant/snowpea/snowpea/snowpea1.png");
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
    
    private void startGameTimer() {
        gameTimer = new Timer(20, e -> {
            // Kiểm tra nếu timer đã bị hủy hoặc cây không còn trong grid
            if (gameTimer == null) return;
            if (Game.getInstance().grid.cells[row][col].plant != this) {
                endGameTimer();
                return;
            }
            
            boolean zombieAhead = Game.getInstance().hasZombieAhead(row, col);
            
            if (zombieAhead && !isShooting) {
                isShooting = true;
                currentFrameIndex = 0;
                frameCounter = 0;
                hasShotInThisCycle = false;
            }
            
            if (isShooting) {
                frameCounter++;
                if (frameCounter >= shootFrameDelay) {
                    frameCounter = 0;
                    if (!shootingFrames.isEmpty()) {
                        currentFrameIndex++;
                        
                        if (currentFrameIndex == 3 && !hasShotInThisCycle && zombieAhead) {
                            Game.getInstance().bullets.add(new SlowBullet(row, col * 100 + 70));
                            hasShotInThisCycle = true;
                        }
                        
                        if (currentFrameIndex >= shootingFrames.size()) {
                            currentFrameIndex = 0;
                            if (!zombieAhead) {
                                isShooting = false;
                                hasShotInThisCycle = false;
                            } else {
                                hasShotInThisCycle = false;
                            }
                        }
                    }
                }
            } else {
                frameCounter++;
                if (frameCounter >= idleFrameDelay) {
                    frameCounter = 0;
                    if (!idleFrames.isEmpty()) {
                        currentFrameIndex++;
                        if (currentFrameIndex >= idleFrames.size()) {
                            currentFrameIndex = 0;
                        }
                    }
                }
            }
            
            if (!zombieAhead && isShooting) {
                isShooting = false;
                currentFrameIndex = 0;
                frameCounter = 0;
                hasShotInThisCycle = false;
            }
        });
        gameTimer.start();
    }
    
    @Override
    public void update() {
        // Timer riêng đã xử lý
    }
    
    public void draw(Graphics g) {
        int drawX = col * 100 + 15;
        int drawY = row * 100 + 100 + 15;
        
        List<Image> currentFrames = isShooting ? shootingFrames : idleFrames;
        
        if (!currentFrames.isEmpty() && currentFrameIndex < currentFrames.size() && currentFrames.get(currentFrameIndex) != null) {
            g.drawImage(currentFrames.get(currentFrameIndex), drawX, drawY, 65, 65, null);
        } else if (!idleFrames.isEmpty()) {
            g.drawImage(idleFrames.get(0), drawX, drawY, 65, 65, null);
        } else {
            g.setColor(java.awt.Color.CYAN);
            g.fillRect(drawX, drawY, 65, 65);
            g.setColor(java.awt.Color.BLACK);
            g.drawString("SP", drawX + 25, drawY + 35);
        }
    }
}