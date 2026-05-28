package Core;

import Entities.Others.LawnMower;
import javax.swing.*;
import java.awt.*;

public class LawnPanel extends JPanel {

    private Image background;
    private Game game = Game.getInstance();
    private JLabel timerLabel;
    private Timer updateTimer;

    public LawnPanel() {
        background = new ImageIcon(getClass().getResource("/image/others/lawn/lawn.png")).getImage();
        setLayout(null);
        setPreferredSize(new Dimension(1430, 618));

        // ===== CREATE CLOCK =====
        timerLabel = new JLabel();
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setBackground(new Color(0, 0, 0, 150));
        timerLabel.setOpaque(true);
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setBounds(20, 15, 130, 35);
        add(timerLabel);
        
        // Timer to update lawnmower and clock
        updateTimer = new Timer(50, e -> {
            // Update lawnmower
            for (LawnMower mower : game.mowers) {
                mower.update();
            }
            updateTimerDisplay();
            repaint();
        });
        updateTimer.start();
    }
    
    // Add reset method
    public void resetGame() {
        game = Game.getInstance();  // Get new instance (already reset)
        updateTimerDisplay();
        repaint();
    }
    
    private void updateTimerDisplay() {
        if (timerLabel != null) {
            int remainingSeconds = game.getRemainingSeconds();
            int minutes = remainingSeconds / 60;
            int seconds = remainingSeconds % 60;
            timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
            
            // CHANGE COLOR: Green when time is almost up
            if (remainingSeconds <= 10) {
                timerLabel.setForeground(Color.GREEN);   // Red -> Green
                timerLabel.setFont(new Font("Arial", Font.BOLD, 24));
            } else if (remainingSeconds <= 30) {
                timerLabel.setForeground(Color.YELLOW);  // Orange -> Yellow
                timerLabel.setFont(new Font("Arial", Font.BOLD, 22));
            } else {
                timerLabel.setForeground(Color.WHITE);
                timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, 1430, 618, null);
        
        for (LawnMower mower : game.mowers) {
            mower.drawOnLawnPanel(g);
        }
        
        // Display overlay when game ends
        if (game.isGameWin()) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(0, 150, 0, 200));
            g2d.fillRect(0, 0, 1430, 618);
        } else if (game.isGameLose()) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(150, 0, 0, 200));
            g2d.fillRect(0, 0, 1430, 618);
        }
    }
}