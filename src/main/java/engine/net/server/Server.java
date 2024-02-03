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
    private ArrayList<ClientData> clients;
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

                clients.add( new ClientData(player) );
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

        /*while(!board.hasFinished()){
            System.out.println("Turn of player " + nextTurn + " with IP " + clients.get(nextTurn).getHostAddress());
            //Notify clientTurn its their turn
            notifyPlayerTurn(clients.get(nextTurn));

            //Get clientTurn package
            Coordinate coord = receiveCoords(clients.get(nextTurn));
            System.out.println("Got " + coord.toString());

            //Send package to all clients
            sendCoordinateToAll(coord);

            nextTurn++;
            nextTurn = (nextTurn == clients.size()) ? 0 : nextTurn;
        }*/
        while (!board.hasFinished()) {
            for (ClientData client : clients) {
                System.out.println("Turn of player with IP " + client.getHostAddress());

                // Notify client it's their turn
                notifyPlayerTurn(client);

                // Get client's move
                Coordinate coord = receiveCoords(client);
                System.out.println("Got " + coord.toString());

                // Send move to all clients
                System.out.println("Sending " + coord.toString() + " to all clients..");
                sendCoordinateToAll(coord);

                System.out.println("-----------------------");
            }

            try {
                Thread.sleep(100); // Adjust the sleep duration as needed
            } catch (InterruptedException e) {
                e.printStackTrace(); // Handle InterruptedException appropriately
            }
        }

    }

    private void sendCoordinateToAll(Coordinate coord) {
        for(ClientData cli : clients){
            cli.sendObject(coord);
        }
    }

    private Coordinate receiveCoords(ClientData clientTurn) {
        return (Coordinate) clientTurn.receiveObject();
    }

    private void notifyPlayerTurn(ClientData clientTurn){
        //Sends -1, -1 to indicate its this player's turn.
        clientTurn.sendObject(new Coordinate(-1, -1));
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
