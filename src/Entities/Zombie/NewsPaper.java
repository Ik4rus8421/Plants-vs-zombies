package Entities.Zombie;

import Core.Game;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class NewsPaper extends Zombies {

    private enum ZombiePhase {
        FULL_NEWSPAPER,
        DAMAGED_NEWSPAPER,
        NO_NEWSPAPER,
        NO_ARM
    }

    private ZombiePhase currentPhase = ZombiePhase.FULL_NEWSPAPER;

    // Walking frames
    private final List<Image> walkFull = new ArrayList<>();
    private final List<Image> walkDamaged = new ArrayList<>();
    private final List<Image> walkNoPaper = new ArrayList<>();
    private final List<Image> walkNoArm = new ArrayList<>();
    
    // Eating frames
    private final List<Image> eatFull = new ArrayList<>();
    private final List<Image> eatDamaged = new ArrayList<>();
    private final List<Image> eatNoPaper = new ArrayList<>();
    private final List<Image> eatNoArm = new ArrayList<>();

    private int currentFrameIndex = 0;
    private int animationTimer = 0;
    private final int frameDelay = 3;
    
    private boolean isAttacking = false;
    private int biteCooldown = 0;

    public NewsPaper(int row) {
        super(row, 150, 1);
        loadWalkFrames();
        loadAttackFrames();
    }

    private void loadWalkFrames() {
        for (int i = 1; i <= 7; i++) {
            walkFull.add(getSprite("/image/zombie/newspaper/newspaperwalk/newspaperwalk/newspaperwalk" + i + ".png"));
            walkDamaged.add(getSprite("/image/zombie/newspaper/newspaperwalk/newspaperwalkd/newspaperwalkd" + i + ".png"));
            walkNoPaper.add(getSprite("/image/zombie/newspaper/newspaperwalk/newspaperwalkdd/newspaperwalkdd" + i + ".png"));
            walkNoArm.add(getSprite("/image/zombie/newspaper/newspaperwalk/newspaperwalkddd/newspaperwalkddd" + i + ".png"));
        }
    }

    private void loadAttackFrames() {
        for (int i = 1; i <= 7; i++) {
            eatFull.add(getSprite("/image/zombie/newspaper/newspapereat/newspapereat/newspapereat" + i + ".png"));
            eatDamaged.add(getSprite("/image/zombie/newspaper/newspapereat/newspapereatd/newspapereatd" + i + ".png"));
            eatNoPaper.add(getSprite("/image/zombie/newspaper/newspapereat/newspapereatdd/newspapereatdd" + i + ".png"));
            eatNoArm.add(getSprite("/image/zombie/newspaper/newspapereat/newspapereatddd/newspapereatddd" + i + ".png"));
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
                
                // CHỈ ĂN KHI ZOMBIE ĐỦ GẦN (cách plant dưới 40px)
                if (distance <= 10) {
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
        if (this.hp > 100) {
            targetPhase = ZombiePhase.FULL_NEWSPAPER;
        } else if (this.hp > 70) {
            targetPhase = ZombiePhase.DAMAGED_NEWSPAPER;
        } else if (this.hp > 35) {
            targetPhase = ZombiePhase.NO_NEWSPAPER;
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
                case DAMAGED_NEWSPAPER: return walkDamaged;
                case NO_NEWSPAPER:      return walkNoPaper;
                case NO_ARM:            return walkNoArm;
                default:                return walkFull;
            }
        } else {
            switch (currentPhase) {
                case DAMAGED_NEWSPAPER: return eatDamaged;
                case NO_NEWSPAPER:      return eatNoPaper;
                case NO_ARM:            return eatNoArm;
                default:                return eatFull;
            }
        }
    }

    public void draw(Graphics g) {
        int baseX = (int) x;
        int baseY = row * 100 + 100;
        
        // SỬA OFFSET ở đây
        int X_OFFSET = 0;   // Đổi từ -30 thành 0
        int Y_OFFSET = -15; // Giữ nguyên hoặc tùy chỉnh

        List<Image> activeFrames = getActiveFrameList();

        if (!activeFrames.isEmpty() && currentFrameIndex < activeFrames.size() && activeFrames.get(currentFrameIndex) != null) {
            Image img = activeFrames.get(currentFrameIndex);
            img = applySlowFilter(img);
            g.drawImage(img, baseX + X_OFFSET, baseY + Y_OFFSET, 105, 105, null);
        } else {
            g.setColor(java.awt.Color.WHITE);
            g.fillRect(baseX, baseY + 20, 60, 60);
            g.setColor(java.awt.Color.BLACK);
            g.drawString("NEWS", baseX + 10, baseY + 55);
            if (slowTimer > 0) {
                g.setColor(new java.awt.Color(0, 200, 255, 180));
                g.fillRect(baseX, baseY + 20, 60, 60);
            }
        }
    }
}







// All 4 zombies have HP-based phase determination
// BrownSuit (HP: 100)
if (hp > 66) targetPhase = FULL_HP;
else if (hp > 33) targetPhase = HALF_HP;
else targetPhase = HEAD_LESS;

// ConeHead (HP: 280)
if (hp > 200) targetPhase = FULL_CONE;
else if (hp > 100) targetPhase = DAMAGED_CONE;
else if (hp > 50) targetPhase = NO_CONE;
else targetPhase = NO_HAND;

// BucketHead (HP: 400)
if (hp > 260) targetPhase = FULL_BUCKET;
else if (hp > 130) targetPhase = DAMAGED_BUCKET;
else if (hp > 50) targetPhase = NO_BUCKET;
else targetPhase = NO_ARM;

// NewsPaper (HP: 150)
if (hp > 100) targetPhase = FULL_NEWSPAPER;
else if (hp > 70) targetPhase = DAMAGED_NEWSPAPER;
else if (hp > 35) targetPhase = NO_NEWSPAPER;
else targetPhase = NO_ARM;




// Reset animation when phase changes
if (targetPhase != currentPhase) {
    currentPhase = targetPhase;
    currentFrameIndex = 0;
    animationTimer = 0;
}

// All zombies check distance to plant before eating
int myCol = (int) (x / 100);
double plantCenter = myCol * 100 + 50;
double distance = Math.abs(x - plantCenter);

// BrownSuit & BucketHead: distance <= 15
// ConeHead: distance <= 25
// NewsPaper: distance <= 10
if (distance <= 15) {  // Threshold varies by zombie type
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
}

// Only move when not eating
if (!foundPlantToEat) {
    isAttacking = false;
    super.update();  // Calls parent move: x -= speed
}


// Each zombie has separate walking and 
// eating frame lists per phase
private List<Image> getActiveFrameList() {
    if (!isAttacking) {
        // Walking frames based on current phase
        switch (currentPhase) {
            case DAMAGED: return walkDamagedFrames;
            case NO_ARM: return walkNoArmFrames;
            default: return walkFullFrames;
        }
    } else {
        // Eating frames based on current phase
        switch (currentPhase) {
            case DAMAGED: return eatDamagedFrames;
            case NO_ARM: return eatNoArmFrames;
            default: return eatFullFrames;
        }
    }
}



// PoleVaulting.java - Jump parabola calculation
private void updateJump() {
    // Move forward during jump
    x -= jumpSpeed;
    
    // Parabola height calculation
    double progress = (double) jumpFrameIndex / (jumpDuration - 1);
    // Peak at middle: -120 * 4 * progress * (1 - progress)
    jumpOffsetY = -120 * (4 * progress * (1 - progress));
    
    jumpTimer++;
    if (jumpTimer >= jumpFrameDelay) {
        jumpTimer = 0;
        jumpFrameIndex++;
        
        List<Image> jumpFrames = (currentPhase == ZombiePhase.WITH_POLE_FULL) 
            ? jumpWithPoleFull : jumpWithPoleDamaged;
        
        if (jumpFrameIndex >= jumpFrames.size()) {
            // Jump completed
            isJumping = false;
            jumped = true;
            speed = 1;
            jumpOffsetY = 0;
            jumpedPlantRow = jumpTargetRow;
            jumpedPlantCol = jumpTargetCol;
        }
    }
}


