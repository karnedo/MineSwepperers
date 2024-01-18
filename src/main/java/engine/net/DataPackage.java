package engine.net;

import engine.net.server.Server;

import java.io.Serializable;

public class DataPackage implements Serializable {

    public int x, y;
    public String senderIP;
    public String receiverIP;

    public DataPackage(int x, int y){
        this.x = x;
        this.y = y;
        senderIP = "";
        receiverIP = "";
    }

    public DataPackage(int x, int y, String senderIP, String receiverIP){
        this.x = x;
        this.y = y;
        this.senderIP = senderIP;
        this.receiverIP = receiverIP;
    }

}
