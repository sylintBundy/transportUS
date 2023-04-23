package source;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.CallableStatement;
import java.sql.Statement;
import java.util.Scanner;

public class Program {
	static final String database = "cs366-2231_bundyjg07";
    static final String netID = "bundyjg07";
    static final String hostName = "washington.uww.edu";
    static final String databaseURL = "jdbc:mariadb://"+hostName+"/"+database;
    static final String password = "jb9438";
    
    private static Connection connection = null;
    
    public static void main(String[] args) {
        if (!TryConnection()) {
            System.out.println("Connection was unsuccessful.");
            return;
        }
        else System.out.println("Connection was successful.");
    }
    
    public static boolean TryConnection() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            System.out.println("databaseURL" + databaseURL);
            connection = DriverManager.getConnection(databaseURL, netID, password);
            return true;
        }
        catch (Exception e) {
            System.out.println("Something went wrong: " + e.getMessage());
            return false;
        }
    }
    
    public static ResultSet executeQuery(String query) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        }
        catch (Exception e) {
            System.out.println("Something went wrong: " + e.getMessage());
            return null;
        }
    }
    
    public static ResultSet executeStoredProcedure(String storedProcedure) {
        try {
            Statement statement = connection.createStatement();
            CallableStatement callStatement = connection.prepareCall("{call " + storedProcedure + "}");
            if (callStatement.execute()) {
                return callStatement.getResultSet();
            }
            else return null;
        }
        catch (Exception e) {
            System.out.println("Something went wrong: " + e.getMessage());
            return null;
        }
    }
}
