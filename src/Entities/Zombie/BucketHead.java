package Entities.Zombie;

import Core.Game;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class BucketHead extends Zombies {

    private enum ZombiePhase {
        FULL_BUCKET,
        DAMAGED_BUCKET,
        NO_BUCKET,
        NO_ARM
    }

    private ZombiePhase currentPhase = ZombiePhase.FULL_BUCKET;

    // Walking frames
    private final List<Image> walkFullBucket = new ArrayList<>();
    private final List<Image> walkDamagedBucket = new ArrayList<>();
    private final List<Image> walkNoBucket = new ArrayList<>();
    private final List<Image> walkNoArm = new ArrayList<>();
    
    // Eating frames
    private final List<Image> eatFullBucket = new ArrayList<>();
    private final List<Image> eatDamagedBucket = new ArrayList<>();
    private final List<Image> eatNoBucket = new ArrayList<>();
    private final List<Image> eatNoArm = new ArrayList<>();

    private int currentFrameIndex = 0;
    private int animationTimer = 0;
    private final int frameDelay = 3;
    
    private boolean isAttacking = false;
    private int biteCooldown = 0;

    public BucketHead(int row) {
        super(row, 400, 1);
        loadWalkFrames();
        loadAttackFrames();
    }

    private void loadWalkFrames() {
        for (int i = 1; i <= 7; i++) {
            walkFullBucket.add(getSprite("/image/zombie/buckethead/bucketheadwalk/bucketheadwalk/bucketheadwalk" + i + ".png"));
            walkDamagedBucket.add(getSprite("/image/zombie/buckethead/bucketheadwalk/bucketheadwalkd/bucketheadwalkd" + i + ".png"));
            walkNoBucket.add(getSprite("/image/zombie/zombie/zombiewalk/zombiewalk" + i + ".png"));
            walkNoArm.add(getSprite("/image/zombie/zombie/armlesszombie/armlesszombie" + i + ".png"));
        }
    }

    private void loadAttackFrames() {
        for (int i = 1; i <= 7; i++) {
            eatFullBucket.add(getSprite("/image/zombie/buckethead/bucketheadeat/bucketheadeat/bucketheadeat" + i + ".png"));
            eatNoBucket.add(getSprite("/image/zombie/zombie/zombieeat/zombieeat" + i + ".png"));
        }
        for (int i = 1; i <= 7; i++) {
            eatDamagedBucket.add(getSprite("/image/zombie/buckethead/bucketheadeat/bucketheadeatd/bucketheadeatd" + i + ".png"));
        }
        for (int i = 1; i <= 7; i++) {
            eatDamagedBucket.add(getSprite("/image/zombie/buckethead/bucketheadeat/bucketheadeatdd/bucketheadeatdd" + i + ".png"));
        }
        for (int i = 1; i <= 7; i++) {
            eatNoArm.add(getSprite("/image/zombie/zombie/armlesszombieeat/armlesszombieeat" + i + ".png"));
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
                // TÍNH KHOẢNG CÁCH ĐẾN PLANT
                double plantCenter = myCol * 100 + 50;
                double distance = Math.abs(x - plantCenter);
                
                // GIẢM XUỐNG 15px - RẤT GẦN
                if (distance <= 15) {  // Đổi từ 40 thành 15
                    foundPlantToEat = true;
                    if (!isAttacking) {
                        currentFrameIndex = 0;
                        animationTimer = 0;
                    }
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
                    // CHƯA ĐỦ GẦN - TIẾP TỤC DI CHUYỂN
                    foundPlantToEat = false;
                }
            }
        }

        if (!foundPlantToEat) {
            if (isAttacking) {
                currentFrameIndex = 0;
                animationTimer = 0;
            }
            isAttacking = false;
            super.update();
        }

        // Phần còn lại giữ nguyên...
        ZombiePhase targetPhase;
        if (this.hp > 260) {
            targetPhase = ZombiePhase.FULL_BUCKET;
        } else if (this.hp > 130) {
            targetPhase = ZombiePhase.DAMAGED_BUCKET;
        } else if (this.hp > 50) {
            targetPhase = ZombiePhase.NO_BUCKET;
        } else {
            targetPhase = ZombiePhase.NO_ARM;
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
                currentFrameIndex = (currentFrameIndex + 1) % activeFrames.size();
            }
        }
    }

    private List<Image> getActiveFrameList() {
        if (!isAttacking) {
            switch (currentPhase) {
                case DAMAGED_BUCKET: return walkDamagedBucket;
                case NO_BUCKET:      return walkNoBucket;
                case NO_ARM:         return walkNoArm;
                default:             return walkFullBucket;
            }
        } else {
            switch (currentPhase) {
                case DAMAGED_BUCKET: return eatDamagedBucket;
                case NO_BUCKET:      return eatNoBucket;
                case NO_ARM:         return eatNoArm;
                default:             return eatFullBucket;
            }
        }
    }

    public void draw(Graphics g) {
        int baseX = (int) x;
        int baseY = row * 100 + 100;
        int X_OFFSET = 0;
        int Y_OFFSET = -20;

        List<Image> activeFrames = getActiveFrameList();

        if (!activeFrames.isEmpty() && currentFrameIndex < activeFrames.size() && activeFrames.get(currentFrameIndex) != null) {
            Image img = activeFrames.get(currentFrameIndex);
            img = applySlowFilter(img);
            g.drawImage(img, baseX + X_OFFSET, baseY + Y_OFFSET, null);
        } else {
            g.setColor(java.awt.Color.GRAY);
            g.fillRect(baseX, baseY + 20, 60, 60);
            if (slowTimer > 0) {
                g.setColor(new java.awt.Color(0, 200, 255, 180));
                g.fillRect(baseX, baseY + 20, 60, 60);
            }
        }
    }
}