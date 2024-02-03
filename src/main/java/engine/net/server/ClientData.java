package engine.net.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class ClientData {

    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public ClientData(Socket socket) throws IOException {
        this.socket = socket;
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
    }

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

    public Serializable receiveObject(){
        try {
            Serializable obj = (Serializable) input.readObject();
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
