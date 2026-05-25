package Entities.Plant;

import Core.Game;
import Entities.Bullet.Bullet;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class Repeater extends Plants {

    private int cooldown = 0;
    private boolean hasShot = false;  // Đánh dấu đã bắn trong chu kỳ animation này

    // Animation Containers
    private final List<Image> idleFrames = new ArrayList<>();
    private final List<Image> shootingFrames = new ArrayList<>();

    private int currentFrameIndex = 0;
    private int animationTimer = 0;

    private final int idleFrameDelay = 2;
    private final int shootFrameDelay = 12;  // Giảm xuống để animation nhanh hơn

    private boolean wasZombieAhead = false;

    public Repeater(int r, int c) {
        super(r, c);
        this.hp = 300;
        loadAnimationFrames();
    }

    private void loadAnimationFrames() {
        for (int i = 1; i <= 4; i++) {
            idleFrames.add(getSprite("/image/plant/repeater/repeater/repeater" + i + ".png"));
        }
        for (int i = 1; i <= 3; i++) {
            shootingFrames.add(getSprite("/image/plant/repeater/repeatershoot/repeatershoot" + i + ".png"));
        }
    }

    private Image getSprite(String path) {
        try {
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL != null) return new ImageIcon(imgURL).getImage();
            System.err.println("Asset not found: " + path);
        } catch (Exception e) {
            System.err.println("Error loading asset: " + path);
        }
        return null;
    }

    @Override
    public void update() {
        boolean zombieAhead = Game.getInstance().hasZombieAhead(row, col);

        if (zombieAhead != wasZombieAhead) {
            wasZombieAhead = zombieAhead;
            currentFrameIndex = 0;
            animationTimer = 0;
            cooldown = 0;
            hasShot = false;
        }

        if (zombieAhead) {
            // Animation shooting frames
            if (!shootingFrames.isEmpty()) {
                animationTimer++;
                if (animationTimer >= shootFrameDelay) {
                    animationTimer = 0;
                    
                    // TĂNG currentFrameIndex TRƯỚC khi kiểm tra
                    currentFrameIndex = (currentFrameIndex + 1) % shootingFrames.size();
                    
                    // BẮN Ở FRAME CUỐI CÙNG (index = shootingFrames.size() - 1)
                    if (currentFrameIndex == shootingFrames.size() - 1 && !hasShot) {
                        // Bắn 2 viên đạn
                        Game.getInstance().bullets.add(new Bullet(row, col * 100 + 90, false));
                        Game.getInstance().bullets.add(new Bullet(row, col * 100 + 70, false));
                        hasShot = true;
                        System.out.println("Repeater SHOT at frame " + currentFrameIndex);
                    }
                    
                    // Reset hasShot khi bắt đầu chu kỳ mới (frame 0)
                    if (currentFrameIndex == 0) {
                        hasShot = false;
                    }
                }
            }
        } else {
            cooldown = 0;
            hasShot = false;
            
            if (!idleFrames.isEmpty()) {
                animationTimer++;
                if (animationTimer >= idleFrameDelay) {
                    animationTimer = 0;
                    currentFrameIndex = (currentFrameIndex + 1) % idleFrames.size();
                }
            }
        }
    }

    public void draw(Graphics g) {
        int drawX = col * 100 + 15;
        int drawY = row * 100 + 100 + 15;

        List<Image> currentFrames = wasZombieAhead ? shootingFrames : idleFrames;
        if (currentFrames.isEmpty()) {
            currentFrames = idleFrames;
        }

        if (!currentFrames.isEmpty() && currentFrameIndex < currentFrames.size() && currentFrames.get(currentFrameIndex) != null) {
            g.drawImage(currentFrames.get(currentFrameIndex), drawX, drawY, 70, 70, null);
        } else {
            g.setColor(new java.awt.Color(34, 139, 34));
            g.fillRect(drawX, drawY, 70, 70);
        }
    }
}