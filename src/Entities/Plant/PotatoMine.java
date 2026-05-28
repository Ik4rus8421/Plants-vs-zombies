package Entities.Plant;

import Core.Game;
import Entities.Zombie.PoleVaulting;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class PotatoMine extends Plants {

    private int timer = 0;
    private boolean armed = false;
    private boolean exploded = false;

    private final List<Image> growingFrames = new ArrayList<>();
    private final List<Image> armedFrames = new ArrayList<>();
    private int currentFrameIndex = 0;
    private int animationTimer = 0;

    private final double frameDelay = 12;

    public PotatoMine(int r, int c) {
        super(r, c);
        loadAnimationFrames();
    }

    private void loadAnimationFrames() {
        for (int i = 1; i <= 5; i++) {
            growingFrames.add(getSprite("/image/plant/potatomine/potatomine/potatomine" + i + ".png"));
        }
        for (int i = 1; i <= 3; i++) {
            armedFrames.add(getSprite("/image/plant/potatomine/potato/potato" + i + ".png"));
        }
    }

    private Image getSprite(String path) {
        try {
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL != null) return new ImageIcon(imgURL).getImage();
        } catch (Exception e) {}
        return null;
    }

    @Override
    public void update() {
        if (exploded) return;
        
        timer++;

        if (timer >= 50 && !armed) {
            armed = true;
            currentFrameIndex = 0;
            animationTimer = 0;
        }

        // Animation
        if (!armed) {
            if (!growingFrames.isEmpty()) {
                animationTimer++;
                if (animationTimer >= frameDelay) {
                    animationTimer = 0;
                    if (currentFrameIndex < growingFrames.size() - 1) {
                        currentFrameIndex++;
                    }
                }
            }
        } else {
            if (!armedFrames.isEmpty()) {
                animationTimer++;
                if (animationTimer >= frameDelay) {
                    animationTimer = 0;
                    currentFrameIndex = (currentFrameIndex + 1) % armedFrames.size();
                }
            }
        }

        if (!armed) return;

        // Potato mine hitbox (entire cell)
        double mineLeft = col * 100;
        double mineRight = col * 100 + 100;
        
        // Detect if any zombie overlaps the mine cell
        boolean shouldExplode = false;
        
        for (var z : Game.getInstance().Zombies) {
            if (z.row != row) continue;
            
            // Zombie hitbox
            double zombieLeft = z.x;
            double zombieRight = z.x + 60;  // Zombie width ~60px
            
            // Adjust for PoleVaulting that hasn't jumped yet (long pole)
            if (z instanceof PoleVaulting) {
                PoleVaulting pv = (PoleVaulting) z;
                if (!pv.hasJumped()) {
                    zombieLeft = z.x + 50;   // Ignore the pole
                    zombieRight = z.x + 110; // Body + pole
                }
            }
            
            // Check if hitboxes overlap
            if (zombieRight >= mineLeft && zombieLeft <= mineRight) {
                shouldExplode = true;
                break;
            }
        }
        
        if (shouldExplode) {
            // Remove ALL zombies that overlap the mine cell
            for (int i = Game.getInstance().Zombies.size() - 1; i >= 0; i--) {
                var z = Game.getInstance().Zombies.get(i);
                if (z.row != row) continue;
                
                double zombieLeft = z.x;
                double zombieRight = z.x + 60;
                
                if (z instanceof PoleVaulting) {
                    PoleVaulting pv = (PoleVaulting) z;
                    if (!pv.hasJumped()) {
                        zombieLeft = z.x + 50;
                        zombieRight = z.x + 110;
                    }
                }
                
                if (zombieRight >= mineLeft && zombieLeft <= mineRight) {
                    Game.getInstance().Zombies.remove(i);
                    System.out.println("PotatoMine killed " + z.getClass().getSimpleName());
                }
            }
            
            // Remove potato mine
            exploded = true;
            Game.getInstance().grid.cells[row][col].plant = null;
            System.out.println("PotatoMine EXPLODED at row " + row + ", col " + col);
        }
    }

    public void draw(Graphics g) {
        if (exploded) return;
        
        int drawX = col * 100 + 15;
        int drawY = row * 100 + 100 + 15;

        List<Image> activeFrames = armed ? armedFrames : growingFrames;

        if (!activeFrames.isEmpty() && currentFrameIndex < activeFrames.size() && activeFrames.get(currentFrameIndex) != null) {
            g.drawImage(activeFrames.get(currentFrameIndex), drawX, drawY, 70, 70, null);
        } else {
            g.setColor(armed ? java.awt.Color.RED : java.awt.Color.DARK_GRAY);
            g.fillRect(drawX, drawY, 70, 70);
        }
    }

    public boolean isArmed() {
        return armed;
    }
    
    public boolean isExploded() {
        return exploded;
    }
}