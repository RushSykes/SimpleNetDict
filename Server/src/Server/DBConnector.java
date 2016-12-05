package Server;

import java.sql.*;
import java.util.ArrayList;

/*
 * This is the connector class used for C/S mode
 * Connecting to database
 */
public class DBConnector {
    private String DB_Driver;
    private String DB_Url;
    private String DB_User;
    private String DB_Password;
    private Connection DB_Conn;
    private boolean connDone;

    // =========================================
    // Constructor
    public DBConnector() {
        DB_Driver = "com.mysql.jdbc.Driver";
        DB_Url = "jdbc:mysql://172.26.91.76:3306/dictdatabase";
        DB_User = "Rush";
        DB_Password = "test1234";
        DB_Conn = null;
        connDone = false;
    }

    // =========================================
    // Create connection
    public void connect() {
        // Register driver
        try {
            Class.forName(DB_Driver);
        } catch (ClassNotFoundException e) {
            System.out.println("Driver loading failed");
            e.printStackTrace();
        }

        // Create connection
        try {
            DB_Conn = DriverManager.getConnection(DB_Url, DB_User, DB_Password);
            connDone = true;
        } catch (SQLException ex) {
            // Handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    // Get connection stat for top-level code parts
    public boolean getConnStat() {
        if(connDone)
            return true;
        else
            return false;
    }

    // =========================================
    // Deal with user data in db, login
    // Return :
    // -1 for exception
    // 0 for success
    // 1 for wrong password
    // 2 for username does not exist
    // 3 for already logged in
    public int findUser_Login(String userName, String password) {
        // Make sure the connection is established
        if(connDone) {
            try {
                Statement stmt = DB_Conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE userName =\'" + userName + "\' ");
                while(rs.next()) {
                    if(rs.getString("password").equals(password)) {
                        stmt.executeUpdate("UPDATE users SET stat = 1 WHERE userName ='" + userName + "\' ");
                        stmt.close();
                        return 0;
                    }
                    else if(rs.getInt("stat") == 1) {
                        stmt.close();
                        return 3;
                    }
                    else
                        stmt.close();
                        return 1;
                }
                stmt.close();
                return 2;
            }
            catch(SQLException ex) {
                // Handle any errors
                System.out.println("Find user in login");
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
                return -1;
            }
        } // Connection established
        System.out.println("Connection with database not established");
        return -1;
    }

    // =========================================
    // Deal with user data in db, register
    // Return :
    // -1 for exception
    // 0 for success
    // 1 for password mismatching
    // 2 for user exists
    public int findUser_Register(String userName, String password, String confirm) {
        if (connDone) {
            try {
                Statement stmt = DB_Conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE userName =\'" + userName + "\'");
                while (!rs.next()) {
                    if (password.equals(confirm)) {
                        stmt.executeUpdate("INSERT INTO users (userName, password) VALUES (\'" + userName + "\', \'" + password + "\')");
                        stmt.close();
                        return 0;
                    }
                    else
                        stmt.close();
                        return 1;
                }
                stmt.close();
                return 2;
            }
            catch (SQLException ex) {
                System.out.println("Find user in register");
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
                return -1;
            }
        }
        System.out.println("Connection with database not established");
        return -1;
    }

    // =========================================
    // Deal with thumb up data
    // Return :
    // 0, for this user thumb this up
    // 1, this user has already thumb this up, so he's disliking it
    public int wordLiked(String word, String user, int dictType) {
        if(connDone) {
            try {
                String dictName = null;
                switch(dictType) {
                    case 0: dictName = "Youdao"; break;
                    case 1: dictName = "Bing"; break;
                    case 2: dictName = "Jinshan"; break;
                    default:
                }
                Statement stmt = DB_Conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM thumbup WHERE word = \'" + word + "\' AND dictName = \'" + dictName + "\' AND user = \'" + user + "\'");

                // If this user has thumbed up for this word before, now he's canceling it
                while(rs.next()) {
                    stmt.executeUpdate("DELETE FROM thumbup WHERE word = \'" + word + "\' AND dictName = \'" + dictName + "\' AND user = \'" + user + "\'");
                    stmt.executeUpdate("UPDATE dicts SET dictScore = dictScore - 1 WHERE dictName = \'" + dictName + "\'");
                    return 1;
                }
                // This user thumb up the word this time (he may have disliked it before)
                stmt.executeUpdate("INSERT INTO thumbup (word, dictName, user) VALUES (\'" + word + "\', \'" + dictName + "\', \'" + user +"\')");
                stmt.executeUpdate("UPDATE dicts SET dictScore = dictScore + 1 WHERE dictName = \'" + dictName + "\'");
                stmt.close();
                return 0;
            }
            catch (SQLException ex) {
                System.out.println("Thumb Up");
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
                return -1;
            }
        }
        System.out.println("Connection with database not established");
        return -1;
    }
    public int dictScore(int dictType) {
        if(connDone) {
            int score = 0;
            try {
                String dictName = null;
                switch(dictType) {
                    case 0: dictName = "Youdao"; break;
                    case 1: dictName = "Bing"; break;
                    case 2: dictName = "Jinshan"; break;
                    default:
                }

                Statement stmt = DB_Conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM dicts WHERE dictName = \'" + dictName + "\'");
                while(rs.next()) {
                    score = rs.getInt("dictScore");
                }
                stmt.close();
                return score;
            }
            catch (SQLException ex) {
                System.out.println("Dict Score");
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
                return -1;
            }
        }
        System.out.println("Connection with database not established");
        return -1;
    }

    // =========================================
    // Get real time online user list
    // Return : all the online users
    public ArrayList<String> onlineUser() {
        if(connDone) {
            try {
                Statement stmt = DB_Conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE stat = 1");
                ArrayList<String> users = new ArrayList<>();

                while(rs.next()) {
                    users.add(rs.getString("userName"));
                }

                stmt.close();
                return users;
            }
            catch (SQLException ex) {
                System.out.println("Onlien Users");
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
                return null;
            }
        }
        System.out.println("Connection with database not established");
        return null;
    }

    // =========================================
    // Set stat in database of tbe corresponding user to 0(offline)
    // 1 for database data modified
    // -1 for exceptions
    public int logOut(String userName) {
        if(connDone) {
            try {
                Statement stmt = DB_Conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE userName =\'" + userName + "\'");

                while(rs.next()) {
                    stmt.executeUpdate("UPDATE users SET stat = 0 WHERE userName ='" + userName + "\' ");
                    break;
                }

                stmt.close();
                DB_Conn.close();
                connDone = false;
                return 1;
            }
            catch(SQLException ex) {
                System.out.println("Logout");
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
                return -1;
            }
        }
        System.out.println("Connection with database not established");
        return -1;
    }
}
