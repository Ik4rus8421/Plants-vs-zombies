package Map;

import Entities.Plant.Plants;

public class Cell {
    public int row, col;
    public Plants plant;

    public Cell(int r, int c) {
        row = r;
        col = c;
    }

    public void update() {
        if (plant != null) {
            plant.update();
        }
    }
}
