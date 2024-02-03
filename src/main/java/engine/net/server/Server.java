package engine.net.server;

import engine.Coordinate;
import engine.board.Board;
import engine.graphics.GamePanel;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable{

    private static final int PORT = 6096;

    private ServerSocket server;
    private Board board;
    private int nextTurn;
    private ArrayList<Socket> clients;
    private Thread thread;

    public Server(int width, int height) throws IllegalArgumentException{
        board = new Board(width, height);
        thread = new Thread(this);
    }

    //Looks for the given num of players to join the server. Returns whether there was an error or not
    public boolean startMatchmaking(int players) {

        try {
            server = new ServerSocket(PORT);
            clients = new ArrayList<>();

            System.out.println("Waiting clients...");

            while(clients.size() < players){
                Socket player = server.accept();
                System.out.println("Player with IP " + player.getInetAddress().getHostAddress() + " connected.");

                clients.add( player );
            }

            return true;
        } catch (IOException e) {
            return false;
        }

    }


    public void startGame(){ this.thread.start(); }

    @Override
    public void run() {
        nextTurn = 0;

        //Completa el código
        while(!board.hasFinished()){
            Socket clientTurn = clients.get(nextTurn);
            System.out.println("Turn of player " + nextTurn + " with IP " + clientTurn.getInetAddress().getHostAddress());
            //Notify clientTurn its their turn
            notifyPlayerTurn(clientTurn);

            //Get clientTurn package
            Coordinate coord = receiveCoords(clientTurn);
            System.out.println("Got " + coord.toString());

            //Send package to all clients
            sendCoordinateToAll(coord);

            nextTurn = (nextTurn == clients.size()-1) ? 0 : nextTurn + 1;
        }

    }

    private void sendCoordinateToAll(Coordinate coord) {
        for(Socket s : clients){
            sendObject(s, coord);
        }
    }

    private Coordinate receiveCoords(Socket clientTurn) {
        return (Coordinate) receiveObject(clientTurn);
    }

    private void notifyPlayerTurn(Socket clientTurn){
        //Sends -1, -1 to indicate its this player's turn.
        sendObject(clientTurn, new Coordinate(-1, -1));
    }

    private void sendObject(Socket socket, Serializable object){
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Serializable receiveObject(Socket socket){
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            Serializable obj = (Serializable) objectInputStream.readObject();
            objectInputStream.close();
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args){
        Server server = new Server(8, 8);
        if(server.startMatchmaking(2)){
            System.out.println("Starting game...");
            server.startGame();
        }else{
            System.out.println("Couldn't make matchmaking.");
        }

    }
}
