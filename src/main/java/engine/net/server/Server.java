package engine.net.server;

import engine.net.data.Coordinate;
import engine.board.Board;
import engine.net.data.ClientData;
import engine.net.data.Loser;
import engine.net.data.Winners;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class Server implements Runnable{

    private static final int PORT = 19788;

    private ServerSocket server;
    private Board board;
    private ArrayList<ClientData> clients;
    private Thread thread;
    private guiService gui;

    private int players;

    public Server(guiService gui){
        this.gui = gui;
        thread = new Thread(this);
    }

    public Server(int width, int height) throws IllegalArgumentException{
        generateBoard(width, height);
        thread = new Thread(this);
    }

    protected void generateBoard(int width, int height) throws IllegalArgumentException{
        board = new Board(width, height);
    }

    protected void setPlayers(int players){ this.players = players; }

    //Looks for the given num of players to join the server. Returns whether there was an error or not
    private boolean startMatchmaking(int players) {
        try {
            print("Matchmaking started.");
            server = new ServerSocket(PORT);
            clients = new ArrayList<>();

            print("Waiting clients...");

            while(clients.size() < players){
                Socket player = server.accept();

                ClientData clientData = new ClientData(player, "UNKNOWN");
                String name = (String) clientData.receiveObject();
                clientData.setName(name);

                clients.add( clientData );
                print("Player with IP " + clientData.getHostAddress() + " joined.");
            }

            //Send all players the game's board
            for(ClientData cli : clients){
                cli.sendObject(board);
            }

            print("All players ready");
            return true;
        } catch (IOException | ClassNotFoundException e) {
            print("A problem making matchmaking has ocurred.");
            print(e.toString());
            return false;
        }

    }


    public void startGame(){ this.thread.start(); }

    @Override
    public void run() {
        if(!startMatchmaking(players)) return;
        print("Starting game...");
        print("----------------");
        while (!board.hasFinished()) {
            Iterator<ClientData> iter = clients.iterator();
            while(iter.hasNext() && !board.hasFinished()){
                ClientData client = iter.next();
                print("Turn of player with IP " + client.getHostAddress());

                // Notify client it's their turn
                notifyPlayerTurn(client);

                // Get client's move
                Coordinate coord;
                boolean bombFound;
                try {
                    coord = receiveCoords(client);
                    bombFound = !this.board.reveal(coord.getX(), coord.getY());
                } catch (IOException | ClassNotFoundException e) {
                    print("One or more clients have lost connection. Game is aborted.");
                    return;
                }
                print("Server received " + coord);
                if(bombFound) {
                    print("Mine found by " + client.getName() + " at " + coord);
                    sendLostNotification(new Loser(client.getName()));
                    iter.remove();
                }


                // Send move to all clients
                print("Sending " + coord + " to all clients..");
                sendCoordinateToAll(coord);

                print("-----------------------");
            }

            if(clients.isEmpty()){
                print("Nobody won.");
                break;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        print("Game finished.");
        if(!clients.isEmpty()){
            print("Sending the notification to all winners...");
            Winners winners = new Winners(getNames());
            sendWinNotification(winners);
            print("Winners: ");
            for(String name : winners.getNames()){
                print(name + " | ");
            }
        }else{
            print("No winners.");
        }

        closeConnections();



    }

    private void print(String msg){
        if(gui == null){
            System.out.println(msg);
        }else{
            gui.printMessage(msg);
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

    private void closeConnections() {
        Iterator<ClientData> iter = clients.iterator();
        while(iter.hasNext()){
            ClientData cli = iter.next();
            cli.close();
        }
        try {
            server.close();
        } catch (IOException ignored) {}
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
        }else if(args.length == 0){
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new ServerGUI().setVisible(true);
                }
            });
        } else{
            System.out.println("Usage: java server.java <Board size> <Number of players>");
        }

    }
}
