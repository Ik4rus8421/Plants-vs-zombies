package Entities.Zombie;

import Core.Game;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class PoleVaulting extends Zombies {

    private enum ZombiePhase {
        WITH_POLE_FULL,
        WITH_POLE_DAMAGED,
        WITHOUT_POLE_FULL,
        WITHOUT_POLE_DAMAGED
    }

    // =========================
    // STATE
    // =========================

    private ZombiePhase currentPhase = ZombiePhase.WITH_POLE_FULL;

    private boolean jumped = false;
    private boolean isJumping = false;
    private boolean isAttacking = false;

    // =========================
    // JUMP DATA
    // =========================

    private int jumpTargetRow = -1;
    private int jumpTargetCol = -1;

    private int jumpedPlantRow = -1;
    private int jumpedPlantCol = -1;

    private double jumpSpeed = 8;

    // =========================
    // ANIMATION
    // =========================

    private int currentFrameIndex = 0;
    private int animationTimer = 0;

    private int jumpFrameIndex = 0;
    private int jumpTimer = 0;

    private int biteCooldown = 0;

    // =========================
    // CONFIG
    // =========================

    private final int frameDelay = 3;
    private final int jumpFrameDelay = 2;

    private static final int TILE_SIZE = 100;
    private static final int FRONT_OFFSET = 30;
    private double jumpOffsetY = 0;
    private int jumpDuration = 12;

    // =========================
    // SPRITES
    // =========================

    private final List<Image> walkWithPoleFull = new ArrayList<>();
    private final List<Image> walkWithPoleDamaged = new ArrayList<>();

    private final List<Image> walkWithoutPoleFull = new ArrayList<>();
    private final List<Image> walkWithoutPoleDamaged = new ArrayList<>();

    private final List<Image> eatWithoutPoleFull = new ArrayList<>();
    private final List<Image> eatWithoutPoleDamaged = new ArrayList<>();

    private final List<Image> jumpWithPoleFull = new ArrayList<>();
    private final List<Image> jumpWithPoleDamaged = new ArrayList<>();

    // =========================
    // CONSTRUCTOR
    // =========================

    public PoleVaulting(int row) {

        super(row, 175, 2);

        loadWalkFrames();
        loadEatFrames();
        loadJumpFrames();
    }

    // =========================
    // UPDATE
    // =========================

    @Override
    public void update() {

        // =========================
        // SLOW EFFECT
        // =========================

        if (slowTimer > 0) {
            slowTimer--;
        } else {
            speed = originalSpeed;
        }

        // =========================
        // UPDATE PHASE
        // =========================

        ZombiePhase targetPhase;

        if (!jumped) {

            targetPhase = (hp > 80)
                    ? ZombiePhase.WITH_POLE_FULL
                    : ZombiePhase.WITH_POLE_DAMAGED;

        } else {

            targetPhase = (hp > 80)
                    ? ZombiePhase.WITHOUT_POLE_FULL
                    : ZombiePhase.WITHOUT_POLE_DAMAGED;
        }

        if (targetPhase != currentPhase) {

            currentPhase = targetPhase;

            currentFrameIndex = 0;
            animationTimer = 0;
        }

        // =========================
        // HANDLE JUMP
        // =========================

        if (isJumping) {

            updateJump();

            return;
        }

        // =========================
        // DETECT PLANT
        // =========================

        int col = (int) ((x + FRONT_OFFSET) / TILE_SIZE);

        if (!jumped
                && col >= 0
                && col < Game.getInstance().grid.cols) {

            var cell = Game.getInstance().grid.cells[row][col];

            if (cell.plant != null) {

                String plantName =
                        cell.plant.getClass().getSimpleName();

                // =========================
                // SPIKEWEED / SPIKEROCK
                // =========================

                if (plantName.equalsIgnoreCase("Spikeweed")
                        || plantName.equalsIgnoreCase("Spikerock")) {

                    x -= speed;
                }

                // =========================
                // TALLNUT
                // =========================

                else if (plantName.equalsIgnoreCase("TallNut")) {

                    attackPlant(cell.plant);
                }

                // =========================
                // START JUMP
                // =========================

                else {

                    jumpTargetRow = row;
                    jumpTargetCol = col;

                    isJumping = true;

                    jumpFrameIndex = 0;
                    jumpTimer = 0;

                    currentFrameIndex = 0;
                    animationTimer = 0;

                    return;
                }
            }
        }

        // =========================
        // ATTACK AFTER JUMP
        // =========================

        int myCol = (int) ((x + FRONT_OFFSET) / TILE_SIZE);

        boolean foundPlant = false;

        if (myCol >= 0
                && myCol < Game.getInstance().grid.cols
                && row >= 0
                && row < Game.getInstance().grid.rows) {

            var cell = Game.getInstance().grid.cells[row][myCol];

            if (cell.plant != null) {

                boolean isJumpedPlant =
                        row == jumpedPlantRow
                                && myCol == jumpedPlantCol;

                if (!isJumpedPlant) {

                    foundPlant = true;

                    attackPlant(cell.plant);
                }
            }
        }

        // =========================
        // MOVE
        // =========================

        if (!foundPlant) {

            if (isAttacking) {

                isAttacking = false;

                currentFrameIndex = 0;
                animationTimer = 0;
            }

            x -= speed;
        }

        // =========================
        // ANIMATION
        // =========================

        updateAnimation();
    }

    // =========================
    // UPDATE JUMP
    // =========================

    private void updateJump() {

        // =========================
        // MOVE FORWARD
        // =========================

        x -= jumpSpeed;

        // =========================
        // PARABOLA HEIGHT
        // =========================

        double progress =
                (double) jumpFrameIndex
                        / (jumpDuration - 1);

        // parabola:
        // peak at middle

        jumpOffsetY =
                -120 * (4 * progress * (1 - progress));

        jumpTimer++;

        if (jumpTimer >= jumpFrameDelay) {

            jumpTimer = 0;

            jumpFrameIndex++;

            List<Image> jumpFrames =
                    (currentPhase == ZombiePhase.WITH_POLE_FULL)
                            ? jumpWithPoleFull
                            : jumpWithPoleDamaged;

            if (jumpFrameIndex >= jumpFrames.size()) {

                isJumping = false;

                jumped = true;

                speed = 1;
                originalSpeed = 1;

                jumpOffsetY = 0;

                jumpedPlantRow = jumpTargetRow;
                jumpedPlantCol = jumpTargetCol;

                jumpTargetRow = -1;
                jumpTargetCol = -1;

                currentFrameIndex = 0;
                animationTimer = 0;
            }
        }
    }
    // =========================
    // ATTACK
    // =========================

    private void attackPlant(Entities.Plant.Plants plant) {
        // Tính khoảng cách đến plant
        int plantCol = plant.col;
        double plantCenter = plantCol * 100 + 50;
        double distance = Math.abs(x - plantCenter);
        
        // CHỈ TẤN CÔNG KHI ĐỦ GẦN (cách plant dưới 40px)
        if (distance > 40) {
            return;  // Chưa đủ gần, không tấn công
        }
        
        if (!isAttacking) {
            isAttacking = true;
            currentFrameIndex = 0;
            animationTimer = 0;
        }

        biteCooldown++;
        if (biteCooldown >= 20) {
            biteCooldown = 0;
            plant.hp -= 20;
            if (plant.hp <= 0) {
                removePlant(plant);
                isAttacking = false;
            }
        }
    }

    // =========================
    // REMOVE PLANT
    // =========================

    private void removePlant(Entities.Plant.Plants targetPlant) {

        for (int r = 0;
             r < Game.getInstance().grid.rows;
             r++) {

            for (int c = 0;
                 c < Game.getInstance().grid.cols;
                 c++) {

                var cell =
                        Game.getInstance().grid.cells[r][c];

                if (cell.plant == targetPlant) {

                    cell.plant = null;

                    return;
                }
            }
        }
    }

    // =========================
    // UPDATE ANIMATION
    // =========================

    private void updateAnimation() {

        List<Image> activeFrames =
                getActiveFrameList();

        if (activeFrames.isEmpty()) {
            return;
        }

        animationTimer++;

        if (animationTimer >= frameDelay) {

            animationTimer = 0;

            currentFrameIndex =
                    (currentFrameIndex + 1)
                            % activeFrames.size();
        }
    }

    // =========================
    // ACTIVE FRAMES
    // =========================

    private List<Image> getActiveFrameList() {

        if (isJumping) {

            return (currentPhase
                    == ZombiePhase.WITH_POLE_FULL)
                    ? jumpWithPoleFull
                    : jumpWithPoleDamaged;
        }

        if (isAttacking) {

            switch (currentPhase) {

                case WITHOUT_POLE_FULL:
                    return eatWithoutPoleFull;

                case WITHOUT_POLE_DAMAGED:
                    return eatWithoutPoleDamaged;

                default:
                    return eatWithoutPoleFull;
            }
        }

        switch (currentPhase) {

            case WITH_POLE_FULL:
                return walkWithPoleFull;

            case WITH_POLE_DAMAGED:
                return walkWithPoleDamaged;

            case WITHOUT_POLE_FULL:
                return walkWithoutPoleFull;

            case WITHOUT_POLE_DAMAGED:
                return walkWithoutPoleDamaged;

            default:
                return walkWithPoleFull;
        }
    }

    // =========================
    // DRAW
    // =========================

    public void draw(Graphics g) {

        int baseX = (int) x;
        int baseY = row * 100 + 100;

        Image img = null;

        if (isJumping) {

            List<Image> jumpFrames =
                    (currentPhase
                            == ZombiePhase.WITH_POLE_FULL)
                            ? jumpWithPoleFull
                            : jumpWithPoleDamaged;

            if (jumpFrameIndex < jumpFrames.size()) {

                img = jumpFrames.get(jumpFrameIndex);
            }

        } else {

            List<Image> activeFrames =
                    getActiveFrameList();

            if (!activeFrames.isEmpty()
                    && currentFrameIndex
                    < activeFrames.size()) {

                img =
                        activeFrames.get(currentFrameIndex);
            }
        }

        if (img != null) {

            img = applySlowFilter(img);
            int width = img.getWidth(null) * 2;
            int height = img.getHeight(null) * 2;

            g.drawImage(
                img,
                baseX,
                baseY - 15 + (int) jumpOffsetY,
                width,
                height,
                null
            );
        }
    }

    // =========================
    // LOAD WALK
    // =========================

    private void loadWalkFrames() {

        for (int i = 1; i <= 8; i++) {

            walkWithPoleFull.add(
                    getSprite(
                            "/image/zombie/polevaulting/withpole/polevaultingwalk/polevaultingwalk"
                                    + i + ".png"
                    )
            );

            walkWithPoleDamaged.add(
                    getSprite(
                            "/image/zombie/polevaulting/withpole/polevaultingwalkd/polevaultingwalkd"
                                    + i + ".png"
                    )
            );
        }

        for (int i = 1; i <= 7; i++) {

            walkWithoutPoleFull.add(
                    getSprite(
                            "/image/zombie/polevaulting/withoutpole/polevaultingwalk/polevaultingwalk"
                                    + i + ".png"
                    )
            );

            walkWithoutPoleDamaged.add(
                    getSprite(
                            "/image/zombie/polevaulting/withoutpole/polevaultingwalkd/polevaultingwalkd"
                                    + i + ".png"
                    )
            );
        }
    }

    // =========================
    // LOAD EAT
    // =========================

    private void loadEatFrames() {

        for (int i = 1; i <= 7; i++) {

            eatWithoutPoleFull.add(
                    getSprite(
                            "/image/zombie/polevaulting/withoutpole/polevaultingeat/polevaultingeat"
                                    + i + ".png"
                    )
            );
        }

        for (int i = 1; i <= 8; i++) {

            eatWithoutPoleDamaged.add(
                    getSprite(
                            "/image/zombie/polevaulting/withoutpole/polevaultingeatd/polevaultingeatd"
                                    + i + ".png"
                    )
            );
        }
    }

    // =========================
    // LOAD JUMP
    // =========================

    private void loadJumpFrames() {

        for (int i = 1; i <= 6; i++) {

            jumpWithPoleFull.add(
                    getSprite(
                            "/image/zombie/polevaulting/withpole/polevaultingjump/polevaultingjump"
                                    + i + ".png"
                    )
            );

            jumpWithPoleDamaged.add(
                    getSprite(
                            "/image/zombie/polevaulting/withpole/polevaultingjumpd/polevaultingjumpd"
                                    + i + ".png"
                    )
            );
        }
    }
    // Thêm method này vào PoleVaulting.java
    public boolean hasJumped() {
        return jumped;
    }
    // =========================
    // LOAD IMAGE
    // =========================

    private Image getSprite(String path) {

        try {

            java.net.URL imgURL =
                    getClass().getResource(path);

            if (imgURL != null) {

                return new ImageIcon(imgURL)
                        .getImage();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }
}