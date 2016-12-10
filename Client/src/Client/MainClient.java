package Client;

import ADT.UserInfo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class MainClient {
    // Streams for interaction with the server
    private ObjectOutputStream infoToServer;
    private ObjectInputStream infoFromServer;

    // Socket
    private Socket socket; // userinfo-only socket
    private ServerSocket picListenSocket;

    // Inner Class for listening and creating picture socket
    class picListener extends Thread {
        public void run() {
            // Create a severSocket for picture receiving
            try {
                picListenSocket = new ServerSocket(8002);

                while(true) {
                    Socket accepted = picListenSocket.accept();
                    System.out.println("Picture incoming");
                    picHandler newPicHandler = new picHandler(accepted);

                    // Start picHandler when listener says "oh there is a picture coming"
                    newPicHandler.start();
                }
            }
            catch(IOException ex) {
                System.err.println("Picture Handler:" + ex);
            }
        }

        class picHandler extends Thread {
            // This client acts as a server when receiving picture and can have multiple picture sockets
            private Socket picSocket;

            // Constructor
            public picHandler(Socket picSocket) {
                this.picSocket = picSocket;
            }

            // Run
            public void run() {
                // For receiving picture & saving to local disk
                byte[] inputBytes;
                int length;
                ObjectInputStream dataFromServer = null;
                FileOutputStream outToFile = null;

                // Try receiving
                try {
                    dataFromServer = new ObjectInputStream(picSocket.getInputStream());
                    outToFile = new FileOutputStream(new File(new Date().getTime() + ".png"));

                    UserInfo temp;
                    temp = (UserInfo)dataFromServer.readObject();
                    inputBytes = temp.getPicData();
                    length = inputBytes.length;
                    // Read picture from server
                    while( length > 0) {
                        outToFile.write(inputBytes, 0, length);
                        outToFile.flush(); // Immediately write the current portion to the file

                        temp = (UserInfo)dataFromServer.readObject();
                        inputBytes = temp.getPicData();
                        length = inputBytes.length;
                    }
                }
                catch(IOException ex) {
                    System.err.println("Receiving picture:");
                    System.err.println(ex);
                }
                catch(ClassNotFoundException ex) {
                    System.err.println("Receiving picture:");
                    System.err.println(ex);
                }
                finally {
                    try {
                        if (dataFromServer != null)
                            dataFromServer.close();
                        if (outToFile != null)
                            outToFile.close();
                        if(picSocket != null)
                            picSocket.close();
                    }
                    catch(IOException ex) {
                        System.err.println("Closing pic stream:");
                        System.err.println(ex);
                    }
                } // finally
            } // run
        } // pic handler inner-inner class
    } // pic listener inner class

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

            new picListener().start();
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
                picListenSocket.close();
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

    public void sendPic(String userName, String picPath) {
        // TODO: Send picture with target username to server
        try {
            // For sending picture, single time socket
            Socket picSocket = new Socket("172.26.91.76", 8001);
            byte[] sendBytes;
            int length;
            ObjectOutputStream dataToServer = null;
            FileInputStream inFromFile = null;

            dataToServer = new ObjectOutputStream(picSocket.getOutputStream());
            inFromFile = new FileInputStream(new File(picPath));

            sendBytes = new byte[1024];
            inFromFile.read(sendBytes, 0, 1024);
            length = sendBytes.length;

            while(length > 0) {
                UserInfo temp = new UserInfo(null, null, 8);
                temp.setPicData(sendBytes);
                dataToServer.writeObject(temp);
                dataToServer.flush();

                sendBytes = new byte[1024];
                inFromFile.read(sendBytes, 0, 1024);
                length = sendBytes.length;
            }

            if(inFromFile != null)
                inFromFile.close();

            // TODO: Tell the server which user to send the picture to
            UserInfo target = new UserInfo(userName, null, 9);

            dataToServer.writeObject(target);
            dataToServer.flush(); // Immediately send it out

            if(dataToServer != null)
                dataToServer.close();

            if(picSocket != null)
                picSocket.close();
        }
        catch(IOException ex) {
            System.err.println("Sending picture:\n" + ex);
        }
    }
}