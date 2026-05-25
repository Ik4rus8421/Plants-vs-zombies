import Core.GamePanel;
import Core.LawnPanel;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        JFrame frame = new JFrame("PVZ");

        LawnPanel lawnPanel = new LawnPanel();

        GamePanel gamePanel = new GamePanel();

        // dịch gamepanel để grid khớp ô cỏ
        gamePanel.setBounds(528, -10, 900, 600);

        lawnPanel.add(gamePanel);

        frame.setContentPane(lawnPanel);

        frame.pack();

        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
    }
}