package engine.net.data;

import java.io.Serializable;

public class Coordinate implements Serializable {
    private final int x, y;

    public Coordinate(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public static Coordinate stringToCoord(String text){
        return new Coordinate(
                Integer.parseInt(text.split(", ")[0]),
                Integer.parseInt(text.split(", ")[1])
                );
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
