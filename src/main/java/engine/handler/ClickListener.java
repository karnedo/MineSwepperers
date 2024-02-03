package engine.handler;

import engine.Coordinate;
import engine.graphics.GamePanel;
import engine.net.client.Client;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ClickListener extends MouseAdapter {

    private GamePanel gamePanel;
    private volatile Coordinate clickedCoords;

    public ClickListener(GamePanel gamePanel){
        this.gamePanel = gamePanel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        int tileX = mouseX / gamePanel.getTileSize();
        int tileY = mouseY / gamePanel.getTileSize();

        clickedCoords = new Coordinate(tileX, tileY);
    }

    public Coordinate getClickedCoords(){
        return clickedCoords;
    }

    public void resetCoords() {
        clickedCoords = null;
    }
}
