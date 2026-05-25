package Entities.Zombie;

import Core.Game;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class ConeHead extends Zombies {

    // Define the possible visual phases matching the structure of Buckethead
    private enum ZombiePhase {
        FULL_CONE,
        DAMAGED_CONE,
        NO_CONE,
        NO_HAND,
    }

    private ZombiePhase currentPhase = ZombiePhase.FULL_CONE;

    // Separate animation frame lists for each state track
    private final List<Image> walkFullCone = new ArrayList<>();
    private final List<Image> walkDamagedCone = new ArrayList<>();
    private final List<Image> walkNoCone = new ArrayList<>();
    private final List<Image> walkNoHand = new ArrayList<>();
    // Biting animation frame containers
    private final List<Image> attackFullCone = new ArrayList<>();
    private final List<Image> attackDamagedCone = new ArrayList<>();
    private final List<Image> attackNoCone = new ArrayList<>();
    private final List<Image> attackNoHand = new ArrayList<>();
    // Animation ticking mechanics
    private int currentFrameIndex = 0;
    private int animationTimer = 0;
    private final int frameDelay = 3; //

    // Eating behavior mechanics
    private boolean isAttacking = false;
    private int biteCooldown = 0;

    public ConeHead(int row) {
        super(row, 280, 1); // Row, HP=280 (scaled down from Buckethead's 400), Speed=1

        // Load all visual states into memory
        loadWalkFrames();
        loadAttackFrames();
    }

    private void loadWalkFrames() {
        for (int i = 1; i <= 7; i++) {
            // FULL_CONE - Nón lành
            walkFullCone.add(getSprite("/image/zombie/conehead/conheadwalk/coneheadwalk/coneheadwalk" + i + ".png"));
            // DAMAGED_CONE - Nón bị nứt
            walkDamagedCone.add(getSprite("/image/zombie/conehead/conheadwalk/coneheadwalkd/coneheadwalkd" + i + ".png"));
            // NO_CONE - Rụng nón (dùng zombie thường)
            walkNoCone.add(getSprite("/image/zombie/zombie/zombiewalk/zombiewalk" + i + ".png"));
            // NO_HAND - Mất tay
            walkNoHand.add(getSprite("/image/zombie/zombie/armlesszombie/armlesszombie" + i + ".png"));
        }
    }

    private void loadAttackFrames() {
        for (int i = 1; i <= 7; i++) {
            // FULL_CONE - Ăn khi còn nón
            attackFullCone.add(getSprite("/image/zombie/conehead/conheadeat/coneheadeat/coneheadeat" + i + ".png"));
            // NO_CONE - Ăn khi rụng nón
            attackNoCone.add(getSprite("/image/zombie/zombie/zombieeat/zombieeat" + i + ".png"));
        }
        for (int i = 1; i <= 14; i++) {
            // DAMAGED_CONE - Ăn khi nón bị nứt
            attackDamagedCone.add(getSprite("/image/zombie/conehead/conheadeat/coneheadeatd/coneheadeatd" + i + ".png"));
        }
        for (int i = 1; i <= 7; i++) {
            // NO_HAND - Ăn khi mất tay
            attackNoHand.add(getSprite("/image/zombie/zombie/armlesszombieeat/armlesszombieeat" + i + ".png"));
        }
    }

    private Image getSprite(String path) {
        try {
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL != null) return new ImageIcon(imgURL).getImage(); //
            System.err.println("File missing: " + path); //
        } catch (Exception e) {
            System.err.println("Error reading: " + path); //
        }
        return null; //
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
                
                // CHỈ ĂN KHI ZOMBIE ĐỦ GẦN (cách plant dưới 40px)
                if (distance <= 25) {
                    foundPlantToEat = true;
                    isAttacking = true;

                    // Munch/Bite down on plant health pool over time ticks
                    biteCooldown++;
                    if (biteCooldown >= 20) {
                        cell.plant.hp -= 20;
                        biteCooldown = 0;

                        // Clear plant from grid if health bottoms out
                        if (cell.plant.hp <= 0) {
                            cell.plant = null;
                            isAttacking = false;
                        }
                    }
                }
            }
        }

        // Only move forward down the lane if NOT busy eating a plant
        if (!foundPlantToEat) {
            isAttacking = false;
            super.update();
        }

        // Phần còn lại giữ nguyên...
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

                if (isAttacking && currentPhase == ZombiePhase.DAMAGED_CONE) {
                    if (currentFrameIndex < activeFrames.size() - 1) {
                        currentFrameIndex++;
                    }
                } else {
                    currentFrameIndex = (currentFrameIndex + 1) % activeFrames.size();
                }
            }
        }
    }

    /**
     * Shifts between walking lists and biting lists depending on status
     */
    private List<Image> getActiveFrameList() {
        if (!isAttacking) {
            switch (currentPhase) {
                case DAMAGED_CONE: return walkDamagedCone;
                case NO_CONE:      return walkNoCone;
                case FULL_CONE:
                default:           return walkFullCone;
            }
        } else {
            switch (currentPhase) {
                case DAMAGED_CONE: return attackDamagedCone;
                case NO_CONE:      return attackNoCone;
                case NO_HAND:      return attackNoHand;
                case FULL_CONE:
                default:           return attackFullCone;
            }
        }
    }

    public void draw(Graphics g) {
        int baseX = (int) x; //
        int baseY = row * 100 + 100; //

        int X_OFFSET = 0; //
        int Y_OFFSET = -15; //

        List<Image> activeFrames = getActiveFrameList(); //

        if (!activeFrames.isEmpty() && currentFrameIndex < activeFrames.size() && activeFrames.get(currentFrameIndex) != null) {
            Image img = activeFrames.get(currentFrameIndex);
            img = applySlowFilter(img);  // ← THÊM DÒNG NÀY
            g.drawImage(img, baseX + X_OFFSET, baseY + Y_OFFSET, null); //
        } else {
            // Fallback rendering default color code block if textures fail to load
            g.setColor(java.awt.Color.ORANGE);
            g.fillRect(baseX, baseY + 20, 60, 60);
            if (slowTimer > 0) {
                g.setColor(new java.awt.Color(0, 200, 255, 180));
                g.fillRect(baseX, baseY + 20, 60, 60);
            }
        }
    }
}