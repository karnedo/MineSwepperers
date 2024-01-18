package engine.graphics;

import engine.board.Board;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable{

    private final int ogTileSize = 16;
    private final int scale = 3;

    private final int tileSize = ogTileSize * scale;

    private final int maxScreenCol = 10;
    private final int maxScreenRow = 10;
    private final int screenWidth = maxScreenCol * tileSize;
    private final int screenHeight = maxScreenRow * tileSize;

    Thread gameThread;
    Board board;

    public GamePanel(){
        this.board = new Board(maxScreenCol, maxScreenRow);

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        //Establish connection with the server

        //Draw game
    }
}
