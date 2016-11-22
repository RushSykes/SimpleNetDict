package Dict.Client;

import Dict.ADT.UserInfo;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MainClient {
    // Streams for interaction with the server
    private ObjectOutputStream infoToServer;
    private DataInputStream infoFromServer;
    // User info
    private UserInfo userInfo;

    // Socket
    private Socket socket;

    // Constructor
    // Prepare socket
    public MainClient() {
        try {
            // Establish connection to server
            socket = new Socket("172.26.7.66", 8000);

            // Create streams
            infoFromServer = new DataInputStream(socket.getInputStream());
            infoToServer= new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException ex) {
            System.err.println("Client:" + ex);
        }
    }

    public int logIn(String userName, String password) {
        // Return :
        // -1 for exception
        // 0 for success
        // 1 for wrong password
        // 2 for username does not exist
        int flag;
        UserInfo userInfo = new UserInfo(userName, password, 0);
        try {
            infoToServer.writeObject(userInfo);
            infoToServer.flush(); // Immediately send it out

            flag = infoFromServer.readInt(); // 0 1 2, or exception -1
        }
        catch(IOException ex) {
            System.err.println("Client:" + ex);
            flag = -1;
        }
        return flag;
    }

    public int signUp(String userName, String password, String confirm) {
        // Return :
        // -1 for exception
        // 0 for success
        // 1 for password mismatching
        // 2 for user exists
        int flag;

        UserInfo userInfo = new UserInfo(userName, password, 1);
        userInfo.setConfirm(confirm);
        try {
            infoToServer.writeObject(userInfo);
            infoToServer.flush(); // Immediately send it out

            flag = infoFromServer.readInt(); // 0 1 2, or exception -1
        }
        catch(IOException ex) {
            System.err.println("Client:" + ex);
            flag = -1;
        }

        return flag;
    }
}