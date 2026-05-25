package Core;

import Entities.Others.Shovel;
import Entities.Plant.*;
import Entities.Zombie.*;
import java.awt.*;
import javax.swing.*;

public class GamePanel extends JPanel {
    private int hoverRow = -1;
    private int hoverCol = -1;
    private int OffsetY = 100;
    private Shovel shovel = new Shovel();
    Game game = Game.getInstance();
    private PlantType selectedPlant = null;

    private Image sunflowerReady;
    private Image sunflowerFade;
    private Image sunflowerPreview;

    private Image peashooterReady;
    private Image peashooterFaded;
    private Image peashooterPreview;

    private Image PotatoMineReady;
    private Image PotatoMineFaded;
    private Image PotatoMinePreview;

    private Image walNutReady;
    private Image walNutFaded;
    private Image walNutPreview;

    private Image repeaterReady;
    private Image repeaterFaded;
    private Image repeaterPreview;

    private Image cherryBombReady;
    private Image cherryBombFaded;
    private Image cherryBombPreview;

    private Image snowPeaReady;
    private Image snowPeaFaded;
    private Image snowPeaPreview;

    private boolean isDragging = false;
    private boolean isDraggingFromClick = false;
    private int dragX = 0, dragY = 0;

    private Image chomperReady;
    private Image chomperFaded;
    private Image chomperPreview;
    // Thêm biến ở đầu class GamePanel
    private boolean isGameOver = false;
    private boolean isGameWin = false;

    // Thêm phương thức để kiểm tra và set trạng thái
    public void checkGameState() {
        if (game.isGameWin() && !isGameWin) {
            isGameWin = true;
            isGameOver = true;
        }
        if (game.isGameLose() && !isGameOver) {
            isGameOver = true;
            isGameWin = false;
        }
    }

