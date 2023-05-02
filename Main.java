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
    static Scanner inputStream = null;
    
    private static Connection connection = null;
    
    // Finished
    public static void main(String[] args) {
        if (!tryConnection()) {
            System.out.println("Connection was unsuccessful.");
            return;
        }
        else System.out.println("Connection was successful. Welcome to TransportUS.");
        try {
        	inputStream = new Scanner(System.in);
        }
        catch (Exception e) {
        	System.out.println("Failed to create an input stream.");
        	return;
        }
        while (!mainMenu()) {
        	// Loop until return is called
        }
        inputStream.close();
    }
    
    // Finished
    public static boolean tryConnection() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection(databaseURL, netID, password);
            return true;
        }
        catch (Exception e) {
        	System.out.printf("Something went wrong: %s\n", e.getMessage());
            return false;
        }
    }
    
    // Finished
    public static boolean isInteger(String string) {
    	try {
    		Integer.parseInt(string);
    	}
    	catch (Exception e) {
    		return false;
    	}
    	return true;
    }
    
    // Finished
    public static boolean isFloat(String string) {
    	try {
    		Float.parseFloat(string);
    	}
    	catch (Exception e) {
    		return false;
    	}
    	return true;
    }
    
    // Finished
    public static void consoleHeader(String header, String subheader) {
    	System.out.print("\033[H\033[2J");  
        System.out.flush();
        System.out.println(header);
        if (subheader != null) {
        	System.out.println(subheader);
        }
        System.out.println();
    }
    
    // Finished
    public static void endMenu() {
    	System.out.print("\nPress enter to continue...");
    	inputStream.nextLine();
    }
    
    // Finished
    public static boolean ynPrompt(String prompt) {
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
    		System.out.printf("Something went wrong: %s\n", e.getMessage());
    		return false;
    	}
    }
    
    // Finished
    public static String[] primaryPrompts(boolean average) {
    	String[] prompts = null;
    	String stateAbbrev = null;
    	if (average) {
    		prompts = new String[] {
    	        "Enter a state, 'average' for the average, or 'list' for a list of states: ",
    	        "Enter a town or county in %s, 'average' for the average, or 'list' for a list of municipalities in %s: ",
    	        "Enter a number of adults (at least 1) or 'average' for the average: ",
    	        "Enter a number of children or 'average' for the average: ",
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
    				if (i == 1 && answers[0].equalsIgnoreCase("average")) {
    					break;
    				}
    				else if (i == 1 && !answers[0].equalsIgnoreCase("average")) {
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
    					displayQuery("List of municipalities in " + answers[0] + ":", executeQuery("select countyOrTownName from countyortown where stateAbbrev = \"" + answers[0] + "\""), DisplayType.LIST);
    				}
    				else {
    					// State
    					if (i == 0) {
    						if (!answers[0].equalsIgnoreCase("average")) {
    							stateAbbrev = translateToAbbrev(answers[0]);
    							if (stateAbbrev != null) {
    								answers[0] = stateAbbrev;
    								break;
    							}
    							else System.out.println("No state using that data was found.");
    						}
    						else break;
    					}
    					// Municipality (get this implemented)
    					else if (i == 1) {
    						if (answers[1].equalsIgnoreCase("average") || answers[i].equalsIgnoreCase("average")) {
    							break;
    						}
    						else {
    							if (checkForData("select countyOrTownName from countyortown where stateAbbrev = \"" + answers[0] + "\" and countyOrTownName = \"" + answers[1] + "\"")) {
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
    						if (!isInteger(answers[4])) {
    							answers[4] = "2021";
    							break;
    						}
    						else if (!checkForData("select dataYear from inflation where dataYear = " + answers[4])) {
    							System.out.println("WARNING: This year does not contain any inflation rate.");
    							break;
    						}
    						else break;
    					}
    				}
    			}
    		}
    		return answers;
    	}
    	catch (Exception e) {
    		System.out.printf("Something went wrong: %s\n", e.getMessage());
    		return null;
    	}
    }
    
    // Finished
    public static boolean mainMenu() {
    	consoleHeader("--- Main Menu ---", "What would you like to do?");
    	while (true) {
    		System.out.println("'quickies': a list of useful stored procedures.");
    		System.out.println("'query': make queries on the database.");
        	System.out.println("'add': add data on the database.");
        	System.out.println("'edit': edit data on the database.");
        	System.out.println("'delete': delete data from the database.");
        	System.out.print("'quit': quit the program.\n\n");
        	System.out.print("Action: ");
        	try {
        		String input = inputStream.nextLine().toLowerCase();
    			switch (input) {
    			case "quickies":
    				return false;
    			case "query":
    				queryMenu();
    				return false;
    			case "add":
    				addMenu();
    				return false;
    			case "edit":
    				editMenu();
    				return false;
    			case "delete":
    				deleteMenu();
    				return false;
    			case "quit":
    				return true;
    			default:
    				System.out.println("Not a recognized command.");
    				break;
    			}
        	}
        	catch (Exception e) {
        		System.out.printf("Something went wrong: %s\n", e.getMessage());
        	}
    	}
    }
    
    // Finished
    public static void queryMenu() {
    	consoleHeader("--- Query Menu ---", "What would you like to do?");
    	String input = "";
    	while (true) {
    		System.out.println("'transportation': calculate transportation costs.");
        	System.out.println("'state': get a state's details.");
        	System.out.println("'municipality': get all the municipalities in a state.");
        	System.out.println("'inflation': get inflation rates between a year and 2023.");
        	System.out.print("'return': return to the previous menu.\n\n");
    		System.out.print("Action: ");
    		try {
    				input = inputStream.nextLine().toLowerCase();
    				switch (input) {
    				case "transportation":
    					calculateTransport();
    					consoleHeader("--- Query Menu ---", "What would you like to do?");
    					break;
    				case "state":
    					stateDetails();
    					consoleHeader("--- Query Menu ---", "What would you like to do?");
    					break;
    				case "municipality":
    					municipalityDetails();
    					consoleHeader("--- Query Menu ---", "What would you like to do?");
    					break;
    				case "inflation":
    					getInflation();
    					consoleHeader("--- Query Menu ---", "What would you like to do?");
    					break;
    				case "return":
    					return;
    				default:
    					System.out.println("Not a recognized command.");
    					break;
    			}
    		}
    		catch (Exception e) {
    			System.out.printf("Something went wrong: %s\n", e.getMessage());
        		return;
    		}
    	}
    }
    
    // Finished
    public static void calculateTransport() {
    	consoleHeader("--- Calculate Transportation ---", "Type 'return' at any time to return to the previous menu.");
    	String[] answers = primaryPrompts(true);
    	if (answers != null) {
    		String query = processInput(answers);
        	if (!displayQuery("The average annual cost for transportation for this criteria is: ", executeQuery(query), DisplayType.MONEY)) {
        		System.out.println("No data with that criteria was found.");
        	}
    	}
    	endMenu();
    }
    
    // Finished
    public static void stateDetails() {
    	consoleHeader("--- Get State Details ---", null);
    	try {
    		while (true) {
    			System.out.print("Enter a state, 'list', or 'return': ");
    			String input = inputStream.nextLine();
        		if (input.equalsIgnoreCase("return")) {
        			break;
        		}
        		else if (input.equalsIgnoreCase("list")) {
        			displayQuery(null, executeQuery("select * from state order by stateName"), DisplayType.TABLE);
        		}
        		else displayQuery(null, executeQuery(String.format("select * from state where stateName=\"%s\" order by stateName", input)), DisplayType.TABLE);
    		}
    	}
    	catch (Exception e) {
    		System.out.printf("Something went wrong: %s\n", e.getMessage());
    		endMenu();
    	}
    	endMenu();
    }
    
    // Finished
    public static void municipalityDetails() {
    	consoleHeader("--- Get Municipality Details ---", null);
    	System.out.print("Enter a state, 'list', or 'return': ");
    	try {
    		String input = inputStream.nextLine();
    		if (input.equalsIgnoreCase("return")) {
    			
    		}
    		else if (input.equalsIgnoreCase("list")) {
    			String query = "select * from countyortown order by stateAbbrev, countyOrTownName";
    			displayQuery("All municipalities:", executeQuery(query), DisplayType.TABLE);
    		}
    		else {
    			String query = String.format("select c.* from countyortown c, state s where c.stateAbbrev = s.stateAbbrev and s.stateName=\"%s\" order by countyOrTownName", input);
    			if (!displayQuery("Municipalities in " + input + ":", executeQuery(query), DisplayType.TABLE)) {
    				System.out.println("No data exists for that state.");
    			}
    		}
    	}
    	catch (Exception e) {
    		System.out.printf("Something went wrong: %s\n", e.getMessage());
    	}
    	endMenu();
    }
    
    // Finished
    public static void getInflation() {
    	consoleHeader("--- Get Inflation ---", null);
    	while (true) {
    		System.out.print("Enter a year or 'return': ");
    		try {
    			String input = inputStream.nextLine();
    			if (input.equalsIgnoreCase("return")) {
    				break;
    			}
    			int intInput = Integer.parseInt(input);
    			String query = String.format("select rate from inflation where dataYear = %d order by dataYear", intInput);
    			if (!displayQuery("The inflation rate for " + intInput + " is: ", executeQuery(query), DisplayType.SINGLE)) {
    				System.out.println("There is no inflation data for that year.");
    			}
    		}
    		catch (NumberFormatException e) {
    			System.out.println("Input is not a valid year.");
    		}
    		catch (Exception e) {
    			System.out.printf("Something went wrong: %s\n", e.getMessage());
    			break;
    		}
    	}
    	endMenu();
    }
    
    // Finished
    public static void addMenu() {
    	consoleHeader("--- Add Menu ---", "What would you like to do?");
    	while (true) {
    		System.out.println("'transportation': add an entry in the transportation table.");
        	System.out.println("'inflation': add a year and its inflation rate.");
        	System.out.print("'return': return to the previous menu.\n\n");
        	System.out.print("Action: ");
        	try {
        		String input = inputStream.nextLine().toLowerCase();
    			switch (input) {
    				case "transportation":
    					addPrimaryEntry();
    					consoleHeader("--- Add Menu ---", "What would you like to do?");
    					break;
    				case "inflation":
    					addInflationEntry();
    					consoleHeader("--- Add Menu ---", "What would you like to do?");
    					break;
    				case "return":
    					return;
    				default:
    					System.out.println("Not a recognized command.");
    					break;
    			}
        	}
        	catch (Exception e) {
        		System.out.printf("Something went wrong: %s\n", e.getMessage());
        	}
    	}
    }
    
    // Not tested
    public static void addPrimaryEntry() {
    	consoleHeader("--- Add Transportation Entry ---", "Type 'return' at any time to return to the previous menu.");
    	String[] addition = new String[6];
    	String[] answers = primaryPrompts(false);
    	for (short i = 0; i < answers.length; i++) {
    		addition[i] = answers[i];
    	}
    	addition[0] = String.format("\"%s\"", addition[0]);
    	addition[1] = String.format("\"%s\"", addition[1]);
    	boolean replacing = false;
    	if (answers != null) {
    		String query = processInput(answers);
    		if (!checkForData(query)) {
    			if (!ynPrompt("An entry with this criteria already exists. Replace it?")) {
    				endMenu();
    				return;
    			}
    			replacing = true;
    		}
    	}
    	while (true) {
    		System.out.print("Enter the annual cost of transportation for the given criteria: ");
    		String input = inputStream.nextLine();
    		if (isFloat(input)) {
    			addition[5] = input;
    			break;
    		}
    		else System.out.println("Input must be a decimal lower than 100000.");
    	}
    	if (!replacing) {
    		executeStoredProcedure("addTransportationEntry", addition);
    	}
    	else executeStoredProcedure("editTransportationEntry", addition);
    	System.out.println("Done!");
    	endMenu();
    }
    
    // Tested
    public static void addInflationEntry() {
    	consoleHeader("--- Add Inflation ---", null);
    	while (true) {
    		System.out.print("Enter a year or 'return': ");
    		try {
    			String input = inputStream.nextLine();
    			if (input.equalsIgnoreCase("return")) {
    				break;
    			}
    			int intInput = Integer.parseInt(input);
    			String query = String.format("select rate from inflation where dataYear = %d", intInput);
    			if (checkForData(query)) {
    				if (!ynPrompt("An inflation rate for that year already exists. Replace it?")) {
    					endMenu();
						return;
    				}
    			}
    			System.out.printf("Enter the inflation rate between now and %d: ", intInput);
    			input = inputStream.nextLine();
    			float floatInput = Float.parseFloat(input);
    			executeStoredProcedure("addInflationEntry", new String[] {String.format("%s", intInput), String.format("%.2f", floatInput)});
    			System.out.println("Done!");
    			break;
    		}
    		catch (NumberFormatException e) {
    			System.out.println("Numeric input is not valid.");
    		}
    		catch (Exception e) {
    			System.out.printf("Something went wrong: %s\n", e.getMessage());
    			break;
    		}
    	}
    	endMenu();
    }
    
    // Finished
    public static void editMenu() {
    	consoleHeader("--- Edit Menu ---", "What would you like to do?");
    	while (true) {
    		System.out.println("'transportation': edit an entry in the transportation table.");
        	System.out.println("'inflation': edit a year and its inflation rate.");
        	System.out.print("'return': return to the previous menu.\n\n");
        	System.out.print("Action: ");
        	try {
        		String input = inputStream.nextLine().toLowerCase();
    			switch (input) {
    				case "transportation":
    					editPrimaryEntry();
    					consoleHeader("--- Edit Menu ---", "What would you like to do?");
    					break;
    				case "inflation":
    					editInflationEntry();
    					consoleHeader("--- Edit Menu ---", "What would you like to do?");
    					break;
    				case "return":
    					return;
    				default:
    					System.out.println("Not a recognized command.");
    					break;
    			}
        	}
        	catch (Exception e) {
        		System.out.printf("Something went wrong: %s\n", e.getMessage());
        	}
    	}
    }
    
    // Not tested
    public static void editPrimaryEntry() {
    	consoleHeader("--- Edit Transportation Entry ---", "Type 'return' at any time to return to the previous menu.");
    	String[] addition = new String[6];
    	String[] answers = primaryPrompts(false);
    	for (short i = 0; i < answers.length; i++) {
    		addition[i] = answers[i];
    	}
    	addition[0] = String.format("\"%s\"", addition[0]);
    	addition[1] = String.format("\"%s\"", addition[1]);
    	boolean replacing = false;
    	if (answers != null) {
    		String query = processInput(answers);
    		if (!checkForData(query)) {
    			if (!ynPrompt("An entry with this criteria doesn't exist. Add it?")) {
    				endMenu();
    				return;
    			}
    			replacing = true;
    		}
    	}
    	while (true) {
    		System.out.print("Enter the annual cost of transportation for the given criteria: ");
    		String input = inputStream.nextLine();
    		if (isFloat(input)) {
    			addition[5] = input;
    			break;
    		}
    		else System.out.println("Input must be a decimal lower than 100000.");
    	}
    	if (replacing) {
    		executeStoredProcedure("addTransportationEntry", addition);
    	}
    	else executeStoredProcedure("editTransportationEntry", addition);
    	System.out.println("Done!");
    	endMenu();
    }
    
    // Implementation needed
    public static void editInflationEntry() {
    	consoleHeader("--- Edit Inflation ---", null);
    	while (true) {
    		System.out.print("Enter a year or 'return': ");
    		try {
    			String input = inputStream.nextLine();
    			if (input.equalsIgnoreCase("return")) {
    				break;
    			}
    			int intInput = Integer.parseInt(input);
    			String query = String.format("select rate from inflation where dataYear = %d", intInput);
    			if (!checkForData(query)) {
    				if (ynPrompt("There is no data for that year. Add it?")) {
    					endMenu();
    					return;
    				}
    			}
    			System.out.printf("Enter the inflation rate between now and %d: ", intInput);
    			input = inputStream.nextLine();
    			float floatInput = Float.parseFloat(input);
    			executeStoredProcedure("editInflationEntry", new String[] {String.format("%s", intInput), String.format("%.2f", floatInput)});
    			System.out.println("Done!");
    			break;
    		}
    		catch (NumberFormatException e) {
    			System.out.println("Numeric input is not valid.");
    		}
    		catch (Exception e) {
    			System.out.printf("Something went wrong: %s\n", e.getMessage());
    			break;
    		}
    	}
    	endMenu();
    }
    
    public static void deleteMenu() {
    	consoleHeader("--- Delete Menu ---", "What would you like to do?");
    	while (true) {
    		System.out.println("'transportation': delete an entry in the transportation table.");
        	System.out.println("'inflation': delete a year and its inflation rate.");
        	System.out.print("'return': return to the previous menu.\n\n");
        	System.out.print("Action: ");
        	try {
        		String input = inputStream.nextLine().toLowerCase();
        		switch (input) {
				case "transportation":
					deletePrimaryEntry();
					consoleHeader("--- Delete Menu ---", "What would you like to do?");
					break;
				case "inflation":
					deleteInflationEntry();
					consoleHeader("--- Delete Menu ---", "What would you like to do?");
					break;
				case "return":
					return;
				default:
					System.out.println("Not a recognized command.");
					break;
        		}
        	}
        	catch (Exception e) {
        		System.out.printf("Something went wrong: %s\n", e.getMessage());
        	}
    	}
    }
    
    public static void deletePrimaryEntry() {
    	consoleHeader("--- Delete Transportation Entry ---", "Type 'return' at any time to return to the previous menu.");
    	String[] deletion = new String[6];
    	String[] answers = primaryPrompts(false);
    	for (short i = 0; i < answers.length; i++) {
    		deletion[i] = answers[i];
    	}
    	deletion[0] = String.format("\"%s\"", deletion[0]);
    	deletion[1] = String.format("\"%s\"", deletion[1]);
    	if (answers != null) {
    		String query = processInput(answers);
    		if (checkForData(query)) {
    			if (ynPrompt("Delete the entry with this criteria?")) {
    				executeStoredProcedure("deleteTransportationEntry", deletion);
    				System.out.println("Done!");
    			}
    		}
    		else System.out.println("An entry with that criteria doesn't exist.");
    	}
    	endMenu();
    }
    
    public static void deleteInflationEntry() {
    	consoleHeader("--- Delete Inflation ---", null);
    	while (true) {
    		System.out.print("Enter a year or 'return': ");
    		try {
    			String input = inputStream.nextLine();
    			if (input.equalsIgnoreCase("return")) {
    				break;
    			}
    			int intInput = Integer.parseInt(input);
    			String query = String.format("select rate from inflation where dataYear = %d", intInput);
    			if (checkForData(query)) {
    				if (ynPrompt("Delete inflation rate for " + intInput + "?")) {
    					executeStoredProcedure("deleteInflationEntry", new String[] {input});
    					System.out.println("Done!");
    					break;
    				}
    			}
    			else System.out.println("No data for that year was found.");
    		}
    		catch (NumberFormatException e) {
    			System.out.println("Numeric input is not valid.");
    		}
    		catch (Exception e) {
    			System.out.printf("Something went wrong: %s\n", e.getMessage());
    		}
    	}
    	endMenu();
    }
    
    // Finished
    public static String processInput(String[] answers) {
    	String selectClause = "select round(avg(t.cost) * (i.rate / 100 + 1), 2) ";
    	String fromClause = "from transportation t, inflation i ";
    	String whereClause = "where i.dataYear = t.dataYear";
    	if (!answers[0].equalsIgnoreCase("average")) {
    		whereClause = whereClause.concat(" and t.stateAbbrev = \"" + answers[0] + "\"");
    	}
    	if (!answers[1].equalsIgnoreCase("average")) {
    		whereClause = whereClause.concat(" and t.countyOrTownName = \"" + answers[1] + "\"");
    	}
    	if (!answers[2].equalsIgnoreCase("average")) {
    		whereClause = whereClause.concat(" and t.numAdults = " + answers[2]);
    	}
    	if (!answers[3].equalsIgnoreCase("average")) {
    		whereClause = whereClause.concat(" and t.numKids = " + answers[3]);
    	}
    	whereClause = whereClause.concat(" and t.dataYear = " + answers[4]);
    	String finalQuery = selectClause + fromClause + whereClause;
    	return finalQuery;
    }
    
    // Finished
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
    
    // Finished
    public static String translateToAbbrev(String original) {
    	if (isInteger(original)) {
    		ResultSet results = executeQuery("select stateAbbrev from state where stateFIPS = " + original);
    		try {
    			results.next();
    			return String.format("%s", results.getObject(1));
    		}
    		catch (Exception e) {
    			return null;
    		}
    	}
    	else if (original.length() == 2) {
    		return original.toUpperCase();
    	}
    	else {
    		ResultSet results = executeQuery("select stateAbbrev from state where stateName = \"" + original + "\"");
    		try {
    			results.next();
    			return String.format("%s", results.getObject(1));
    		}
    		catch (Exception e) {
    			return null;
    		}
    	}
    }
    
    // Finished
    public static ResultSet executeQuery(String query) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        }
        catch (SQLException e) {
        	return null;
        }
        catch (Exception e) {
        	System.out.printf("Something went wrong: %s\n", e.getMessage());
            return null;
        }
    }
    
    // Finished
    public static boolean displayQuery(String preface, ResultSet results, DisplayType displayMode) {
    	if (results == null) {
    		return false;
    	}
    	ResultSetMetaData meta = null;
    	try {
    		meta = results.getMetaData();
    		switch (displayMode) {
        	case SINGLE:
        		if (results.next()) {
        			System.out.println(preface + results.getObject(1));
        		}
        		else return false;
        		return true;
        	case MONEY:
        		if (results.next()) {
        			double money = Double.parseDouble(results.getObject(1).toString());
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
        			if (i == columns) {
        				System.out.println(meta.getColumnName(i));
        			}
        			else System.out.print(meta.getColumnName(i) + ", ");
        		}
        		while (results.next()) {
        			for (int i = 1; i <= columns; i++) {
        				if (i == columns) {
        					System.out.println(results.getObject(i));
        				}
        				else System.out.print(results.getObject(i) + ", ");
        			}
        		}
        		System.out.println();
        		return true;
        	default:
        		return false;
        	}
    	}
    	catch (Exception e) {
    		System.out.printf("Something went wrong: %s\n", e.getMessage());
    		return false;
    	}
    }
    
    // Finished
    public static ResultSet executeStoredProcedure(String storedProcedure, String[] args) {
    	if (args != null && args.length != 0) {
    		storedProcedure = storedProcedure.concat("(");
    		for (short i = 0; i < args.length; i++) {
        		if (i == args.length - 1) {
        			storedProcedure = storedProcedure.concat(args[i]);
        		}
        		else storedProcedure = storedProcedure.concat(args[i] + ", ");
        	}
    		storedProcedure = storedProcedure.concat(")");
    	}
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
        	System.out.printf("Something went wrong: %s\n", e.getMessage());
            return null;
        }
    }
}
