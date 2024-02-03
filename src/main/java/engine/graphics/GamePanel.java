package engine.graphics;

import engine.Coordinate;
import engine.board.Board;
import engine.handler.ClickListener;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel{

    private final int ogTileSize = 16;
    private final int scale = 3;

    private final int tileSize = ogTileSize * scale;

    private final int maxScreenCol = 10;
    private final int maxScreenRow = 10;
    private final int screenWidth = maxScreenCol * tileSize;
    private final int screenHeight = maxScreenRow * tileSize;

    private ClickListener clickListener;

    Board board;

    public GamePanel(){
        this.board = new Board(maxScreenCol, maxScreenRow);

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);

        clickListener = new ClickListener(this);
        this.addMouseListener(clickListener);
    }

    public void clickTile(int x, int y) {
        boolean bombFound = !this.board.reveal(x, y);
        repaint();
        if(bombFound){
            JOptionPane.showMessageDialog(null, "You lost!");
            System.exit(0);
        }
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

    public ClickListener getClickListener() {
        return this.clickListener;
    }

    public void updateBoard(Coordinate coord) {
        clickTile(coord.getX(), coord.getY());
    }
}
