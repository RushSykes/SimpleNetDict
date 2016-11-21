package Dict.Connector;

import java.sql.*;

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
        DB_Url = "jdbc:mysql://172.26.7.66:3306/dictdatabase";
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
    // Deal with user data in db
    // Return :
    // -1 for exception
    // 0 for success
    // 1 for wrong password
    // 2 for username does not exist
    public int findUser_Login(String userName, String password) {
        // Make sure the connection is established
        if(connDone) {
            try {
                Statement stmt = DB_Conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE userName =\'" + userName + "\' ");
                while(rs.next()) {
                    if(rs.getString("password").equals(password)) {
                        return 0;
                    }
                    else
                        return 1;
                }
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
        System.out.println("Connection not established");
        return -1;
    }

    // =========================================
    // Deal with user data in db
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
                        return 0;
                    }
                    else
                        return 1;
                }
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
        System.out.println("Connection not established");
        return -1;
    }
}
