package Server;

import ADT.Jinshan;
import ADT.UserInfo;
import ADT.Youdao;
import ADT.Bing;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainServer {
    // clientNo
    int clientNo = 0;
    // userList
    Map<String, String> userMap = new HashMap<>();

    // Constructor
    public MainServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(8000);
            // identify each client with a number
            System.out.println("Server started");
            while(true) {
                // create a set of 1 thread and 1 socket for every single new client
                Socket socket = serverSocket.accept();
                System.out.println("Request from " + socket.getInetAddress());
                HandleClient newClient = new HandleClient(socket);

                // Start the thread for the new client
                newClient.start();

                // Prepare for next client
                clientNo++;
                System.out.println("Client now: " + clientNo);
            }
        }
        catch(IOException ex) {
            System.err.println("Server:" + ex);
        }
    }

    // Inner class for handle multi clients
    class HandleClient extends Thread {
        // This would be the unique socket for the specific client
        private Socket socket;
        private ServerSocket picListenSocket;

        // Streams for interaction with the client
        private ObjectOutputStream infoToClient;
        private ObjectInputStream infoFromClient;

        // Connector
        private DBConnector connector;

        // Each client thread has this picture listener
        // This is for the very client this thread is dealing with
        // Because every thread only deals with one client
        // So we just need to accept one socket
        // Check if it's this thread's guest's ip
        class picListener extends Thread {
            public void run() {
                try {
                    picListenSocket = new ServerSocket(8001);
                    Socket accepted = picListenSocket.accept();
                    picHandler newPicHandler;
                    if(accepted.getInetAddress().equals(socket.getInetAddress())) {
                        newPicHandler = new picHandler(accepted);
                        newPicHandler.start();
                    }
                }
                catch(IOException ex) {
                    System.err.println("Picture Handler:" + ex);
                }
            }

            // Inner inner class
            class picHandler extends Thread {
                private Socket picSocket;

                public picHandler(Socket picSocket) {
                    this.picSocket = picSocket;
                }

                public void run() {
                    // Try receiving
                    try {
                        // For receiving picture & saving to local disk
                        byte[] inputBytes;
                        int length;
                        DataInputStream dataFromClient = null;
                        FileOutputStream outToFile = null;
                        ObjectInputStream forwardFromClient = null;

                        dataFromClient = new DataInputStream(picSocket.getInputStream());
                        File tempFile = new File(new Date().getTime() + ".png");

                        // Write to server local disk
                        outToFile = new FileOutputStream(tempFile);
                        inputBytes = new byte[1024];

                        // Read picture from client
                        while((length = dataFromClient.read(inputBytes, 0, inputBytes.length)) > 0) {
                            outToFile.write(inputBytes, 0, length);
                            outToFile.flush(); // Immediately write the current portion to the file
                        }

                        // Close picture input and file output stream
                        if (dataFromClient != null)
                            dataFromClient.close();
                        if (outToFile != null)
                            outToFile.close();

                        // For forwarding(sending)
                        // Get target info
                        forwardFromClient = new ObjectInputStream(picSocket.getInputStream());
                        UserInfo targetInfo = (UserInfo)forwardFromClient.readObject();
                        String targetName = targetInfo.getUserName();
                        String targetIp = userMap.get(targetName).replaceAll("/", "");

                        if(forwardFromClient != null)
                            forwardFromClient.close();
                        if(picSocket != null)
                            picSocket.close();

                        // Get picture file that just has been received
                        String tempPath = tempFile.getAbsolutePath();
                        byte[] sendBytes;
                        DataOutputStream dataToClient = null;
                        FileInputStream inFromFile = null;

                        // Create a socket with the target client
                        Socket forwardSocket = new Socket(targetIp, 8002);

                        dataToClient = new DataOutputStream(forwardSocket.getOutputStream());
                        inFromFile = new FileInputStream(new File(tempPath));

                        sendBytes = new byte[1024];
                        while((length = inFromFile.read(sendBytes, 0, sendBytes.length)) > 0) {
                            dataToClient.write(sendBytes, 0, length);
                            dataToClient.flush();
                        }

                        if(dataToClient != null)
                            dataToClient.close();
                        if(inFromFile != null)
                            inFromFile.close();
                        if(forwardSocket != null)
                            forwardSocket.close();
                    }
                    catch(IOException ex) {
                        System.err.println("Picture handler:");
                        System.err.println(ex);
                    }
                    catch(ClassNotFoundException ex) {
                        System.err.println("Picture handler:");
                        System.err.println(ex);
                    }
                }
            } // picHandler
        } // picListener

        public HandleClient(Socket socket) {
            this.socket = socket;
        }

        // Run a thread that has just been created
        public void run() {
            // Handle client info and respond
            try {
                // Info that comes from the client
                System.out.println(socket.getInputStream());

                // Prepare info that will be sent to the client
                infoToClient = new ObjectOutputStream(socket.getOutputStream());
                infoToClient.flush();
                infoFromClient = new ObjectInputStream(socket.getInputStream());

                connector = new DBConnector();
                connector.connect();

                while(true) {
                    // Read
                    Object infoObject = infoFromClient.readObject();

                    UserInfo userInfo = (UserInfo) infoObject;

                    // Valid connection
                    if (connector.getConnStat()) {
                        // 0 for logIn
                        if (userInfo.getMode() == 0) {
                            System.out.println("=====Login=====");
                            System.out.println("User: " + userInfo.getUserName());
                            System.out.println("Password: " + userInfo.getPassword());
                            switch (connector.findUser_Login(userInfo.getUserName(), userInfo.getPassword())) {
                                case 0:
                                    infoToClient.writeObject(new Integer(0));
                                    userMap.put(userInfo.getUserName(), socket.getInetAddress().toString());
                                    System.out.println("LogIn stat: 0");
                                    break;
                                case 1:
                                    infoToClient.writeObject(new Integer(1));
                                    System.out.println("LogIn stat: 1");
                                    break;
                                case 2:
                                    infoToClient.writeObject(new Integer(2));
                                    System.out.println("LogIn stat: 2");
                                    break;
                                default:
                                    infoToClient.writeObject(new Integer(-1));
                                    System.out.println("LogIn stat: -1");
                            }
                            System.out.println("=====^^^^^=====");
                        }
                        // 1 for signUp
                        else if (userInfo.getMode() == 1) {
                            System.out.println("=====Register=====");
                            System.out.println("User: " + userInfo.getUserName());
                            System.out.println("Password: " + userInfo.getPassword());
                            System.out.println("Confirm: " + userInfo.getConfirm());
                            switch (connector.findUser_Register(userInfo.getUserName(), userInfo.getPassword(), userInfo.getConfirm())) {
                                case 0:
                                    infoToClient.writeObject(new Integer(0));
                                    System.out.println("Register stat: 0");
                                    break;
                                case 1:
                                    infoToClient.writeObject(new Integer(1));
                                    System.out.println("Register stat: 1");
                                    break;
                                case 2:
                                    infoToClient.writeObject(new Integer(2));
                                    System.out.println("Register stat: 2");
                                    break;
                                default:
                                    infoToClient.writeObject(new Integer(-1));
                                    System.out.println("Register stat: -1");
                            }
                            System.out.println("=====^^^^^=====");
                        }
                        // 2 for query
                        else if(userInfo.getMode() == 2) {
                            switch(userInfo.getQueryType()) {
                                // 0 for Youdao
                                case 0:
                                    Youdao search0 = new Youdao();
                                    // Search the words using API
                                    search0.query(userInfo.getWord());
                                    // Add explanation data to the userInfo data pack
                                    userInfo.setResult(search0.getExplains());
                                    // Add dictScore to the data pack
                                    userInfo.setDictScore(connector.dictScore(0));
                                    // Send it back
                                    infoToClient.writeObject(userInfo);
                                    break;
                                // 1 for Bing
                                case 1:
                                    Bing search1 = new Bing();
                                    search1.query(userInfo.getWord());
                                    userInfo.setResult(search1.getExplains());
                                    userInfo.setDictScore(connector.dictScore(1));
                                    infoToClient.writeObject(userInfo);
                                    break;
                                //2 for Jinshan
                                case 2:
                                    Jinshan search2 = new Jinshan();
                                    search2.query(userInfo.getWord());
                                    userInfo.setResult(search2.getExplains());
                                    userInfo.setDictScore(connector.dictScore(2));
                                    infoToClient.writeObject(userInfo);
                                    break;
                                default:
                            }
                        }
                        // 3 for thumbup
                        else if(userInfo.getMode() == 3){
                            int stat;
                            String word = userInfo.getWord();
                            String user = userInfo.getUserName();
                            int dictType = userInfo.getQueryType();
                            stat = connector.wordLiked(word, user, dictType);
                            // 0 he liked it this time
                            if(stat == 0) {
                                userInfo.setLiked(true);
                            }
                            // 1 he disliked it this time
                            else if(stat == 1) {
                                userInfo.setLiked(false);
                            }
                            infoToClient.writeObject(userInfo);
                        }
                        // 4 and 5 for real time info userList
                        else if(userInfo.getMode() == 4) {
                            ArrayList<String> users = new ArrayList<>();
                            users = connector.onlineUser();

                            for(int i = 0 ; i < users.size(); i++) {
                                UserInfo temp = new UserInfo(users.get(i), null, 4);
                                infoToClient.writeObject(temp);
                            }
                            infoToClient.writeObject(new UserInfo(null, null, 5));
                        }
                        // 6 for exiting the program, and shutdown the Handle client thread
                        // 7 to inform the client you're good to go
                        else if(userInfo.getMode() == 6) {
                            if(connector.logOut(userInfo.getUserName())==1) {
                                UserInfo respond = new UserInfo(userInfo.getUserName(), null, 7);
                                infoToClient.writeObject(respond);
                                userMap.remove(userInfo.getUserName());
                                clientNo--;
                                System.out.println("=====LogOut=====");
                                System.out.println("Connection canceled by user");
                                System.out.println("User IP: " + socket.getInetAddress());
                                System.out.println("Client now: " + clientNo);
                                System.out.println("=====^^^^^=====");
                                break; // break out of while (to finally)
                            }
                        }
                    }
                } // while client not logged out
            } // try
            catch(IOException ex) {
                System.err.println("Server: " + ex);
            }
            catch(ClassNotFoundException ex) {
                System.err.println("Server: " + ex);
            }
            finally {
                // Release the resource the thread applied
                try {
                    infoToClient.close();
                    infoFromClient.close();
                    socket.close();
                }
                catch(IOException ex) {
                    System.err.println("Server: " + ex);
                }
            }
        }
    } // Inner class ClientHandler

    public static void main(String[] args) {
        MainServer server = new MainServer();
    }
}
