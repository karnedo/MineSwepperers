package engine.net.server;

import engine.net.dataPackage.Coordinate;
import engine.board.Board;
import engine.net.dataPackage.ClientData;
import engine.net.dataPackage.Loser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

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

            int n = 0;
            while(clients.size() < players){
                Socket player = server.accept();

                ClientData clientData = new ClientData(player, "UNKNOWN");
                String name = (String) clientData.receiveObject();
                clientData.setName(name);

                clients.add( clientData );
            }

            //Send all players the game's board
            for(ClientData cli : clients){
                cli.sendObject(board);
            }

            return true;
        } catch (IOException | ClassNotFoundException e) {
            return false;
        }

    }


    public void startGame(){ this.thread.start(); }

    @Override
    public void run() {
        nextTurn = 0;

        while (!board.hasFinished()) {
            Iterator<ClientData> iter = clients.iterator();
            while(iter.hasNext()){
                ClientData client = iter.next();
                System.out.println("Turn of player with IP " + client.getHostAddress());

                // Notify client it's their turn
                notifyPlayerTurn(client);

                // Get client's move
                Coordinate coord = null;
                boolean bombFound = false;
                try {
                    coord = receiveCoords(client);
                    bombFound = !this.board.reveal(coord.getX(), coord.getY());
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("One or more clients have lost connection. Game is aborted.");
                    return;
                }
                System.out.println("Got " + coord);
                if(bombFound) {
                    System.out.println("Mine found by " + client.getHostAddress() + " at " + coord);
                    sendLostNotification(new Loser(client.getName()));
                    iter.remove();
                }


                // Send move to all clients
                System.out.println("Sending " + coord + " to all clients..");
                sendCoordinateToAll(coord);

                System.out.println("-----------------------");
            }
            /*for (ClientData client : clients) {
                System.out.println("Turn of player with IP " + client.getHostAddress());

                // Notify client it's their turn
                notifyPlayerTurn(client);

                // Get client's move
                Coordinate coord = null;
                boolean bombFound = false;
                try {
                    coord = receiveCoords(client);
                    bombFound = !this.board.reveal(coord.getX(), coord.getY());
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("One or more clients have lost connection. Game is aborted.");
                    return;
                }
                System.out.println("Got " + coord);
                if(bombFound) {
                    System.out.println("Mine found by " + client.getHostAddress() + " at " + coord);
                    sendLostNotification(new Loser(client.getName()));
                }


                // Send move to all clients
                System.out.println("Sending " + coord + " to all clients..");
                sendCoordinateToAll(coord);

                System.out.println("-----------------------");
            }*/

            try {
                Thread.sleep(100); // Adjust the sleep duration as needed
            } catch (InterruptedException e) {
                e.printStackTrace(); // Handle InterruptedException appropriately
            }
        }

        System.out.println("Game finished.");

    }

    //Sends everyone a notification of the loser's IP
    private void sendLostNotification(Loser loser) {
        //This needs to be done with an Iterator as otherwise would throw a ConcurrentModificationException
        Iterator<ClientData> iter = clients.iterator();
        while(iter.hasNext()){
            ClientData cli = iter.next();
            cli.sendObject(loser);
        }
    }

    private void sendCoordinateToAll(Coordinate coord) {
        for(ClientData cli : clients){
            cli.sendObject(coord);
        }
    }

    private Coordinate receiveCoords(ClientData clientTurn) throws IOException, ClassNotFoundException {
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
