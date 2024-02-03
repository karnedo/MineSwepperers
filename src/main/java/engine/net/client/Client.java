package engine.net.client;

import engine.Coordinate;
import engine.board.Board;
import engine.graphics.GamePanel;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 6096;

    private GamePanel gamePanel;

    public Client(){
        init();
    }

    private void init(){
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Game");

        this.gamePanel = new GamePanel();
        window.add(gamePanel);

        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    private void start() throws IOException, ClassNotFoundException {
        Socket socket = new Socket(SERVER_IP, PORT);
        System.out.println("Connected with server");

        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

        boolean hasLost = false;

        while (!hasLost) {
            //Receives turn's coordinates
            Coordinate coord = (Coordinate) objectInputStream.readObject();

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

        objectInputStream.close();
        objectOutputStream.close();

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
