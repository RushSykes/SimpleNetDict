package Server;

import ADT.UserInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer {
    // Streams for interaction with the client
    private DataOutputStream infoToClient;
    private ObjectInputStream infoFromClient;

    // Connector
    private DBConnector connector = new DBConnector();

    // Constructor
    public MainServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(8000);
            // identify each client with a number
            int clientNo = 1;

            while(true) {
                // create a set of 1 thread and 1 socket for every single new client
                Socket socket = serverSocket.accept();
                HandleClient newClient = new HandleClient(socket);

                // Start the thread for the new client
                new Thread(newClient).start();

                // Prepare for next client
                clientNo++;
            }
        }
        catch(IOException ex) {
            System.err.println("Server:" + ex);
        }
    }

    // Inner class for handle multi clients
    class HandleClient implements Runnable {
        // This would be the unique socket for the specific client
        private Socket socket;
        private DBConnector connector;

        public HandleClient(Socket socket) {
            this.socket = socket;
        }

        // Run a thread that has just been created
        public void run() {
            // Handle client info and respond
            try {
                // Info that comes from the client
                infoFromClient = new ObjectInputStream(socket.getInputStream());
                // Prepare info that will be sent to the client
                infoToClient = new DataOutputStream(socket.getOutputStream());

                // Read
                Object infoObject = infoFromClient.readObject();

                UserInfo userInfo = (UserInfo)infoObject;

                connector = new DBConnector();

                // Valid connection
                if(connector.getConnStat()) {
                    // 0 for logIn
                    if (userInfo.getMode() == 0) {
                        switch(connector.findUser_Login(userInfo.getUserName(), userInfo.getPassword())) {
                            case 0: infoToClient.writeInt(0); break;
                            case 1: infoToClient.writeInt(1); break;
                            case 2: infoToClient.writeInt(2); break;
                            default: infoToClient.writeInt(-1);
                        }
                    }
                    // 1 for signUp
                    else if (userInfo.getMode() == 1) {
                        switch(connector.findUser_Register(userInfo.getUserName(), userInfo.getPassword(), userInfo.getConfirm())) {
                            case 0: infoToClient.writeInt(0); break;
                            case 1: infoToClient.writeInt(1); break;
                            case 2: infoToClient.writeInt(2); break;
                            default: infoToClient.writeInt(-1);
                        }
                    }
                }
            }
            catch(IOException ex) {
                System.err.println("Server:" + ex);
            }
            catch(ClassNotFoundException ex) {
                System.err.println("Server:" + ex);
            }
        }
    }
}
