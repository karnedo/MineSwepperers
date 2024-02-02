package engine.net.server;

import engine.board.Board;
import engine.graphics.GamePanel;

import javax.swing.*;

public class Server {

    Board board;

    public Server(int width, int height) throws IllegalArgumentException{
        board = new Board(width, height);
    }



    public static void main(String[] args){



    }

}
