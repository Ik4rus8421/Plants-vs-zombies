package Entities.Plant;

public abstract class Plants {
    public int row;
    public int col;

    // 🛠️ Shared health variable across ALL plants
    public int hp;

    public Plants(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public abstract void update();
}