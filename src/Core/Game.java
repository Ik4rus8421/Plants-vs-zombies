package Core;

import Entities.Bullet.Bullet;
import Entities.Others.LawnMower;
import Entities.Others.Sun;
import Entities.Zombie.*;
import Map.Grid;
import java.util.ArrayList;

public class Game {

    private static Game instance;

    public ArrayList<Bullet> bullets = new ArrayList<>();
    public ArrayList<Zombies> Zombies = new ArrayList<>();
    public ArrayList<Sun> suns = new ArrayList<>();

    public int sun = 1000;

    public Grid grid;
    private boolean[] hasLostMower = new boolean[5];

    public ArrayList<LawnMower> mowers = new ArrayList<>();
    
    // ===== SURVIVAL TIMER (dùng frame, 10 frame = 1 giây) =====
    private int survivalFrames = 0;
    private final int WIN_FRAMES = 1200 * 10;  // 240 giây * 10 frame/giây = 2400 frame
    private boolean gameWin = false;
    private boolean gameLose = false;

    // ===== CÀI ĐẶT GAME OVER =====
    private final double GAME_OVER_X_LIMIT = 0;
    
    // Thêm phương thức
    public boolean isGameLose() {
        return gameLose;
    }

    public void setGameLose(boolean lose) {
        this.gameLose = lose;
    }

    private Game() {
        grid = new Grid(5, 9);
        for (int i = 0; i < grid.rows; i++) {
            mowers.add(new LawnMower(i));
        }
    }

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    private void handleCollision() {
        for (int i = 0; i < bullets.size(); i++) {
            Bullet b = bullets.get(i);
            if (!b.isMoving()) continue;

            for (int j = 0; j < Zombies.size(); j++) {
                Zombies z = Zombies.get(j);
                double zombieTorsoLeft = z.x + 20;
                double zombieTorsoRight = z.x + 45;

                if (b.row == z.row && b.x >= zombieTorsoLeft && b.x <= zombieTorsoRight) {
                    z.hp -= 20;
                    if (b.slow) {
                        z.speed = z.originalSpeed * 0.5;
                        z.slowTimer = 50;
                    }
                    b.explode();
                    if (z.hp <= 0) {
                        Zombies.remove(j);
                        j--;
                    }
                    break;
                }
            }
        }
    }

    public void update() {
        // ===== KIỂM TRA KẾT THÚC GAME =====
        if (gameWin || gameLose) {
            return;
        }
        
        // ===== CẬP NHẬT TIMER =====
        survivalFrames++;
        if (survivalFrames >= WIN_FRAMES) {
            gameWin = true;
            System.out.println("=== VICTORY! You survived! ===");
            return;
        }
        
        // ===== RANDOM SUN =====
        if (Math.random() < 0.01) {
            int col = (int)(Math.random() * grid.cols);
            int x = col * 100 + 40;
            int targetY = (int)(Math.random() * grid.rows) * 100 + 140;
            suns.add(new Sun(x, 0, targetY));
        }

        // ===== UPDATE SUN =====
        for (int i = 0; i < suns.size(); i++) {
            suns.get(i).update();
            if (suns.get(i).isDead()) {
                suns.remove(i);
                i--;
            }
        }

        // ===== SPAWN ZOMBIE =====
        if (Math.random() < 0.02) {
            int row = (int)(Math.random() * grid.rows);
            int type = (int)(Math.random() * 5);
            switch (type) {
                case 0: Zombies.add(new BrownSuit(row)); break;
                case 1: Zombies.add(new ConeHead(row)); break;
                case 2: Zombies.add(new BucketHead(row)); break;
                case 3: Zombies.add(new NewsPaper(row)); break;
                case 4: Zombies.add(new PoleVaulting(row)); break;
            }
        }

        grid.update();

        // ===== UPDATE ZOMBIES =====
        for (int i = 0; i < Zombies.size(); i++) {
            Zombies.get(i).update();
        }

        // ===== REMOVE DEAD ZOMBIES =====
        for (int i = 0; i < Zombies.size(); i++) {
            if (Zombies.get(i).hp <= 0) {
                Zombies.remove(i);
                i--;
            }
        }

        // ===== UPDATE BULLETS =====
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).update();
            if (bullets.get(i).x > 1000 || bullets.get(i).shouldRemove) {
                bullets.remove(i);
                i--;
            }
        }

        // ===== UPDATE MOWERS =====
        for (LawnMower mower : mowers) {
            mower.update();
        }

        // ===== ACTIVATE MOWER =====
        for (int i = 0; i < Zombies.size(); i++) {
            Zombies z = Zombies.get(i);
            LawnMower mower = mowers.get(z.row);

            if (!mower.active && !mower.used) {
                if (z.x <= 0) {
                    mower.active = true;
                    mower.used = true;
                    System.out.println("LawnMower ACTIVATED at row " + z.row);
                }
            }
        }

        // ===== MOWER COLLISION =====
        for (LawnMower mower : mowers) {
            if (mower.active) {
                int mowerCol = (int)(mower.gameX / 100);
                
                for (int i = 0; i < Zombies.size(); i++) {
                    Zombies z = Zombies.get(i);
                    if (z.row == mower.row) {
                        int zombieCol = (int)(z.x / 100);
                        
                        if (mowerCol == zombieCol) {
                            Zombies.remove(i);
                            i--;
                            System.out.println("LawnMower KILLED zombie at row " + z.row + ", col " + zombieCol);
                        }
                    }
                }
            }
        }

        handleCollision();

        // ===== CHECK GAME OVER =====
        for (Zombies z : Zombies) {
            if (z.x <= GAME_OVER_X_LIMIT) {
                int row = z.row;

                if (hasLostMower[row]) {
                    if (!gameLose && !gameWin) {
                        gameLose = true;
                        System.out.println("GAME OVER - No lawnmower at row " + row);
                    }
                } else {
                    hasLostMower[row] = true;
                    System.out.println("LawnMower used at row " + row);
                    LawnMower mower = mowers.get(row);
                    mower.active = true;
                    mower.used = true;
                }
            }
        }
    }

    public boolean hasZombieAhead(int row, int col) {
        for (var z : Zombies) {
            int zCol = (int)(z.x / 100);
            if (z.row == row && zCol >= col) {
                return true;
            }
        }
        return false;
    }
    
    // ===== GETTER CHO TIMER =====
    public int getRemainingSeconds() {
        int remainingFrames = WIN_FRAMES - survivalFrames;
        return Math.max(0, remainingFrames / 10);
    }
    
    public boolean isGameWin() {
        return gameWin;
    }
    
    public static void resetInstance() {
        instance = null;
    }
    
    public static void setInstance(Game newInstance) {
        instance = newInstance;
    }
    
    // ===== RESET DATA =====
    public void resetData() {
        bullets.clear();
        Zombies.clear();
        suns.clear();
        
        sun = 1000;
        
        survivalFrames = 0;
        gameWin = false;
        gameLose = false;
        
        for (int i = 0; i < grid.rows; i++) {
            for (int j = 0; j < grid.cols; j++) {
                grid.cells[i][j].plant = null;
            }
        }
        
        for (LawnMower mower : mowers) {
            mower.active = false;
            mower.used = false;
            mower.gameX = -100;
            mower.lawnX = LawnMower.MAP_LEFT - 100;
        }
        
        for (int i = 0; i < hasLostMower.length; i++) {
            hasLostMower[i] = false;
        }
    }
}