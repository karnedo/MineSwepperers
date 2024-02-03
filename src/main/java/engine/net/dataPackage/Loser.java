package engine.net.dataPackage;

import java.io.Serializable;

public class Loser implements Serializable {

    private String IP;

    public Loser(String IP){
        this.IP = IP;
    }

    public String getIP(){
        return this.IP;
    }

}
