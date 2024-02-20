package engine.net.client;

import engine.graphics.GameWindow;
import engine.net.data.Coordinate;
import engine.board.Board;
import engine.graphics.GamePanel;
import engine.net.data.Loser;
import engine.net.data.Winners;

import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    private static String SERVER_IP;
    private static final int PORT = 19788;
    private static String NAME;

    private GamePanel gamePanel;

    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private GameWindow game;

    public Client(){ }

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

    private Socket connect(){
        boolean validData;
        boolean couldConnect = false;
        Socket socket = null;
        do{
            showConnectionDialog();
            validData = !SERVER_IP.isBlank() && !NAME.isBlank();
            if(!validData) JOptionPane.showMessageDialog(null, "Invalid data.");
            if(validData){
                try {
                    couldConnect = true;
                    socket = new Socket(SERVER_IP, PORT);
                } catch (IOException e) {
                    couldConnect = false;
                    JOptionPane.showMessageDialog(null, "Could not connect to server: "
                            + e);
                }
            }
        }while(!validData || !couldConnect);

        return socket;
    }

    private void start() throws IOException, ClassNotFoundException {

        Socket socket = connect();

        if(socket == null){ System.exit(0); }

        objectInputStream = new ObjectInputStream(socket.getInputStream());
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

        //Send this client's name to the server
        objectOutputStream.writeObject(NAME);
        objectOutputStream.flush();

        this.game = new GameWindow();

        //Get the game's board
        Board board = (Board) objectInputStream.readObject();

        //Show game board's window
        game.startGame(board);

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
        socket.close();

        System.exit(0);

    }

    /* Set the current game's state */
    private void setState(String message){
        game.setTitle(message);
    }

    private void winGame(String[] names) {
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

    private void processTurn(Coordinate coord) throws IOException {
        System.out.println("Received " + coord.toString());

        if (coord.getX() == -1 && coord.getY() == -1) {
            setState("It's your turn");
            //Get clicked coords
            Coordinate clickedCoords = null;
            boolean tileIsRevealed = false;
            while(clickedCoords == null || tileIsRevealed){
                clickedCoords = game.getClickedCoords();
                if(clickedCoords != null) tileIsRevealed = game.isTileRevealed(clickedCoords);
            }
            game.resetClickedCoordinates();

            setState("Wait for your turn");

            // Send coords to server
            objectOutputStream.writeObject(clickedCoords);
            objectOutputStream.flush();
        } else {
            //Receive coords
            game.updateBoard(coord);
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.start();
        } catch(EOFException e){
            JOptionPane.showMessageDialog(null, "The server has closed the connection!");
            System.exit(0);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
