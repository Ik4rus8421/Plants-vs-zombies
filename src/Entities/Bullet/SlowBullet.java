package Entities.Bullet;

import javax.swing.ImageIcon;

public class SlowBullet extends Bullet {

    public SlowBullet(int row, double x) {
        // Calls the Bullet.java constructor, passing 'true' for the slow flag
        super(row, x, true);
    }

    /**
     * Overrides the standard green pea loader to pull your Ice Pea images instead!
     */
    @Override
    protected void loadAnimationFrames() {
        for (int i = 1; i <= 3; i++) {
            String path = "/image/plant/bullet/slowpea/slowpea" + i + ".png";
            try {
                java.net.URL imgURL = getClass().getResource(path);
                if (imgURL != null) {
                    animationFrames.add(new ImageIcon(imgURL).getImage());
                } else {
                    System.err.println("Could not find ice bullet asset: " + path);
                }
            } catch (Exception e) {
                System.err.println("Error loading ice bullet frame: " + e.getMessage());
            }
        }
    }
}