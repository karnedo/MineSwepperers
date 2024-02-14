package engine.graphics;

import engine.board.Board;
import engine.net.data.Coordinate;

import javax.swing.*;

public class GameWindow {

    private GamePanel gamePanel;
    private JFrame window;
    private JFrame matchmakingWindow;

    public GameWindow(){
        initWaitingWindow();
    }

    public Coordinate getClickedCoords(){
        if(gamePanel == null) return null;
        return this.gamePanel.getClickListener().getClickedCoords();
    }

    public void updateBoard(Coordinate coord){
        gamePanel.updateBoard(coord);
    }

    public void resetClickedCoordinates(){
        gamePanel.getClickListener().resetCoords();
    }

    public boolean isTileRevealed(Coordinate coord){
        return gamePanel.isTileRevealed(coord);
    }

    public void setTitle(String message){
        window.setTitle(message);
    }

    private void initWaitingWindow() {
        matchmakingWindow = new JFrame("Waiting for Players...");
        matchmakingWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        matchmakingWindow.setSize(300, 100);

        JLabel waitingLabel = new JLabel("Waiting for other players to join...");
        waitingLabel.setHorizontalAlignment(JLabel.CENTER);

        matchmakingWindow.add(waitingLabel);
        matchmakingWindow.setLocationRelativeTo(null);
        matchmakingWindow.setVisible(true);
    }

    public void startGame(Board board){
        this.gamePanel = new GamePanel(board);

        matchmakingWindow.dispose();
        window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Game");

        window.add(gamePanel);

        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

}
