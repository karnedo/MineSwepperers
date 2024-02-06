package engine.net.dataPackage;

import java.io.Serializable;
import java.util.ArrayList;

public class Winners implements Serializable  {

    private String[] names;

    public Winners(String[] names){
        this.names = names;
    }

    public boolean hasWinner(String name){
        for(String n : names)
            if(n.equals(name)) return true;
        return false;
    }

    public String[] getNames(){
        return names;
    }

}
