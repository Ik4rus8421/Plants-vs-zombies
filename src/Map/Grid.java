package Map;
import java.awt.*;


public class Grid {
    public int rows, cols;
    public Cell[][] cells;

    public Grid(int r, int c) {
        rows = r;
        cols = c;

        cells = new Cell[r][c];

        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
    }

    public void update() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j].update();
            }
        }
    }

    public void draw(Graphics g) {
        int OffsetY = 100;

        // This loop just draws the empty square outlines of the map
        //for (int i = 0; i < rows; i++) {
            //for (int j = 0; j < cols; j++) {
                //g.drawRect(j * 100, i * 100 + OffsetY, 100, 100);
            //}
        //}

        // 🚨 STOP HERE!
        // Completely delete the "for" loop that checks for "plant != null" and "instanceof SunFlower"
    }
}