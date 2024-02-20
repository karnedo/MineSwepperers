package engine.net.data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class ClientData {

    private String name;
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public ClientData(Socket socket, String name) throws IOException {
        this.socket = socket;
        this.name = name;
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){this.name = name;}

    public String getHostAddress(){
        return this.socket.getInetAddress().getHostAddress();
    }

    public void sendObject(Serializable object){
        try {
            output.writeObject(object);
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean close(){
        try {
            output.close();
            input.close();
            socket.close();
            return true;
        } catch (IOException e) { return false; }
    }

    public Serializable receiveObject() throws IOException, ClassNotFoundException{
        return (Serializable) input.readObject();
    }

}
