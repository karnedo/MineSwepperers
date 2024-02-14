package engine.net.data;

import java.io.Serializable;

public class Loser implements Serializable {

    private final String loser;

    public Loser(String loser){
        this.loser = loser;
    }

    public String getName(){
        return this.loser;
    }

}
