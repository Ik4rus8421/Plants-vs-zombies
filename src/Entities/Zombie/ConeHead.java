package Entities.Zombie;

import Core.Game;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class ConeHead extends Zombies {

    private enum ZombiePhase {
        FULL_CONE,
        DAMAGED_CONE,
        NO_CONE,
        NO_HAND,
    }

    private ZombiePhase currentPhase = ZombiePhase.FULL_CONE;

    // Walking frames
    private final List<Image> walkFullCone = new ArrayList<>();
    private final List<Image> walkDamagedCone = new ArrayList<>();
    private final List<Image> walkNoCone = new ArrayList<>();
    private final List<Image> walkNoHand = new ArrayList<>();
    
    // Eating frames
    private final List<Image> attackFullCone = new ArrayList<>();
    private final List<Image> attackDamagedCone = new ArrayList<>();
    private final List<Image> attackNoCone = new ArrayList<>();
    private final List<Image> attackNoHand = new ArrayList<>();
    
    // Animation
    private int currentFrameIndex = 0;
    private int animationTimer = 0;
    private final int frameDelay = 3;

    // Eating behavior
    private boolean isAttacking = false;
    private int biteCooldown = 0;

    public ConeHead(int row) {
        super(row, 280, 1);
        loadWalkFrames();
        loadAttackFrames();
    }

    private void loadWalkFrames() {
        for (int i = 1; i <= 7; i++) {
            walkFullCone.add(getSprite("/image/zombie/conehead/conheadwalk/coneheadwalk/coneheadwalk" + i + ".png"));
            walkDamagedCone.add(getSprite("/image/zombie/conehead/conheadwalk/coneheadwalkd/coneheadwalkd" + i + ".png"));
            walkNoCone.add(getSprite("/image/zombie/zombie/zombiewalk/zombiewalk" + i + ".png"));
            walkNoHand.add(getSprite("/image/zombie/zombie/armlesszombie/armlesszombie" + i + ".png"));
        }
    }

    private void loadAttackFrames() {
        for (int i = 1; i <= 7; i++) {
            attackFullCone.add(getSprite("/image/zombie/conehead/conheadeat/coneheadeat/coneheadeat" + i + ".png"));
            attackNoCone.add(getSprite("/image/zombie/zombie/zombieeat/zombieeat" + i + ".png"));
        }
        for (int i = 1; i <= 14; i++) {
            attackDamagedCone.add(getSprite("/image/zombie/conehead/conheadeat/coneheadeatd/coneheadeatd" + i + ".png"));
        }
        for (int i = 1; i <= 7; i++) {
            attackNoHand.add(getSprite("/image/zombie/zombie/armlesszombieeat/armlesszombieeat" + i + ".png"));
        }
    }

    private Image getSprite(String path) {
        try {
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL != null) return new ImageIcon(imgURL).getImage();
            System.err.println("File missing: " + path);
        } catch (Exception e) {
            System.err.println("Error reading: " + path);
        }
        return null;
    }

    @Override
    public void update() {
        int myCol = (int) (x / 100);
        boolean foundPlantToEat = false;

        if (myCol >= 0 && myCol < Game.getInstance().grid.cols && row >= 0 && row < Game.getInstance().grid.rows) {
            var cell = Game.getInstance().grid.cells[row][myCol];

            if (cell.plant != null) {
                double plantCenter = myCol * 100 + 50;
                double distance = Math.abs(x - plantCenter);
                
                // FIXED: Eat when close enough (25px)
                if (distance <= 25) {
                    foundPlantToEat = true;
                    isAttacking = true;

                    biteCooldown++;
                    if (biteCooldown >= 20) {
                        cell.plant.hp -= 20;
                        biteCooldown = 0;

                        if (cell.plant.hp <= 0) {
                            cell.plant = null;
                            isAttacking = false;
                        }
                    }
                } else {
                    // FIXED: Not close enough → continue moving
                    foundPlantToEat = false;
                }
            }
        }

        // FIXED: Only move when not eating
        if (!foundPlantToEat) {
            if (isAttacking) {
                currentFrameIndex = 0;
                animationTimer = 0;
            }
            isAttacking = false;
            super.update();
        }

        // Determine phase based on HP
        ZombiePhase targetPhase;
        if (this.hp > 200) {
            targetPhase = ZombiePhase.FULL_CONE;
        } else if (this.hp > 100) {
            targetPhase = ZombiePhase.DAMAGED_CONE;
        } else if (this.hp > 50) {
            targetPhase = ZombiePhase.NO_CONE;
        } else {
            targetPhase = ZombiePhase.NO_HAND;
        }

        // Reset animation when phase changes
        if (targetPhase != currentPhase) {
            currentPhase = targetPhase;
            currentFrameIndex = 0;
            animationTimer = 0;
        }

        List<Image> activeFrames = getActiveFrameList();

        if (!activeFrames.isEmpty()) {
            animationTimer++;
            if (animationTimer >= frameDelay) {
                animationTimer = 0;
                currentFrameIndex = (currentFrameIndex + 1) % activeFrames.size();
            }
        }
    }

    private List<Image> getActiveFrameList() {
        if (!isAttacking) {
            switch (currentPhase) {
                case DAMAGED_CONE: return walkDamagedCone;
                case NO_CONE:      return walkNoCone;
                case NO_HAND:      return walkNoHand;
                default:           return walkFullCone;
            }
        } else {
            switch (currentPhase) {
                case DAMAGED_CONE: return attackDamagedCone;
                case NO_CONE:      return attackNoCone;
                case NO_HAND:      return attackNoHand;
                default:           return attackFullCone;
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
            img = applySlowFilter(img);
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