package engine.net.data;

import java.io.Serializable;

public class Winners implements Serializable  {

    private final String[] names;

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
