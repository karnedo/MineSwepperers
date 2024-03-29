package engine.graphics;

import engine.net.data.Coordinate;
import engine.board.Board;
import engine.handler.ClickListener;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel{

    private final int ogTileSize = 16;
    private final int scale = 3;

    private final int tileSize = ogTileSize * scale;

    private final int maxScreenCol;
    private final int maxScreenRow;
    private final int screenWidth;
    private final int screenHeight;

    private ClickListener clickListener;

    Board board;

    public GamePanel(Board board){
        maxScreenCol = board.getWidth();
        maxScreenRow = board.getHeight();

        screenWidth = maxScreenCol * tileSize;
        screenHeight = maxScreenRow * tileSize;

        this.board = board;

        initWindowSettings();
    }

    private void initWindowSettings(){
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);

        clickListener = new ClickListener(this);
        this.addMouseListener(clickListener);
    }

    public boolean clickTile(int x, int y) {
        boolean bombFound = !this.board.reveal(x, y);
        repaint();
        return bombFound;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the board
        for (int i = 0; i < maxScreenCol; i++) {
            for (int j = 0; j < maxScreenRow; j++) {
                char box = board.getBoxesHidden()[i][j];
                boolean visible = board.getBoxesVisible()[i][j];

                // Adjust the position based on the scale and tileSize
                int x = i * tileSize;
                int y = j * tileSize;

                if (visible) {
                    // Draw revealed cell
                    g.setColor(Color.WHITE);
                    g.fillRect(x, y, tileSize, tileSize);

                    if (box == '*') {
                        // Draw bomb symbol
                        g.setColor(Color.RED);
                        g.drawString("*", x + tileSize / 2 - 5, y + tileSize / 2 + 5);
                    } else {
                        // Draw number of surrounding bombs
                        g.setColor(Color.BLUE);
                        int surroundingBombs = board.getBoxesHidden()[i][j] - '0';
                        if (surroundingBombs > 0) {
                            g.drawString(Integer.toString(surroundingBombs), x + tileSize / 2 - 5, y + tileSize / 2 + 5);
                        }
                    }
                } else {
                    // Draw hidden cell
                    g.setColor(Color.GRAY);
                    g.fillRect(x, y, tileSize, tileSize);
                }
                // Draw border
                g.setColor(Color.DARK_GRAY);
                g.drawRect(x, y, tileSize, tileSize);
            }
        }
    }

    public int getTileSize() {
        return tileSize;
    }

    protected ClickListener getClickListener() {
        return this.clickListener;
    }

    protected boolean isTileRevealed(Coordinate coord){
        return board.isRevealed(coord.getX(), coord.getY());
    }

    /**
     * @return if the chosen coordinate is a bomb
     **/
    protected boolean updateBoard(Coordinate coord) {
        return clickTile(coord.getX(), coord.getY());
    }
}
