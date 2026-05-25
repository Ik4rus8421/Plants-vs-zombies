package Entities.Zombie;

import Core.Game;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class BrownSuit extends Zombies {

    private enum ZombiePhase {
        FULL_HP,
        HALF_HP,
        HEAD_LESS,
    }
    private ZombiePhase currentPhase = ZombiePhase.FULL_HP;

    // Walking frames containers
    private final List<Image> walkFullHP = new ArrayList<>();
    private final List<Image> walkHalfHP = new ArrayList<>();
    private final List<Image> walkHeadLess = new ArrayList<>();

    // Eating frames containers
    private final List<Image> eatFullHP = new ArrayList<>();
    private final List<Image> eatHalfHP = new ArrayList<>();
    private final List<Image> eatHeadLess = new ArrayList<>();

    // Ticking mechanics
    private int currentFrameIndex = 0;
    private int animationTimer = 0;
    private final int frameDelay = 3;

    // Eating behavior
    private boolean isEating = false;
    private int biteCooldown = 0;

    public BrownSuit(int row) {
        super(row, 100, 1);
        loadWalkFrames();
        loadEatFrames();
    }

    private void loadWalkFrames(){
        for (int i = 1; i <= 7; i++){
            walkFullHP.add(getSprite("/image/zombie/zombie/zombiewalk/zombiewalk" + i + ".png"));
            walkHalfHP.add(getSprite("/image/zombie/zombie/armlesszombie/armlesszombie" + i + ".png"));
            walkHeadLess.add(getSprite("/image/zombie/zombie/zombieheadless/zombieheadless" + i + ".png"));
        }
    }

    private void loadEatFrames(){
        for (int i = 1; i <= 7; i++){
            eatFullHP.add(getSprite("/image/zombie/zombie/zombieeat/zombieeat" + i + ".png"));
            eatHalfHP.add(getSprite("/image/zombie/zombie/armlesszombieeat/armlesszombieeat" + i + ".png"));
            eatHeadLess.add(getSprite("/image/zombie/zombie/headlesszombieeat/headlesszombieeat" + i + ".png"));
        }
    }

    private Image getSprite(String path){
        try {
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL != null) return new ImageIcon(imgURL).getImage();
            System.err.println("File missing: " + path);
        } catch (Exception e){
            System.err.println("Error reading resource: " + path);
        }
        return null;
    }

    @Override
    public void update() {
        // Find if there's a plant inside our current cell spot to eat
        int myCol = (int) (x / 100);
        boolean foundPlantToEat = false;

        if (myCol >= 0 && myCol < Game.getInstance().grid.cols && row >= 0 && row < Game.getInstance().grid.rows) {
            var cell = Game.getInstance().grid.cells[row][myCol];

            if (cell.plant != null) {
                // TÍNH KHOẢNG CÁCH ĐẾN PLANT
                double plantCenter = myCol * 100 + 50;
                double distance = Math.abs(x - plantCenter);
                
                // GIẢM XUỐNG 15px - RẤT GẦN
                if (distance <= 15) {  // Đổi từ 40 thành 15
                    foundPlantToEat = true;

                    // Reset frame timing when transitioning into eating state
                    if (!isEating) {
                        currentFrameIndex = 0;
                        animationTimer = 0;
                    }
                    isEating = true;

                    // Munch/Bite down on plant health pool over time ticks
                    biteCooldown++;
                    if (biteCooldown >= 20) {
                        cell.plant.hp -= 20;
                        biteCooldown = 0;

                        // Clear plant from grid if health bottoms out
                        if (cell.plant.hp <= 0) {
                            cell.plant = null;
                            isEating = false;
                        }
                    }
                } else {
                    // CHƯA ĐỦ GẦN - KHÔNG ĂN, TIẾP TỤC DI CHUYỂN
                    foundPlantToEat = false;
                }
            }
        }

        // Only move forward down the lane if NOT busy eating a plant
        if (!foundPlantToEat) {
            if (isEating) {
                currentFrameIndex = 0;
                animationTimer = 0;
            }
            isEating = false;
            super.update(); // Keeps baseline walking physics alive
        }

        // Phần còn lại giữ nguyên...
        ZombiePhase targetPhase;
        if (this.hp > 66) {
            targetPhase = ZombiePhase.FULL_HP;
        } else if (this.hp > 33) {
            targetPhase = ZombiePhase.HALF_HP;
        } else {
            targetPhase = ZombiePhase.HEAD_LESS;
        }

        if (targetPhase != currentPhase) {
            currentPhase = targetPhase;
            currentFrameIndex = 0;
            animationTimer = 0;
        }

        List<Image> activeFrames = getActiveFrameList();

        if (!activeFrames.isEmpty()) {
            if (currentFrameIndex >= activeFrames.size()) {
                currentFrameIndex = 0;
            }

            animationTimer++;
            if (animationTimer >= frameDelay) {
                animationTimer = 0;
                currentFrameIndex = (currentFrameIndex + 1) % activeFrames.size();
            }
        }
    }

    private List<Image> getActiveFrameList() {
        if (!isEating) {
            switch (currentPhase) {
                case HALF_HP:   return walkHalfHP;
                case HEAD_LESS: return walkHeadLess;
                case FULL_HP:
                default:        return walkFullHP;
            }
        } else {
            switch (currentPhase) {
                case HALF_HP:   return eatHalfHP;
                case HEAD_LESS: return eatHeadLess;
                case FULL_HP:
                default:        return eatFullHP;
            }
        }
    }

    public void draw(Graphics g) {
        int baseX = (int) x;
        int baseY = row * 100 + 100;

        int X_OFFSET = 0;
        int Y_OFFSET = -15;

        List<Image> activeFrames = getActiveFrameList();

        if (!activeFrames.isEmpty() && currentFrameIndex < activeFrames.size() && activeFrames.get(currentFrameIndex) != null) {
            Image img = activeFrames.get(currentFrameIndex);
            img = applySlowFilter(img);  // ← CHỈ THÊM DÒNG NÀY
            g.drawImage(img, baseX + X_OFFSET, baseY + Y_OFFSET, null);
        } else {
            g.setColor(java.awt.Color.ORANGE);
            g.fillRect(baseX, baseY + 20, 60, 60);
            if (slowTimer > 0) {
                g.setColor(new java.awt.Color(0, 200, 255, 180));
                g.fillRect(baseX, baseY + 20, 60, 60);
            }
        }
    }
}