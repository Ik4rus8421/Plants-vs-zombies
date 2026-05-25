package Entities.Plant;

import Core.Game;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class Chomper extends Plants {

    private int cooldown = 0;
    private boolean isEating = false;
    private boolean hasEaten = false;
    
    // Lưu máu của zombie đã ăn
    private int eatenZombieHp = 0;
    
    // Animation frames
    private final List<Image> idleFrames = new ArrayList<>();
    private final List<Image> chewFrames = new ArrayList<>();
    private final List<Image> chompFrames = new ArrayList<>();
    
    // Card images
    private static Image cardReady;
    private static Image cardFaded;
    private static Image dragImage;
    
    private int currentFrameIndex = 0;
    private int animationTimer = 0;
    private int currentAction = 0; // 0: idle, 1: chew, 2: chomp
    private final double frameDelay = 3.0;
    
    public Chomper(int r, int c) {
        super(r, c);
        this.hp = 150;
        loadIdleFrames();
        loadChewFrames();
        loadChompFrames();
        loadCardImages();
        loadDragImage();
    }
    
    private void loadIdleFrames() {
        for (int i = 1; i <= 9; i++) {
            String path = "/image/plant/chomper/chomper/chomper" + i + ".png";
            try {
                java.net.URL imgURL = getClass().getResource(path);
                if (imgURL != null) {
                    idleFrames.add(new ImageIcon(imgURL).getImage());
                }
            } catch (Exception e) {}
        }
        if (idleFrames.isEmpty()) {
            idleFrames.add(null);
        }
    }
    
    private void loadChewFrames() {
        for (int i = 1; i <= 3; i++) {
            String path = "/image/plant/chomper/chomperchew/chomperchew" + i + ".png";
            try {
                java.net.URL imgURL = getClass().getResource(path);
                if (imgURL != null) {
                    chewFrames.add(new ImageIcon(imgURL).getImage());
                }
            } catch (Exception e) {}
        }
    }
    
    private void loadChompFrames() {
        for (int i = 1; i <= 6; i++) {
            String path = "/image/plant/chomper/chomperchomp/chomperchomp" + i + ".png";
            try {
                java.net.URL imgURL = getClass().getResource(path);
                if (imgURL != null) {
                    chompFrames.add(new ImageIcon(imgURL).getImage());
                }
            } catch (Exception e) {}
        }
    }
    
    private static void loadCardImages() {
        if (cardReady == null) {
            try {
                java.net.URL readyURL = Chomper.class.getResource("/image/plant/chomper/chomperpacket/chomperpacket1.png");
                java.net.URL fadedURL = Chomper.class.getResource("/image/plant/chomper/chomperpacket/chomperpacket2.png");
                if (readyURL != null) cardReady = new ImageIcon(readyURL).getImage();
                if (fadedURL != null) cardFaded = new ImageIcon(fadedURL).getImage();
            } catch (Exception e) {}
        }
    }
    
    private static void loadDragImage() {
        if (dragImage == null) {
            try {
                java.net.URL dragURL = Chomper.class.getResource("/image/plant/chomper/chomper/chomper1.png");
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
    
    // Tính thời gian nhai dựa trên máu zombie
    private int calculateCooldownByHp(int zombieHp) {
        // Mỗi 10 máu = 5 tick cooldown (có thể điều chỉnh)
        // Zombie thường: 100 máu -> 50 tick
        // Conehead: 280 máu -> 140 tick
        // Buckethead: 400 máu -> 200 tick
        return (zombieHp / 10) * 5;
    }
    
    @Override
    public void update() {
        // Kiểm tra cooldown sau khi ăn xong
        if (cooldown > 0) {
            cooldown--;
            if (cooldown == 0) {
                isEating = false;
                currentAction = 0;
                currentFrameIndex = 0;
                animationTimer = 0;
                hasEaten = false;
                eatenZombieHp = 0;
            }
        }
        
        // Chỉ tìm zombie để ăn khi không đang trong cooldown
        if (cooldown == 0 && !isEating) {
            for (int i = 0; i < Game.getInstance().Zombies.size(); i++) {
                var z = Game.getInstance().Zombies.get(i);
                double zombiePos = z.x;
                double chomperPos = col * 100 + 50;
                double distance = Math.abs(zombiePos - chomperPos);
                
                if (z.row == row && distance <= 50) {
                    // Lưu máu zombie trước khi ăn
                    eatenZombieHp = z.hp;
                    isEating = true;
                    currentAction = 2;
                    currentFrameIndex = 0;
                    animationTimer = 0;
                    hasEaten = false;
                    break;
                }
            }
        }
        
        // Animation state machine
        List<Image> currentFrames = getCurrentFrames();
        
        if (!currentFrames.isEmpty() && currentFrames.get(0) != null) {
            animationTimer++;
            if (animationTimer >= frameDelay) {
                animationTimer = 0;
                currentFrameIndex++;
                
                // Xử lý khi đến frame chomperchomp2.png (index = 1)
                if (currentAction == 2 && currentFrameIndex == 1 && !hasEaten) {
                    for (int i = 0; i < Game.getInstance().Zombies.size(); i++) {
                        var z = Game.getInstance().Zombies.get(i);
                        double zombiePos = z.x;
                        double chomperPos = col * 100 + 50;
                        double distance = Math.abs(zombiePos - chomperPos);
                        
                        if (z.row == row && distance <= 50) {
                            Game.getInstance().Zombies.remove(i);
                            hasEaten = true;
                            break;
                        }
                    }
                }
                
                // Khi hết chomp frames, chuyển sang chew với cooldown dựa trên máu zombie
                if (currentAction == 2 && currentFrameIndex >= chompFrames.size()) {
                    currentAction = 1;
                    currentFrameIndex = 0;
                    // Tính cooldown dựa trên máu zombie đã ăn
                    cooldown = calculateCooldownByHp(eatenZombieHp);
                    System.out.println("Chomper ate zombie with " + eatenZombieHp + " HP, cooldown = " + cooldown);
                }
                
                // CHEW ANIMATION: Loop liên tục
                if (currentAction == 1 && currentFrameIndex >= chewFrames.size()) {
                    currentFrameIndex = 0;  // Loop lại từ đầu
                }
                
                // IDLE ANIMATION: Loop liên tục
                if (currentAction == 0 && currentFrameIndex >= idleFrames.size()) {
                    currentFrameIndex = 0;
                }
            }
        }
    }
    
    private List<Image> getCurrentFrames() {
        if (!isEating) {
            return idleFrames;
        } else if (currentAction == 2) {
            return chompFrames;
        } else {
            return chewFrames;
        }
    }
    
    public void draw(Graphics g) {
        int size = 80;
        int offset = (100 - size) / 2;
        int drawX = col * 100 + offset;
        int drawY = row * 100 + 100 + offset;
        
        List<Image> currentFrames = getCurrentFrames();
        
        if (!currentFrames.isEmpty() && currentFrameIndex < currentFrames.size() && currentFrames.get(currentFrameIndex) != null) {
            g.drawImage(currentFrames.get(currentFrameIndex), drawX, drawY, size, size, null);
        } else {
            g.setColor(java.awt.Color.MAGENTA);
            g.fillRect(drawX, drawY, size, size);
            g.setColor(java.awt.Color.BLACK);
            g.drawString("CH", drawX + size/2 - 8, drawY + size/2 + 5);
        }
    }
    
    public boolean isEating() {
        return cooldown > 0;
    }
}