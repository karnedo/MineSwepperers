package engine.net.server;

import engine.net.dataPackage.Coordinate;
import engine.board.Board;
import engine.net.dataPackage.ClientData;
import engine.net.dataPackage.Loser;
import engine.net.dataPackage.Winners;

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
            while(iter.hasNext() && !board.hasFinished()){
                ClientData client = iter.next();
                System.out.println("Turn of player with IP " + client.getHostAddress());

                // Notify client it's their turn
                notifyPlayerTurn(client);

                // Get client's move
                Coordinate coord;
                boolean bombFound;
                try {
                    coord = receiveCoords(client);
                    bombFound = !this.board.reveal(coord.getX(), coord.getY());
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("One or more clients have lost connection. Game is aborted.");
                    return;
                }
                System.out.println("Got " + coord);
                if(bombFound) {
                    System.out.println("Mine found by " + client.getName() + " at " + coord);
                    sendLostNotification(new Loser(client.getName()));
                    iter.remove();
                }


                // Send move to all clients
                System.out.println("Sending " + coord + " to all clients..");
                sendCoordinateToAll(coord);

                System.out.println("-----------------------");
            }

            if(clients.isEmpty()){
                System.out.println("Nobody won.");
                break;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Game finished.");
        if(!clients.isEmpty()){
            System.out.println("Sending the notification to all winners...");
            Winners winners = new Winners(getNames());
            sendWinNotification(winners);
            System.out.println("Winners: ");
            for(String name : winners.getNames()){
                System.out.print(name + " | ");
            }
        }else{
            System.out.println("No winners.");
        }

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

    private String[] getNames(){
        Iterator<ClientData> iter = clients.iterator();
        String[] names = new String[clients.size()];
        int n = 0;
        while(iter.hasNext()){
            ClientData cli = iter.next();
            names[n++] = cli.getName();
        }
        return names;
    }

    private void sendWinNotification(Winners winners) {
        Iterator<ClientData> iter = clients.iterator();
        while(iter.hasNext()){
            ClientData cli = iter.next();
            cli.sendObject(winners);
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
        if(args.length == 2){
            try{
                int boardSize = Integer.parseInt(args[0]);
                int numPlayers = Integer.parseInt(args[1]);
                if(boardSize < 8 || numPlayers < 1){
                    System.out.println("Board size has to be bigger than 7 and number of players at least 1");
                }else{
                    Server server = new Server(boardSize, boardSize);
                    if(server.startMatchmaking(numPlayers)){
                        System.out.println("Starting game...");
                        server.startGame();
                    }else{
                        System.out.println("Couldn't make matchmaking.");
                    }
                }
            }catch (NumberFormatException e){
                System.out.println("Usage: java server.java <Board size> <Number of players>");
            }
        }else{
            System.out.println("Usage: java server.java <Board size> <Number of players>");
        }

    }
}