    public void resetGame() {
        game.resetData();  // Reset dữ liệu trong cùng instance
        
        isGameOver = false;
        isGameWin = false;
        
        repaint();
    }
    public GamePanel() {
        setOpaque(false);
        loadPeashooterImages();
        loadSunflowerImages();
        loadPotatoImages();
        loadWalnutImages();
        loadRepeaterImages();
        loadCherryBombImages();
        loadSnowPeaImages();
        loadChomperImages();

        Mouse mouse = new Mouse(this);
        addMouseListener(mouse);
        addMouseMotionListener(mouse);

        new GameLoop(() -> {
            game.update();
            repaint();
        });
        // Trong constructor GamePanel, thêm:
        setFocusable(true);
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    if (isGameOver) {
                        resetGame();
                    }
                }
            }
        });
    }

    public void updateHoverGrid(int mouseX, int mouseY) {
        if (mouseY <= 100) {
            hoverRow = -1;
            hoverCol = -1;
            repaint();
            return;
        }
        
        int col = mouseX / 100;
        int row = (mouseY - 100) / 100;

        if (row >= 0 && row < game.grid.rows && col >= 0 && col < game.grid.cols) {
            hoverRow = row;
            hoverCol = col;
        } else {
            hoverRow = -1;
            hoverCol = -1;
        }
        repaint();
    }

    private void loadPeashooterImages() {
        try {
            java.net.URL readyURL = getClass().getResource("/image/plant/peashooter/peashooterpacket/peashooterpacket1.png");
            java.net.URL fadedURL = getClass().getResource("/image/plant/peashooter/peashooterpacket/peashooterpacket2.png");
            java.net.URL previewURL = getClass().getResource("/image/plant/peashooter/peashooter/peashooter1.png");

            if (readyURL != null) peashooterReady = new ImageIcon(readyURL).getImage();
            if (fadedURL != null) peashooterFaded = new ImageIcon(fadedURL).getImage();
            if (previewURL != null) peashooterPreview = new ImageIcon(previewURL).getImage();
        } catch (Exception e) {
            System.err.println("Error loading Peashooter assets: " + e.getMessage());
        }
    }

    private void loadSunflowerImages(){
        try {
            java.net.URL readyURL = getClass().getResource("/image/plant/sunflower/sunflowerpacket/sunflowerpacket1.png");
            java.net.URL fadeURL = getClass().getResource("/image/plant/sunflower/sunflowerpacket/sunflowerpacket2.png");
            java.net.URL previewURL = getClass().getResource("/image/plant/sunflower/sunflower/sunflower1.png");

            if (readyURL != null) sunflowerReady = new ImageIcon(readyURL).getImage();
            if (fadeURL != null) sunflowerFade = new ImageIcon(fadeURL).getImage();
            if (previewURL != null) sunflowerPreview = new ImageIcon(previewURL).getImage();
        } catch (Exception e){
            System.err.println("Error loading Sunflower assets:  " + e.getMessage());
        }
    }

    private void loadPotatoImages(){
        try {
            java.net.URL readyURL = getClass().getResource("/image/plant/potatomine/potatopacket/potatopacket1.png");
            java.net.URL fadedURL = getClass().getResource("/image/plant/potatomine/potatopacket/potatopacket2.png");
            java.net.URL previewURL = getClass().getResource("/image/plant/potatomine/potatomine/potatomine1.png");

            if (readyURL != null) PotatoMineReady = new ImageIcon(readyURL).getImage();
            if (fadedURL != null) PotatoMineFaded = new ImageIcon(fadedURL).getImage();
            if (previewURL != null) PotatoMinePreview = new ImageIcon(previewURL).getImage();
        } catch (Exception e){
            System.err.println("Error loading PotatoMine assets:  " + e.getMessage());
        }
    }

    private void loadWalnutImages() {
        try {
            java.net.URL readyURL = getClass().getResource("/image/plant/walnut/walnutpacket/walnutpacket1.png");
            java.net.URL fadedURL = getClass().getResource("/image/plant/walnut/walnutpacket/walnutpacket2.png");
            java.net.URL previewURL = getClass().getResource("/image/plant/walnut/walnut/walnut1.png");

            if (readyURL != null) walNutReady = new ImageIcon(readyURL).getImage();
            if (fadedURL != null) walNutFaded = new ImageIcon(fadedURL).getImage();
            if (previewURL != null) walNutPreview = new ImageIcon(walNutPreview != null ? null : previewURL).getImage();
        } catch (Exception e) {
            System.err.println("Error loading WalNut assets: " + e.getMessage());
        }
    }

    private void loadRepeaterImages(){
        try {
            java.net.URL readyURL = getClass().getResource("/image/plant/repeater/repeaterpacket/repeaterpacket1.png");
            java.net.URL fadedURL = getClass().getResource("/image/plant/repeater/repeaterpacket/repeaterpacket2.png");
            java.net.URL previewURL = getClass().getResource("/image/plant/repeater/repeater/repeater1.png");

            if (readyURL != null) repeaterReady = new ImageIcon(readyURL).getImage();
            if (fadedURL != null) repeaterFaded = new ImageIcon(fadedURL).getImage();
            if (previewURL != null) repeaterPreview = new ImageIcon(previewURL).getImage();
        } catch (Exception e) {
            System.err.println("Error loading Repeater assets: " + e.getMessage());
        }
    }

    private void loadCherryBombImages() {
        try {
            java.net.URL readyURL = getClass().getResource("/image/plant/cherrybomb/cherrybombpacket/cherrybombpacket1.png");
            java.net.URL fadedURL = getClass().getResource("/image/plant/cherrybomb/cherrybombpacket/cherrybombpacket2.png");
            java.net.URL previewURL = getClass().getResource("/image/plant/cherrybomb/cherrybomb/cherrybomb1.png");

            if (readyURL != null) cherryBombReady = new ImageIcon(readyURL).getImage();
            if (fadedURL != null) cherryBombFaded = new ImageIcon(fadedURL).getImage();
            if (previewURL != null) cherryBombPreview = new ImageIcon(previewURL).getImage();
        } catch (Exception e) {
            System.err.println("Error loading CherryBomb assets: " + e.getMessage());
        }
    }

    private void loadSnowPeaImages() {
        try {
            java.net.URL readyURL = getClass().getResource("/image/plant/snowpea/snowpeapacket/snowpeapacket1.png");
            java.net.URL fadedURL = getClass().getResource("/image/plant/snowpea/snowpeapacket/snowpeapacket2.png");
            java.net.URL previewURL = getClass().getResource("/image/plant/snowpea/snowpea/snowpea1.png");

            if (readyURL != null) snowPeaReady = new ImageIcon(readyURL).getImage();
            if (fadedURL != null) snowPeaFaded = new ImageIcon(fadedURL).getImage();
            if (previewURL != null) snowPeaPreview = new ImageIcon(previewURL).getImage();
        } catch (Exception e) {
            System.err.println("Error loading SnowPea assets: " + e.getMessage());
        }
    }

    private void loadChomperImages() {
        try {
            Image ready = Chomper.getCardReady();
            Image faded = Chomper.getCardFaded();
            Image preview = Chomper.getDragImageStatic();
            
            if (ready != null) chomperReady = ready;
            if (faded != null) chomperFaded = faded;
            if (preview != null) chomperPreview = preview;
            
            if (chomperReady == null) System.err.println("Warning: Chomper card ready image is null");
            if (chomperFaded == null) System.err.println("Warning: Chomper card faded image is null");
            if (chomperPreview == null) System.err.println("Warning: Chomper drag preview image is null");
        } catch (Exception e) {
            System.err.println("Error loading Chomper assets: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, 900, 100);

        // === CARD 1: SUN COUNTER (x=20) ===
        Image sunCardImage = new ImageIcon(getClass().getResource("/image/others/sun/sunCard.png")).getImage();
        g.drawImage(sunCardImage, 20, 20, 60, 60, null);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString(String.valueOf(game.sun), 30, 73);

        // === CARD 2: SUNFLOWER (x=110) ===
        if (game.sun >= 50 && sunflowerReady != null){
            g.drawImage(sunflowerReady, 110, 20, 60, 60, null);
        } else if (sunflowerFade != null) {
            g.drawImage(sunflowerFade, 110, 20, 60, 60, null);
        }
        g.setColor(Color.BLACK);
        g.drawString("50", 133, 73);

        // === CARD 3: PEASHOOTER (x=200) ===
        if (game.sun >= 100 && peashooterReady != null) {
            g.drawImage(peashooterReady, 200, 20, 60, 60, null);
        } else if (peashooterFaded != null) {
            g.drawImage(peashooterFaded, 200, 20, 60, 60, null);
        }
        g.drawString("100", 219, 73);

        // === CARD 4: POTATO MINE (x=290) ===
        if (game.sun >= 25 && PotatoMineReady != null) {
            g.drawImage(PotatoMineReady, 290, 20, 60, 60, null);
        } else if (PotatoMineFaded != null) {
            g.drawImage(PotatoMineFaded, 290, 20, 60, 60, null);
        }
        g.drawString("25", 313, 73);

        // === CARD 5: SNOW PEA (x=380) ===
        if (game.sun >= 175 && snowPeaReady != null) {
            g.drawImage(snowPeaReady, 380, 20, 60, 60, null);
        } else if (snowPeaFaded != null) {
            g.drawImage(snowPeaFaded, 380, 20, 60, 60, null);
        } else {
            g.setColor(Color.CYAN);
            g.fillRect(380, 20, 60, 60);
        }
        g.setColor(Color.BLACK);
        g.drawString("175", 400, 73);

        // === CARD 6: REPEATER (x=470) ===
        if (game.sun >= 200 && repeaterReady != null) {
            g.drawImage(repeaterReady, 470, 20, 60, 60, null);
        } else if (repeaterFaded != null) {
            g.drawImage(repeaterFaded, 470, 20, 60, 60, null);
        } else {
            g.setColor(Color.GREEN);
            g.fillRect(470, 20, 60, 60);
        }
        g.drawString("200", 489, 73);

        // === CARD 7: CHOMPER (x=560) ===
        if (game.sun >= 150 && chomperReady != null) {
            g.drawImage(chomperReady, 560, 13, 62, 70, null);
        } else if (chomperFaded != null) {
            g.drawImage(chomperFaded, 560, 13, 62, 70, null);
        } else {
            g.setColor(Color.MAGENTA);
            g.fillRect(560, 13, 62, 70);
        }
        g.setColor(Color.BLACK);
        g.drawString("150", 580, 73);

        // === CARD 8: CHERRY BOMB (x=650) ===
        if (game.sun >= 150 && cherryBombReady != null) {
            g.drawImage(cherryBombReady, 650, 10, 60, 70, null);
        } else if (cherryBombFaded != null) {
            g.drawImage(cherryBombFaded, 650, 10, 60, 70, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(650, 10, 60, 70);
        }
        g.setColor(Color.BLACK);
        g.drawString("150", 670, 73);

        // === CARD 9: WALL NUT (x=740) ===
        if (game.sun >= 50 && walNutReady != null){
            g.drawImage(walNutReady, 740, 20, 60, 60, null);
        } else if (walNutFaded != null) {
            g.drawImage(walNutFaded, 740, 20, 60, 60, null);
        } else {
            g.setColor(Color.ORANGE);
            g.fillRect(740, 20, 60, 60);
        }
        g.setColor(Color.BLACK);
        g.drawString("50", 762, 73);

        // === CARD 10: SHOVEL (x=830) ===
        shovel.drawCard(g, 830, 20);

        // === HIGHLIGHT SELECTION ===
        g.setColor(Color.BLACK);
        if (selectedPlant == PlantType.SUNFLOWER)    g.drawRect(110, 20, 60, 60);
        if (selectedPlant == PlantType.PEASHOOTER)   g.drawRect(200, 20, 60, 60);
        if (selectedPlant == PlantType.POTATOMINE)   g.drawRect(290, 20, 60, 60);
        if (selectedPlant == PlantType.SNOWPEA)      g.drawRect(380, 20, 60, 60);
        if (selectedPlant == PlantType.REPEATER)     g.drawRect(470, 20, 60, 60);
        if (selectedPlant == PlantType.CHOMPER)      g.drawRect(560, 20, 60, 60);
        if (selectedPlant == PlantType.CHERRYBOMB)   g.drawRect(650, 20, 60, 60);
        if (selectedPlant == PlantType.WALL_NUT)     g.drawRect(740, 20, 60, 60);

        game.grid.draw(g);

        // Draw Live Placed Plants
        for (int r = 0; r < game.grid.rows; r++) {
            for (int c = 0; c < game.grid.cols; c++) {
                var plant = game.grid.cells[r][c].plant;
                if (plant instanceof PeaShooter)  ((PeaShooter) plant).draw(g);
                else if (plant instanceof SunFlower) ((SunFlower) plant).draw(g);
                else if (plant instanceof PotatoMine) ((PotatoMine) plant).draw(g);
                else if (plant instanceof WallNut)   ((WallNut) plant).draw(g);
                else if (plant instanceof Repeater)  ((Repeater) plant).draw(g);
                else if (plant instanceof CherryBomb) ((CherryBomb) plant).draw(g);
                else if (plant instanceof SnowPea) ((SnowPea) plant).draw(g);
                else if (plant instanceof Chomper) ((Chomper) plant).draw(g);
            }
        }


        // 2. Draw Zombies Midground Layer
        for (var z : game.Zombies) {
            if (z instanceof BrownSuit) {
                ((BrownSuit) z).draw(g);//
                continue;
            } else if (z instanceof ConeHead) {
                ((ConeHead) z).draw(g);
                continue;
            } else if (z instanceof BucketHead) {
                ((BucketHead) z).draw(g);
                continue;
            } else if (z instanceof NewsPaper) {
                ((NewsPaper) z).draw(g);
                continue;
            } else if (z instanceof PoleVaulting) {
                ((PoleVaulting) z).draw(g);
                continue;
            }
        }

        for (var b : Game.getInstance().bullets) {
            b.draw(g, 100);
        }

        // --- ALPHA SEED SELECTION HOVER PREVIEWS ---
        if (hoverRow != -1 && hoverCol != -1) {
            if (hoverRow >= 0 && hoverRow < game.grid.rows && hoverCol >= 0 && hoverCol < game.grid.cols) {
                Graphics2D g2 = (Graphics2D) g;
                java.awt.Composite originalComposite = g2.getComposite();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

                if (selectedPlant != null && game.grid.cells[hoverRow][hoverCol].plant == null) {
                    int hoverX = hoverCol * 100 + 15;
                    int hoverY = hoverRow * 100 + 100 + 15;

                    if (selectedPlant == PlantType.PEASHOOTER && peashooterPreview != null) {
                        g2.drawImage(peashooterPreview, hoverX, hoverY, 70, 70, null);
                    } else if (selectedPlant == PlantType.SUNFLOWER && sunflowerPreview != null) {
                        g2.drawImage(sunflowerPreview, hoverX, hoverY, 70, 70, null);
                    } else if (selectedPlant == PlantType.POTATOMINE && PotatoMinePreview != null) {
                        g2.drawImage(PotatoMinePreview, hoverX, hoverY, 70, 70, null);
                    } else if (selectedPlant == PlantType.WALL_NUT) {
                        if (walNutPreview != null) {
                            g2.drawImage(walNutPreview, hoverX, hoverY, 70, 70, null);
                        } else {
                            g2.setColor(Color.ORANGE);
                            g2.fillRect(hoverX, hoverY, 70, 70);
                        }
                    } else if (selectedPlant == PlantType.REPEATER) {
                        if (repeaterPreview != null) {
                            g2.drawImage(repeaterPreview, hoverX, hoverY, 70, 70, null);
                        } else {
                            g2.setColor(Color.GREEN);
                            g2.fillRect(hoverX, hoverY, 70, 70);
                        }
                    } else if (selectedPlant == PlantType.CHERRYBOMB && cherryBombPreview != null) {
                        g2.drawImage(cherryBombPreview, hoverX, hoverY, 70, 70, null);
                    } else if (selectedPlant == PlantType.SNOWPEA && snowPeaPreview != null) {
                        g2.drawImage(snowPeaPreview, hoverX, hoverY, 70, 70, null);
                    } else if (selectedPlant == PlantType.CHOMPER && chomperPreview != null) {
                        g2.drawImage(chomperPreview, hoverX, hoverY, 70, 70, null);
                    }
                }

                if (shovel.isActive() && game.grid.cells[hoverRow][hoverCol].plant != null) {
                    g2.setColor(Color.RED);
                    g2.fillRect(hoverCol * 100, hoverRow * 100 + 100, 100, 100);
                }
                
                g2.setComposite(originalComposite);
            }
        }

        if (isDragging && selectedPlant != null) {
            Image dragImage = getDragImage(selectedPlant);
            if (dragImage != null) {
                g.drawImage(dragImage, dragX - 37, dragY - 37, 75, 75, null);
            }
        }

        if (shovel.isDragging()) {
            shovel.drawDrag(g);
        }

        for (var s : game.suns) {
            s.draw(g);
        }
        // Kiểm tra trạng thái game
        checkGameState();

        if (isGameOver) {
            // Vẽ lớp phủ mờ
            Graphics2D g2d = (Graphics2D) g;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
            
            if (isGameWin) {
                g2d.setColor(new Color(0, 150, 0));  // Xanh lá
            } else {
                g2d.setColor(new Color(150, 0, 0));  // Đỏ
            }
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            // Reset composite
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            
            // Vẽ chữ thông báo
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 60));
            
            String message = isGameWin ? "VICTORY!" : "GAME OVER!";
            FontMetrics fm = g2d.getFontMetrics();
            int msgWidth = fm.stringWidth(message);
            g2d.drawString(message, (getWidth() - msgWidth) / 2 - 250, getHeight() / 2);
            
            // Vẽ hướng dẫn
            g2d.setFont(new Font("Arial", Font.PLAIN, 24));
            String instruction = "Press ENTER to play again";
            int insWidth = fm.stringWidth(instruction);
            g2d.drawString(instruction, (getWidth() - insWidth) / 2, getHeight() / 2 + 50);
        }
    }

public void handleClick(int x, int y) {
    // ==================== 1. XỬ LÝ CLICK VÀO SUN ====================
    for (int i = 0; i < game.suns.size(); i++) {
        var s = game.suns.get(i);
        if (x >= s.x && x <= s.x + 40 && y >= s.y && y <= s.y + 40) {
            game.sun += s.value;
            game.suns.remove(i);
            repaint();
            return;
        }
    }

    // ==================== 2. XỬ LÝ CLICK TRÊN THANH MENU ====================
    if (y <= 100) {
        // 2a. Hủy plant khi click vào vùng trống của menu
        if (selectedPlant != null) {
            boolean isOnCard = (x >= 110 && x <= 170) ||
                               (x >= 200 && x <= 260) ||
                               (x >= 290 && x <= 350) ||
                               (x >= 380 && x <= 440) ||
                               (x >= 470 && x <= 530) ||
                               (x >= 560 && x <= 620) ||
                               (x >= 650 && x <= 710) ||
                               (x >= 740 && x <= 800);
            boolean isOnShovel = (x >= 830 && x <= 890);
            
            if (!isOnCard && !isOnShovel) {
                cancelPlantSelection();
                repaint();
                return;
            }
        }
        
        // 2b. Chọn plant (hoặc hủy nếu click vào packet đang chọn)
        PlantType newPlant = null;
        if (x >= 110 && x <= 170) newPlant = PlantType.SUNFLOWER;
        else if (x >= 200 && x <= 260) newPlant = PlantType.PEASHOOTER;
        else if (x >= 290 && x <= 350) newPlant = PlantType.POTATOMINE;
        else if (x >= 380 && x <= 440) newPlant = PlantType.SNOWPEA;
        else if (x >= 470 && x <= 530) newPlant = PlantType.REPEATER;
        else if (x >= 560 && x <= 620) newPlant = PlantType.CHOMPER;
        else if (x >= 650 && x <= 710) newPlant = PlantType.CHERRYBOMB;
        else if (x >= 740 && x <= 800) newPlant = PlantType.WALL_NUT;

        if (newPlant != null) {
            // Nếu click vào packet và đã có plant được chọn (bất kể loại nào) -> HỦY
            if (selectedPlant != null) {
                cancelPlantSelection();
            } else {
                // Chỉ chọn mới nếu chưa có plant nào được chọn
                selectPlant(newPlant);
            }
        }
        else if (x >= 830 && x <= 890) {
            // 2c. Xử lý shovel
            if (shovel.isActive()) {
                shovel.deactivate();
            } else {
                shovel.activate();
                cancelPlantSelection();
                startShovelDrag(x, y);
            }
        }
        
        repaint();
        return;
    }

    // ==================== 3. XỬ LÝ CLICK RA NGOÀI MAP ====================
    int col = x / 100;
    int row = (y - 100) / 100;
    
    if (row < 0 || row >= game.grid.rows || col < 0 || col >= game.grid.cols) {
        cancelPlantSelection();
        shovel.deactivate();
        repaint();
        return;
    }

    // ==================== 4. XỬ LÝ SHOVEL MODE ====================
    if (shovel.isActive()) {
        if (row >= 0 && row < game.grid.rows && col >= 0 && col < game.grid.cols) {
            game.grid.cells[row][col].plant = null;
        }
        shovel.deactivate();
        repaint();
        return;
    }

    // ==================== 5. XỬ LÝ ĐẶT PLANT ====================
    if (game.grid.cells[row][col].plant == null && selectedPlant != null) {
        placePlantAt(row, col, selectedPlant);
        cancelPlantSelection();
        repaint();
    }
}

// ==================== PHƯƠNG THỨC HỖ TRỢ ====================

private void selectPlant(PlantType type) {
    selectedPlant = type;
    startDrag(dragX, dragY, type);
    isDraggingFromClick = true;
    updateDrag(dragX, dragY);
}

private void cancelPlantSelection() {
    selectedPlant = null;
    isDraggingFromClick = false;
    endDrag();
}

    public boolean isDraggingFromClick() {
        return isDraggingFromClick;
    }

    public void setDraggingFromClick(boolean value) {
        this.isDraggingFromClick = value;
    }

    public void handleDragDrop(int dropX, int dropY, int startX, int startY) {
        // Xử lý shovel kéo thả
        if (startY <= 100 && startX >= 830 && startX <= 890) {
            if (dropY > 100) {
                int col = dropX / 100;
                int row = (dropY - 100) / 100;
                if (row >= 0 && row < game.grid.rows && col >= 0 && col < game.grid.cols) {
                    game.grid.cells[row][col].plant = null;
                }
            }
            shovel.deactivate();
            endDrag();
            return;
        }
        
        // Xử lý plant kéo thả
        if (startY <= 100) {
            PlantType draggedPlant = getPlantAtCardPosition(startX);
            if (draggedPlant == null || shovel.isActive()) {
                endDrag();
                return;
            }
            
            if (dropY > 100) {
                int col = dropX / 100;
                int row = (dropY - 100) / 100;
                if (row >= 0 && row < game.grid.rows && col >= 0 && col < game.grid.cols) {
                    if (game.grid.cells[row][col].plant == null) {
                        placePlantAt(row, col, draggedPlant);
                    }
                }
            }
            endDrag();
        }
    }

    private Image getDragImage(PlantType type) {
        String path = "";
        switch (type) {
            case PEASHOOTER:
                path = "/image/plant/peashooter/peashooter/peashooter1.png";
                break;
            case SUNFLOWER:
                path = "/image/plant/sunflower/sunflower/sunflower1.png";
                break;
            case POTATOMINE:
                path = "/image/plant/potatomine/potatomine/potatomine1.png";
                break;
            case WALL_NUT:
                path = "/image/plant/walnut/walnut/walnut1.png";
                break;
            case REPEATER:
                path = "/image/plant/repeater/repeater/repeater1.png";
                break;
            case SNOWPEA:
                path = "/image/plant/snowpea/snowpea/snowpea1.png";
                break;
            case CHERRYBOMB:
                path = "/image/plant/cherrybomb/cherrybomb/cherrybomb1.png";
                break;
            case CHOMPER:
                path = "/image/plant/chomper/chomper/chomper1.png";
                break;
            default:
                return null;
        }
        try {
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL != null) return new ImageIcon(imgURL).getImage();
        } catch (Exception e) {}
        return null;
    }

    public void startDrag(int x, int y, PlantType type) {
        isDragging = true;
        selectedPlant = type;
        dragX = x;
        dragY = y;
        repaint();
    }

    public void updateDrag(int x, int y) {
        dragX = x;
        dragY = y;
        if (isDragging || isDraggingFromClick) {
            repaint();
        }
    }

    public void endDrag() {
        isDragging = false;
        selectedPlant = null;
        isDraggingFromClick = false;
        repaint();
    }

    public PlantType getSelectedPlant() {
        return selectedPlant;
    }

    public boolean isShovelActive() {
        return shovel.isActive();
    }

    public void startShovelDrag(int x, int y) {
        shovel.startDrag(x, y);
    }

    public void updateShovelDrag(int x, int y) {
        shovel.updateDrag(x, y);
        repaint();
    }

    public PlantType getPlantAtCardPosition(int x) {
        if (x >= 110 && x <= 170) return PlantType.SUNFLOWER;
        if (x >= 200 && x <= 260) return PlantType.PEASHOOTER;
        if (x >= 290 && x <= 350) return PlantType.POTATOMINE;
        if (x >= 380 && x <= 440) return PlantType.SNOWPEA;
        if (x >= 470 && x <= 530) return PlantType.REPEATER;
        if (x >= 560 && x <= 620) return PlantType.CHOMPER;
        if (x >= 650 && x <= 710) return PlantType.CHERRYBOMB;
        if (x >= 740 && x <= 800) return PlantType.WALL_NUT;
        return null;
    }

    private void placePlantAt(int row, int col, PlantType type) {
        switch (type) {
            case WALL_NUT:
                if (game.sun >= 50) { game.grid.cells[row][col].plant = new WallNut(row, col); game.sun -= 50; }
                break;
            case CHERRYBOMB:
                if (game.sun >= 150) { game.grid.cells[row][col].plant = new CherryBomb(row, col); game.sun -= 150; }
                break;
            case SUNFLOWER:
                if (game.sun >= 50) { game.grid.cells[row][col].plant = new SunFlower(row, col); game.sun -= 50; }
                break;
            case PEASHOOTER:
                if (game.sun >= 100) { game.grid.cells[row][col].plant = new PeaShooter(row, col); game.sun -= 100; }
                break;
            case POTATOMINE:
                if (game.sun >= 25) { game.grid.cells[row][col].plant = new PotatoMine(row, col); game.sun -= 25; }
                break;
            case SNOWPEA:
                if (game.sun >= 175) { game.grid.cells[row][col].plant = new SnowPea(row, col); game.sun -= 175; }
                break;
            case REPEATER:
                if (game.sun >= 200) { game.grid.cells[row][col].plant = new Repeater(row, col); game.sun -= 200; }
                break;
            case CHOMPER:
                if (game.sun >= 150) { game.grid.cells[row][col].plant = new Chomper(row, col); game.sun -= 150; }
                break;
        }
        selectedPlant = null;
    }
}