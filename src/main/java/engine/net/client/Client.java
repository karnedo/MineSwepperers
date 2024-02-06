package engine.net.client;

import engine.net.dataPackage.Coordinate;
import engine.board.Board;
import engine.graphics.GamePanel;
import engine.net.dataPackage.Loser;
import engine.net.dataPackage.Winners;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class Client {

    private static String SERVER_IP;
    private static final int PORT = 6096;
    private static String NAME;

    private GamePanel gamePanel;
    private JFrame window;
    private JFrame matchmakingWindow;

    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    public Client(){
        showConnectionDialog();
    }

    private void showConnectionDialog() {
        JPanel panel = new JPanel();
        JTextField ipField = new JTextField(10);
        JTextField nameField = new JTextField(10);

        panel.add(new JLabel("Server IP:"));
        panel.add(ipField);
        panel.add(new JLabel("Your Name:"));
        panel.add(nameField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Connect to Server",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            SERVER_IP = ipField.getText();
            NAME = nameField.getText();
        } else {
            System.exit(0);
        }
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

        objectInputStream = new ObjectInputStream(socket.getInputStream());
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

        //Send this client's name to the server
        objectOutputStream.writeObject(NAME);
        objectOutputStream.flush();

        //Show a window that says "Waiting players..."
        initWaitingWindow();

        //Get the game's board
        Board board = (Board) objectInputStream.readObject();
        this.gamePanel = new GamePanel(board);

        //Show game board's window
        initWindow();
        matchmakingWindow.dispose();

        boolean hasEnded = false;

        while (!hasEnded) {
            //Receive package
            Object data = objectInputStream.readObject();

            if(data instanceof Coordinate){
                processTurn((Coordinate) data);
            }else if(data instanceof Loser){
                Loser loser = (Loser) data;
                System.out.println("Loser name: " + loser.getName());
                System.out.println("My name: " + InetAddress.getLocalHost().getHostName());
                if(loser.getName().equals(NAME)){
                    loseGame();
                    hasEnded = true;
                }
            }else if(data instanceof Winners){
                Winners winners = (Winners) data;
                if(winners.hasWinner(NAME)){
                    winGame(winners.getNames());
                    hasEnded = true;
                }
            }

        }

        objectInputStream.close();
        objectOutputStream.close();

        System.exit(0);

    }

    private void winGame(String names[]) {
        String message = "";
        if(names.length > 1) {
            for (String s : names) {
                if (!s.equals(NAME)) message += s + ", ";
            }
            message += "and you won!";
        }else{
            message += "You won!";
        }
        JOptionPane.showMessageDialog(null, message);
    }

    private void loseGame() {
        JOptionPane.showMessageDialog(null, "You lost.");
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
