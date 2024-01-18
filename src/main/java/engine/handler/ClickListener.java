package engine.handler;

import engine.graphics.GamePanel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ClickListener extends MouseAdapter {

    private GamePanel gamePanel;

    public ClickListener(GamePanel gamePanel){
        this.gamePanel = gamePanel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        int tileX = mouseX / gamePanel.getTileSize();
        int tileY = mouseY / gamePanel.getTileSize();

        gamePanel.clickTile(tileX, tileY);
    }
}
