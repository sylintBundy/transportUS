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
        if (!tryConnection()) {
            System.out.println("Connection was unsuccessful.");
            return;
        }
        else System.out.println("Connection was successful. Welcome to TranportUS");
        while (!mainMenu()) {
        	// Loop until new
        }
    }
    
    public static boolean mainMenu() {
    	clearConsole();
    	Scanner inputStream = null;
    	System.out.println("--- Main Menu ---");
    	System.out.print("What would you like to do?\n\n");
    	System.out.println("'query': make queries on the database.");
    	System.out.println("'add': add data on the database.");
    	System.out.println("'edit': edit data on the database.");
    	System.out.print("'quit': quit the program.\n\n");
    	try {
    		inputStream = new Scanner(System.in);
    		while (true) {
    			System.out.print("Action: ");
    			String input = inputStream.nextLine().toLowerCase();
    			switch (input) {
    			case "query":
    				queryMenu(inputStream);
    				break;
    			case "add":
    				addMenu(inputStream);
    				break;
    			case "edit":
    				editMenu(inputStream);
    				break;
    			case "quit":
    				return true;
    			default:
    				System.out.println("Not a recognized command.");
    				break;
    			}
    		}
    	}
    	catch (Exception e) {
    		System.out.printf("Something went wrong: \s", e.getMessage());
    		return false;
    	}
    	finally {
    		if (inputStream != null) {
    			inputStream.close();
    		}
    	}
    }
    
    public static void queryMenu(Scanner inputStream) {
    	clearConsole();
    	System.out.println("--- Query Menu ---");
    	System.out.print("What would you like to do?\n\n");
    	System.out.println("'transportation': calculate transportation costs.");
    	System.out.println("'state': get a state's details.");
    	System.out.println("'municipality': get details about a town or county.");
    	System.out.println("'inflation': get inflation rates between a year and 2023.");
    	System.out.println("'return': return to the previous menu.");
    	try {
    		while (true) {
    			System.out.print("Action: ");
    			String input = inputStream.nextLine().toLowerCase();
    			switch (input) {
    				case "transportation":
    					// Method
    					break;
    				case "state":
    					// Method
    					break;
    				case "municipality":
    					// Method
    					break;
    				case "inflation":
    					// Method
    					break;
    				case "return":
    					return;
    				default:
    					System.out.println("Not a recognized command.");
    					break;
    			}
    		}
    	}
    	catch (Exception e) {
    		System.out.printf("Something went wrong: \s", e.getMessage());
    	}
    }
    
    public static void addMenu(Scanner inputStream) {
    	clearConsole();
    	System.out.println("--- Add Menu ---");
    }
    
    public static void editMenu(Scanner inputStream) {
    	clearConsole();
    	System.out.println("--- Edit Menu ---");
    }
    
    public static void clearConsole() {
    	System.out.print("\033[H\033[2J");  
        System.out.flush();
    }
    
    public static boolean tryConnection() {
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
