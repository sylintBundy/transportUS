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
	static enum DisplayType {
		NONE,
		SINGLE,
		MONEY,
		LIST,
		TABLE
	}
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
        else System.out.println("Connection was successful. Welcome to TransportUS.");
        while (!mainMenu()) {
        	// Loop until return is called
        }
    }
    
    public static boolean tryConnection() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection(databaseURL, netID, password);
            return true;
        }
        catch (Exception e) {
            System.out.printf("Something went wrong in tryConnection(): %s\n", e.getMessage());
            return false;
        }
    }
    
    public static boolean ynPrompt(String prompt, Scanner inputStream) {
    	try {
    		while (true) {
    			System.out.printf("%s (y/n):", prompt);
    			String input = inputStream.nextLine();
    			switch (input.toLowerCase()) {
    			case "y":
    				return true;
    			case "n":
    				return false;
    			default:
    				System.out.println("Not a recognized command.");
					break;
    			}
    		}
    	}
    	catch (Exception e) {
    		System.out.printf("Something went wrong in ynPrompt(): %s\n", e.getMessage());
    		return false;
    	}
    }
    
    public static boolean mainMenu() {
    	consoleHeader("--- Main Menu ---", "What would you like to do?");
    	Scanner inputStream = null;
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
    		System.out.printf("Something went wrong in mainMenu(): %s\n", e.getMessage());
    		return false;
    	}
    	finally {
    		if (inputStream != null) {
    			inputStream.close();
    		}
    	}
    }
    
    public static void queryMenu(Scanner inputStream) {
    	consoleHeader("--- Query Menu ---", "What would you like to do?");
    	System.out.println("'transportation': calculate transportation costs.");
    	System.out.println("'state': get a state's details.");
    	System.out.println("'municipality': get details about a town or county.");
    	System.out.println("'inflation': get inflation rates between a year and 2023.");
    	System.out.println("'return': return to the previous menu.");
    	String input = "";
    	try {
    		while (true) {
    			System.out.print("Action: ");
    			input = inputStream.nextLine().toLowerCase();
    			switch (input) {
    				case "transportation":
    					calculateTransport(inputStream);
    					break;
    				case "state":
    					stateDetails(inputStream);
    					break;
    				case "municipality":
    					municipalityDetails(inputStream);
    					break;
    				case "inflation":
    					getInflation(inputStream);
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
    		System.out.printf("Something went wrong in queryMenu(): %s", e.getMessage());
    	}
    }
    
    public static void addMenu(Scanner inputStream) {
    	consoleHeader("--- Add Menu ---", "What would you like to do?");
    	System.out.println("'transportation': add an entry in the transportation table.");
    	System.out.println("'inflation': add a year and its inflation rate.");
    	System.out.println("'return': return to the previous menu.");
    	try {
    		while (true) {
    			System.out.print("Action: ");
    			String input = inputStream.nextLine().toLowerCase();
    			switch (input) {
    				case "transportation":
    					addPrimaryEntry(inputStream);
    					consoleHeader("--- Add Menu ---", "What would you like to do?");
    					break;
    				case "inflation":
    					addInflationEntry(inputStream);
    					consoleHeader("--- Add Menu ---", "What would you like to do?");
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
    		System.out.printf("Something went wrong in addMenu(): %s", e.getMessage());
    	}
    }
    
    // All the big stuffs is in here. Finish the year.
    public static String[] primaryPrompts(Scanner inputStream, boolean average) {
    	String[] prompts = null;
    	if (average) {
    		prompts = new String[] {
    	        "Enter a state, 'average' for the average, or 'list' for a list of states: ",
    	        "Enter a town or county in %s, 'average' for the average, or 'list' for a list of municipalities in %s: ",
    	        "Enter a number of adults between 1 and 3 inclusively or 'average' for the average: ",
    	        "Enter a number of children between 0 and 4 or 'average' for the average: ",
    	        "Enter a year of data (2021 is default, but the database can be expanded.): "
    	    };
    	}
    	else {
    		prompts = new String[] {
        	    "Enter a state or 'list' for a list of states: ",
        	    "Enter a town or county in %s or 'list' for a list of municipalities in %s: ",
        	    "Enter a number of adults: ",
        	    "Enter a number of children: ",
        	    "Enter a year of data (2021 is default, but the database can be expanded.): "
        	};
    	}
    	String[] answers = new String[prompts.length];
    	try {
    		for (short i = 0; i < prompts.length; i++) {
    			while (true) {
    				if (i == 1) {
    					System.out.printf(prompts[i], answers[0], answers[0]);
    				}
    				else System.out.print(prompts[i]);
    				answers[i] = inputStream.nextLine();
    				if (answers[i].equalsIgnoreCase("return")) {
    					return null;
    				}
    				else if (answers[i].equalsIgnoreCase("list") && i == 0) {
    					displayQuery("List of states:", executeQuery("select stateName from state"), DisplayType.LIST);
    				}
    				else if (answers[i].equalsIgnoreCase("list") && i == 1) {
    					
    				}
    				else {
    					// State
    					if (i == 0) {
    						// FIPS
    						if (isInteger(answers[0])) {
    							ResultSet results = executeQuery("select stateAbbrev from state where stateFIPS = " + Integer.parseInt(answers[0]));
    							if (results != null && results.next()) {
    								answers[0] = results.getString(1);
    								break;
    							}
    							else System.out.println("No state using that data was found.");
    						}
    						// Abbreviation
    						else if (answers[0].length() == 2) {
    							answers[0] = answers[0].toUpperCase();
    							if (checkForData("select stateAbbrev from state where stateAbbrev = " + answers[0])) {
    								break;
    							}
    							else System.out.println("No state using that data was found.");
    						}
    						// Average value
    						else if (answers[0].equalsIgnoreCase("average") && average) {
    							break;
    						}
    						// Name
    						else {
    							if (checkForData("select stateAbbrev from state where stateName = " + answers[0])) {
    								break;
    							}
    							else System.out.println("No state using that data was found.");
    						}
    					}
    					// Municipality (get this implemented)
    					else if (i == 1) {
    						if (answers[0].equalsIgnoreCase("average")) {
    							break;
    						}
    						else {
    							if (checkForData("select countyOrTownName from countyortown where stateAbbrev = " + answers[0] + " and countyOrTownName = " + answers[1])) {
    								break;
    							}
    							else System.out.println("No municipality using that data was found.");
    						}
    					}
    					// Number of adults
    					else if (i == 2) {
    						if (answers[2].equalsIgnoreCase("average") && average) {
    							break;
    						}
    						else {
    							if (isInteger(answers[2])) {
    								break;
    							}
    							else System.out.println("Number of adults must be a whole number.");
    						}
    					}
    					// Number of children
    					else if (i == 3) {
    						if (answers[3].equalsIgnoreCase("average") && average) {
    							break;
    						}
    						else {
    							if (isInteger(answers[3])) {
    								break;
    							}
    							else System.out.println("Number of children must be a whole number.");
    						}
    					}
    					// Year
    					else if (i == 4) {
    						
    					}
    				}
    			}
    		}
    		return answers;
    	}
    	catch (Exception e) {
    		System.out.printf("Something went wrong in calculateTransport(): %s\n", e.getMessage());
    		return null;
    	}
    }
    
    // Resume work here
    // Needs implementation
    public static void addPrimaryEntry(Scanner inputStream) {
    	consoleHeader("--- Add Transportation Entry ---", "Type 'return' at any time to return to the previous menu.");
    	String[] answers = primaryPrompts(inputStream, false);
    	if (answers != null) {
    		String query = processInput(answers);
    		if (checkForData(query)) {
    			if (ynPrompt("An entry with this criteria already exists. Replace it?", inputStream)) {
    				
    			}
    		}
    	}
    }
    
    public static void addInflationEntry(Scanner inputStream) {
    	consoleHeader("--- Add Inflation ---", null);
    	while (true) {
    		System.out.print("Enter a year or 'return': ");
    		try {
    			String input = inputStream.nextLine();
    			if (input.equalsIgnoreCase("return")) {
    				break;
    			}
    			int intInput = Integer.parseInt(input);
    			String query = String.format("select rate from inflation where dataYear = %.2f;", intInput);
    			if (checkForData(query)) {
    				if (!ynPrompt("An inflation rate for that year already exists. Replace it?", inputStream)) {
    					endMenu(inputStream);
						return;
    				}
    			}
    			System.out.println("Enter the inflation rate between now and " + intInput + ": ");
    			input = inputStream.nextLine();
    			float floatInput = Float.parseFloat(input);
    			executeStoredProcedure("addInflationEntry", new String[] {String.format("%s", intInput), String.format("%.2f", floatInput)});
    		}
    		catch (NumberFormatException e) {
    			System.out.println("Numeric input is not valid.");
    		}
    		catch (Exception e) {
    			System.out.printf("Something went wrong in addInflation(): %s", e.getMessage());
    			break;
    		}
    	}
    	endMenu(inputStream);
    }
    
    public static void editMenu(Scanner inputStream) {
    	consoleHeader("--- Edit Menu ---", "What would you like to do?");
    	System.out.println("'transportation': edit an entry in the transportation table.");
    	System.out.println("'inflation': edit a year and its inflation rate.");
    	System.out.println("'return': return to the previous menu.");
    	try {
    		while (true) {
    			System.out.print("Action: ");
    			String input = inputStream.nextLine().toLowerCase();
    			switch (input) {
    				case "transportation":
    					editPrimaryEntry(inputStream);
    					consoleHeader("--- Edit Menu ---", "What would you like to do?");
    					break;
    				case "inflation":
    					editInflationEntry(inputStream);
    					consoleHeader("--- Edit Menu ---", "What would you like to do?");
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
    		System.out.printf("Something went wrong in addMenu(): %s", e.getMessage());
    	}
    }
    
    public static void editPrimaryEntry(Scanner inputStream) {
    	
    }
    
    public static boolean isInteger(String string) {
    	try {
    		Integer.parseInt(string);
    	}
    	catch (Exception e) {
    		return false;
    	}
    	return true;
    }
    
    // Needs implementation
    public static void editInflationEntry(Scanner inputStream) {
    	consoleHeader("--- Edit Inflation ---", null);
    	while (true) {
    		System.out.print("Enter a year or 'return': ");
    		try {
    			String input = inputStream.nextLine();
    			if (input.equalsIgnoreCase("return")) {
    				break;
    			}
    			int intInput = Integer.parseInt(input);
    			String query = String.format("select rate from inflation where dataYear = %.2f;", intInput);
    			if (!checkForData(query)) {
    				if (ynPrompt("There is no data for that year. Add it?", inputStream)) {
    					endMenu(inputStream);
    					return;
    				}
    			}
    			System.out.println("Enter the inflation rate between now and " + intInput + ": ");
    			input = inputStream.nextLine();
    			float floatInput = Float.parseFloat(input);
    			executeStoredProcedure("editInflationEntry", new String[] {String.format("%s", intInput), String.format("%.2f", floatInput)});
    		}
    		catch (NumberFormatException e) {
    			System.out.println("Numeric input is not valid.");
    		}
    		catch (Exception e) {
    			System.out.printf("Something went wrong in editInflation(): %s", e.getMessage());
    			break;
    		}
    	}
    }
    
    public static void calculateTransport(Scanner inputStream) {
    	consoleHeader("--- Calculate Transportation ---", "Type 'return' at any time to return to the previous menu.");
    	String[] answers = primaryPrompts(inputStream, true);
    	if (answers != null) {
    		String query = processInput(answers);
        	if (!displayQuery("The average annual cost for transportation for this criteria is: ", executeQuery(query), DisplayType.MONEY)) {
        		System.out.println("No data with that criteria was found.");
        	}
    	}
    	endMenu(inputStream);
    }
    
    public static String processInput(String[] answers) {
    	for (short i = 0; i < answers.length; i++) {
    		System.out.print(answers[i] + ",");
    	}
    	String selectClause = "select avg(t.cost) * (i.rate / 100 + 1) ";
    	String fromClause = "from transportation t, inflation i ";
    	String whereClause = "where t.dataYear = i.dataYear";
    	if (!answers[0].equalsIgnoreCase("average")) {
    		whereClause = whereClause.concat(" and t.stateAbbrev = " + answers[0]);
    	}
    	if (!answers[1].equalsIgnoreCase("average")) {
    		whereClause = whereClause.concat(" and t.countyOrTownName = " + answers[1]);
    	}
    	if (!answers[2].equalsIgnoreCase("average")) {
    		whereClause = whereClause.concat(" and t.numAdults = " + answers[2]);
    	}
    	if (!answers[3].equalsIgnoreCase("average")) {
    		whereClause = whereClause.concat(" and t.numKids = " + answers[3]);
    	}
    	whereClause = whereClause.concat(" and i.dataYear = " + answers[4]);
    	String finalQuery = selectClause + fromClause + whereClause + ";";
    	return finalQuery;
    }
    
    public static void stateDetails(Scanner inputStream) {
    	consoleHeader("--- Get State Details ---", null);
    	try {
    		while (true) {
    			System.out.print("Enter a state, 'list', or 'return': ");
    			String input = inputStream.nextLine();
        		if (input.equalsIgnoreCase("return")) {
        			break;
        		}
        		else if (input.equalsIgnoreCase("list")) {
        			displayQuery(null, executeQuery("select * from state"), DisplayType.TABLE);
        		}
        		else displayQuery(null, executeQuery(String.format("select * from state where stateName=\"%s\"", input)), DisplayType.TABLE);
    		}
    	}
    	catch (Exception e) {
    		System.out.printf("Something went wrong in addToQuery(): %s", e.getMessage());
    		endMenu(inputStream);
    	}
    }
    
    public static void municipalityDetails(Scanner inputStream) {
    	consoleHeader("--- Get Municipality Details ---", null);
    	System.out.print("Enter a state, 'list', or 'return': ");
    	try {
    		String input = inputStream.nextLine();
    		if (input.equalsIgnoreCase("return")) {
    			return;
    		}
    		else if (input.equalsIgnoreCase("list")) {
    			String query = "select * from countyortown";
    			displayQuery(null, executeQuery(query), DisplayType.TABLE);
    		}
    		else {
    			String query = String.format("select c.* from countyortown c, state s where c.stateAbbrev = s.stateAbbrev and s.stateName=\"%s\"", input);
    			if (!displayQuery(null, executeQuery(query), DisplayType.TABLE)) {
    				System.out.println("No data exists for that state.");
    			}
    		}
    	}
    	catch (Exception e) {
    		System.out.printf("Something went wrong in municipalityDetails(): %s", e.getMessage());
    	}
    	endMenu(inputStream);
    }
    
    public static void getInflation(Scanner inputStream) {
    	consoleHeader("--- Get Inflation ---", null);
    	while (true) {
    		System.out.print("Enter a year or 'return': ");
    		try {
    			String input = inputStream.nextLine();
    			if (input.equalsIgnoreCase("return")) {
    				break;
    			}
    			int intInput = Integer.parseInt(input);
    			String query = String.format("select rate from inflation where dataYear = %.2f;", intInput);
    			if (!displayQuery("The inflation rate for " + intInput + " is: ", executeQuery(query), DisplayType.SINGLE)) {
    				System.out.println("There is no inflation data for that year.");
    			}
    		}
    		catch (NumberFormatException e) {
    			System.out.println("Input is not a valid year.");
    		}
    		catch (Exception e) {
    			System.out.printf("Something went wrong in getInflation(): %s", e.getMessage());
    			break;
    		}
    	}
    	endMenu(inputStream);
    }
    
    public static void consoleHeader(String header, String subheader) {
    	System.out.print("\033[H\033[2J");  
        System.out.flush();
        System.out.println(header);
        if (subheader != null) {
        	System.out.println(subheader);
        }
        System.out.println();
    }
    
    public static boolean checkForData(String query) {
    	try {
    		query = query.concat(";");
    		ResultSet results = executeQuery(query);
    		if (results != null && results.next()) {
    			return true;
    		}
    		return false;
    	}
    	catch (Exception e) {
    		System.out.printf("Something went wrong: %s\n", e.getMessage());
    		return false;
    	}
    }
    
    public static ResultSet executeQuery(String query) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        }
        catch (Exception e) {
            System.out.println("Something went wrong in executeQuery(): " + e.getMessage());
            return null;
        }
    }
    
    public static boolean displayQuery(String preface, ResultSet results, DisplayType displayMode) {
    	ResultSetMetaData meta = null;
    	try {
    		meta = results.getMetaData();
    		switch (displayMode) {
        	case NONE:
        		return false;
        	case SINGLE:
        		if (results.next()) {
        			System.out.println(preface + results.getObject(1));
        		}
        		else return false;
        		return true;
        	case MONEY:
        		if (results.next()) {
        			double money = Math.round(Double.parseDouble(results.getObject(1).toString()) * 100.0) / 100.0;
        			System.out.println(preface + "$" + money);
        		}
        		else return false;
        		return true;
        	case LIST:
        		if (preface != null) {
        			System.out.println(preface);
        		}
        		while (results.next()) {
        			System.out.println(results.getObject(1));
        		}
        		return true;
        	case TABLE:
        		if (preface != null) {
        			System.out.println(preface);
        		}
        		int columns = meta.getColumnCount();
        		for (int i = 1; i <= columns; i++) {
        			System.out.print(meta.getColumnName(i) + ",");
        		}
        		System.out.println();
        		while (results.next()) {
        			for (int i = 1; i <= columns; i++) {
        				System.out.print(results.getObject(i) + ",");
        			}
        			System.out.println();
        		}
        		return true;
        	default:
        		return false;
        	}
    	}
    	catch (Exception e) {
    		System.out.printf("Something went wrong in displayQuery(): %s", e.getMessage());
    		return false;
    	}
    }
    
    public static ResultSet executeStoredProcedure(String storedProcedure, String[] args) {
        try {
            connection.createStatement();
            CallableStatement callStatement = connection.prepareCall("{call " + storedProcedure + "}");
            if (callStatement.execute()) {
                return callStatement.getResultSet();
            }
            else return null;
        }
        catch (SQLException e) {
        	System.out.println("The stored procedure failed. Did you give invalid arguments?");
        	return null;
        }
        catch (Exception e) {
            System.out.println("Something went wrong in executeStoredProcedure(): " + e.getMessage());
            return null;
        }
    }

    public static void endMenu(Scanner inputStream) {
    	System.out.print("\nPress enter to continue...");
    	inputStream.nextLine();
    }
}
