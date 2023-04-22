import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    static final String database = "";
    static final String netID = "";
    static final String hostName = "";
    static final String databaseURL = "";
    static final String password = "";
    
    private Connection connection = null;
    
    public void main(String[] args) {
        if (!TryConnection()) {
            System.out.println("Connection was unsuccessful.");
            return;
        }
        else System.out.println("Connection was successful.");
    }
    
    public boolean TryConnection() {
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
    
    public ResultSet executeQuery(String query) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        }
        catch (Exception e) {
            System.out.println("Something went wrong: " + e.getMessage());
            return null;
        }
    }
    
    public ResultSet executeStoredProcedure(String storedProcedure) {
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
