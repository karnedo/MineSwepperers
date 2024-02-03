package engine.net.client;

import engine.net.dataPackage.Coordinate;
import engine.board.Board;
import engine.graphics.GamePanel;
import engine.net.dataPackage.Loser;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class Client {

    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 6096;

    private GamePanel gamePanel;
    private JFrame window;
    private JFrame matchmakingWindow;

    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    public Client(){

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

    private void initWindow(){
        window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Game");

        window.add(gamePanel);

        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    private void start() throws IOException, ClassNotFoundException {
        Socket socket = new Socket(SERVER_IP, PORT);

        //Show a window that says "Waiting players..."
        initWaitingWindow();

        objectInputStream = new ObjectInputStream(socket.getInputStream());
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

        //Get the game's board
        Board board = (Board) objectInputStream.readObject();
        this.gamePanel = new GamePanel(board);

        //Show game board's window
        initWindow();
        matchmakingWindow.dispose();

        boolean hasLost = false;

        while (!hasLost) {
            //Receive package
            Object data = objectInputStream.readObject();

            if(data instanceof Coordinate){
                processTurn((Coordinate) data);
            }else if(data instanceof Loser){
                Loser loser = (Loser) data;
                if(loser.getName().equals(InetAddress.getLocalHost().getHostName())){
                    loseGame();
                }
            }


        }

        objectInputStream.close();
        objectOutputStream.close();

    }

    private void loseGame() {
        JOptionPane.showMessageDialog(null, "You lost.");
        System.exit(0);
    }

    private void processTurn(Coordinate coord) throws IOException, ClassNotFoundException {
        System.out.println("Received " + coord.toString());

        if (coord.getX() == -1 && coord.getY() == -1) {
            System.out.println("It's your turn!");
            //Get clicked coords
            Coordinate clickedCoords = null;
            while(clickedCoords == null){
                clickedCoords = gamePanel.getClickListener().getClickedCoords();
            }
            gamePanel.getClickListener().resetCoords();

            // Send coords to server
            objectOutputStream.writeObject(clickedCoords);
            objectOutputStream.flush();
        } else {
            //Receive coords
            gamePanel.updateBoard(coord);
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
