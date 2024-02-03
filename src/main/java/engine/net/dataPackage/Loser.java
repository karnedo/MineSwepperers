package engine.net.dataPackage;

import java.io.Serializable;

public class Loser implements Serializable {

    private String loser;

    public Loser(String loser){
        this.loser = loser;
    }

    public String getName(){
        return this.loser;
    }

}
