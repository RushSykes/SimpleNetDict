package Client;

import ADT.UserInfo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class MainClient {
    // Streams for interaction with the server
    private ObjectOutputStream infoToServer;
    private ObjectInputStream infoFromServer;

    // Socket
    private Socket socket;

    // Constructor
    // Prepare socket
    public MainClient() {
        try {
            // Establish connection to server
            socket = new Socket("172.26.91.76", 8000);

            // Create streams
            infoToServer= new ObjectOutputStream(socket.getOutputStream());
            infoToServer.flush();
            infoFromServer = new ObjectInputStream(socket.getInputStream());
        }
        catch (IOException ex) {
            System.err.println("Client: " + ex);
        }
    }

    public int logIn(String userName, String password) {
        // Return :
        // -1 for exception
        // 0 for success
        // 1 for wrong password
        // 2 for username does not exist
        // 3 for already logged in
        int flag;
        UserInfo userInfo = new UserInfo(userName, password, 0);
        try {
            infoToServer.writeObject(userInfo);
            infoToServer.flush(); // Immediately send it out

            flag = (Integer)infoFromServer.readObject(); // 0 1 2, or exception -1
            System.out.println("LogIn stat: " + flag);
        }
        catch(IOException ex) {
            System.err.println("Client: " + ex);
            flag = -1;
        }
        catch(ClassNotFoundException ex) {
            System.err.println("Client: " + ex);
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

            flag = (Integer)infoFromServer.readObject(); // 0 1 2, or exception -1
            System.out.println("Register stat: " + flag);
        }
        catch(IOException ex) {
            System.err.println("Client:" + ex);
            flag = -1;
        }
        catch(ClassNotFoundException ex) {
            System.err.println("Client: " + ex);
            flag = -1;
        }

        return flag;
    }

    public UserInfo query(String word, int type) {
        UserInfo request = new UserInfo(word, 2, type);
        UserInfo respond;

        try {
            infoToServer.writeObject(request);
            infoToServer.flush(); // Immediately send it out

            respond = (UserInfo)infoFromServer.readObject();
        }
        catch(IOException ex) {
            System.err.println("Client:" + ex);
            return null;
        }
        catch(ClassNotFoundException ex) {
            System.err.println("Client: " + ex);
            return null;
        }
        return respond;
    }

    public UserInfo[] queryAll(String word) {
        UserInfo[] request = new UserInfo[3];
        UserInfo[] respond = new UserInfo[3];

        for(int i = 0 ; i < request.length; i++) {
            request[i] = new UserInfo(word, 2, i);
        }

        try {
            infoToServer.writeObject(request[0]);
            infoToServer.flush(); // Immediately send it out

            respond[0] = (UserInfo)infoFromServer.readObject();

            infoToServer.writeObject(request[1]);
            infoToServer.flush(); // Immediately send it out

            respond[1] = (UserInfo)infoFromServer.readObject();

            infoToServer.writeObject(request[2]);
            infoToServer.flush(); // Immediately send it out

            respond[2] = (UserInfo)infoFromServer.readObject();
        }

        catch(IOException ex) {
            System.err.println("Client:" + ex);
            return null;
        }
        catch(ClassNotFoundException ex) {
            System.err.println("Client: " + ex);
            return null;
        }
        return respond;
    }

    public UserInfo likeIt(String word, String user, int dictType) {
        UserInfo request = new UserInfo(word, user, 3, dictType);
        UserInfo respond;

        try {
            infoToServer.writeObject(request);
            infoToServer.flush(); // Immediately send it out

            respond = (UserInfo)infoFromServer.readObject();
        }
        catch(IOException ex) {
            System.err.println("Client:" + ex);
            return null;
        }
        catch(ClassNotFoundException ex) {
            System.err.println("Client: " + ex);
            return null;
        }
        return respond;
    }

    public ArrayList<String> currentUserInfo() {
        UserInfo request = new UserInfo(null, null ,4);
        UserInfo respond;
        ArrayList<String> result = new ArrayList<>();

        try {
            infoToServer.writeObject(request);
            infoToServer.flush(); // Immediately send it out

            respond = (UserInfo)infoFromServer.readObject();
            while(respond.getMode() != 5) {
                result.add(respond.getUserName());
                respond = (UserInfo)infoFromServer.readObject();
            }

            return result;
        }
        catch(IOException ex) {
            System.err.println("Client:" + ex);
            return null;
        }
        catch(ClassNotFoundException ex) {
            System.err.println("Client: " + ex);
            return null;
        }
    }

    // Return :
    // -1 for exception
    // 0 for success
    public int logOut(String userName) {
        UserInfo request = new UserInfo(userName, null, 6);
        UserInfo respond;

        try {
            infoToServer.writeObject(request);
            infoToServer.flush(); // Immediately send it out

            respond = (UserInfo)infoFromServer.readObject();
            if(respond.getMode() == 7) {
                infoToServer.close();
                infoFromServer.close();
                socket.close();
                return 0;
            }
        }
        catch(IOException ex) {
            System.err.println("Client:" + ex);
            return -1;
        }
        catch(ClassNotFoundException ex) {
            System.err.println("Client: " + ex);
            return -1;
        }

        return -1;
    }
}