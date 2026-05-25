package Core;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Mouse extends MouseAdapter {
    public int x, y;
    public boolean pressed;
    private boolean isDragging = false;
    private int dragStartX, dragStartY;
    private final GamePanel gamePanel;

    public Mouse(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.pressed = true;
        this.isDragging = false;
        this.dragStartX = e.getX();
        this.dragStartY = e.getY();
        
        // 1. Cập nhật dragX, dragY TRƯỚC
        gamePanel.updateDrag(e.getX(), e.getY());
        
        // 2. Gọi handleClick (sẽ gọi selectPlant -> set isDragging = true)
        gamePanel.handleClick(e.getX(), e.getY());
        
        // 3. Nếu cần, bắt đầu shovel drag
        if (e.getY() <= 100 && e.getX() >= 830 && e.getX() <= 890) {
            gamePanel.startShovelDrag(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.pressed = false;

        if (isDragging) {
            // Kéo thả thông thường
            gamePanel.handleDragDrop(e.getX(), e.getY(), dragStartX, dragStartY);
            gamePanel.endDrag();
            gamePanel.setDraggingFromClick(false);
        }
        // KHÔNG xử lý isDraggingFromClick ở đây nữa
        // isDraggingFromClick sẽ được tắt trong handleClick khi đặt plant hoặc hủy

        this.isDragging = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        this.x = e.getX();
        this.y = e.getY();
        
        if (!isDragging) {
            isDragging = true;
        }
        
        if (gamePanel.isShovelActive()) {
            gamePanel.updateShovelDrag(e.getX(), e.getY());
        } else {
            gamePanel.updateDrag(e.getX(), e.getY());
        }
        gamePanel.updateHoverGrid(this.x, this.y);
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        this.x = e.getX();
        this.y = e.getY();
        
        if (gamePanel.isDraggingFromClick()) {
            gamePanel.updateDrag(e.getX(), e.getY());
        }
        if (gamePanel.isShovelActive()) {
            gamePanel.updateShovelDrag(e.getX(), e.getY());
        }
        
        gamePanel.updateHoverGrid(this.x, this.y);
    }
}