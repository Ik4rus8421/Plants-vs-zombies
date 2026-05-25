package Core;

import javax.swing.Timer;

public class GameLoop {
    public GameLoop(Runnable update) {
        new Timer(100, e -> update.run()).start();
    }
}
