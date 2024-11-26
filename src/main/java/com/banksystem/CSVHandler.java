package com.banksystem;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Handles all CSV file operations for the banking system.
 * This class is responsible for:
 * - Reading customer data from CSV files
 * - Writing updated customer data back to CSV files
 * - Handling dynamic column positions in CSV files
 * - Maintaining data integrity during file operations
 * 
 * @author Daniel Fuentes, Rogelio Lozano
 * @version 2.0
 */
public class CSVHandler {
    /** The file path for original bank users data */
    private static final String ORIGINAL_CSV_FILE = "CS 3331 - Bank Users.csv";
    /** The file path for updated bank users data */
    private static final String UPDATED_CSV_FILE = "Updated_Bank_Users.csv";
    
    /** Maps column names to their positions in the CSV */
    private Map<String, Integer> columnMap;

    /**
     * Constructor initializes the CSV handler and prepares for file operations.
     * Creates an empty column mapping that will be populated when reading the file.
     */
    public CSVHandler() {
        this.columnMap = new HashMap<>();
    }

    /**
     * Reads the CSV header row and creates a mapping of column names to their positions.
     * This allows the system to handle CSVs where columns appear in different orders.
     * 
     * For example, if "First Name" is in column 3 instead of column 1, the map will
     * store {"First Name" -> 3} and all other reads will look for position 3
     * for first names.
     * 
     * @throws IOException if there's an error reading the file
     */
    private void initializeColumnMap() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(ORIGINAL_CSV_FILE))) {
            //read and parse the header line
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IOException("CSV file is empty - no header row found");
            }

            //split the header into individual column names and map each to its position
            String[] headers = headerLine.split(",");
            for (int i = 0; i < headers.length; i++) {
                //store each column name with its position in the CSV
                columnMap.put(headers[i].trim(), i);
            }
        }
    }

    /**
     * Loads all customer data from the CSV file into memory.
     * This method:
     * 1. Initializes column mapping from the header row
     * 2. Reads each line of customer data
     * 3. Creates customer objects with appropriate account information
     * 4. Handles any errors during the loading process
     * 
     * @return Map of customer names to Customer objects containing all their data
     */
    public Map<String, Customer> loadCustomerData() {
        Map<String, Customer> customers = new HashMap<>();
        
        try {
            //read the header and set up column mapping
            initializeColumnMap();
            
            //read the actual customer data
            try (BufferedReader reader = new BufferedReader(new FileReader(ORIGINAL_CSV_FILE))) {
                //skip the header line since already processed it
                reader.readLine();
                
                String line;
                //process each line of customer data
                while ((line = reader.readLine()) != null) {
                    try {
                        //split the line, preserving commas within quoted fields
                        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                        
                        //clean each field by removing quotes and extra spaces
                        for (int i = 0; i < parts.length; i++) {
                            parts[i] = parts[i].replace("\"", "").trim();
                        }
                        
                        //extract customer info using column positions from the map
                        String id = parts[columnMap.get("Identification Number")];
                        String firstName = parts[columnMap.get("First Name")];
                        String lastName = parts[columnMap.get("Last Name")];
                        String fullName = firstName + " " + lastName;
                        
                        //create a new customer object
                        Customer customer = new Customer(fullName, id);
                        
                        //create and set up their accounts
                        List<Account> accounts = new ArrayList<>();
                        
                        //create Checking account
                        accounts.add(new Checkings(
                            parts[columnMap.get("Checking Account Number")],
                            Double.parseDouble(parts[columnMap.get("Checking Starting Balance")])
                        ));
                        
                        //create Savings account
                        accounts.add(new Savings(
                            parts[columnMap.get("Savings Account Number")],
                            Double.parseDouble(parts[columnMap.get("Savings Starting Balance")])
                        ));
                        
                        //create Credit account
                        accounts.add(new Credit(
                            parts[columnMap.get("Credit Account Number")],
                            Double.parseDouble(parts[columnMap.get("Credit Starting Balance")]),
                            Double.parseDouble(parts[columnMap.get("Credit Max")])
                        ));
                        
                        //associate accounts with customer and add to customers map
                        customer.setAccounts(accounts);
                        customers.put(fullName, customer);
                        
                    } catch (Exception e) {
                        //log any errors but continue processing other customers
                        System.out.println("Error processing customer data line: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            
        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
            e.printStackTrace();
        }
        
        return customers;
    }

    /**
     * Saves all customer data to a new CSV file.
     * This method:
     * 1. Creates a new CSV file for updated data
     * 2. Preserves the original CSV structure and format
     * 3. Updates account balances with current values
     * 4. Does not modify the original input file
     * 
     * @param customers Map of customer names to Customer objects to save
     */
    public void saveCustomerData(Map<String, Customer> customers) {
        try {
            //store all lines including header for rewriting
            List<String> lines = new ArrayList<>();
            Set<String> processedCustomers = new HashSet<>();
            
            //read the original file to preserve structure and update existing customers
            try (BufferedReader reader = new BufferedReader(new FileReader(ORIGINAL_CSV_FILE))) {
                //keep the header line exactly as is
                String headerLine = reader.readLine();
                lines.add(headerLine);
                
                //process each existing customer line
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    
                    String firstName = parts[columnMap.get("First Name")].replace("\"", "").trim();
                    String lastName = parts[columnMap.get("Last Name")].replace("\"", "").trim();
                    String fullName = firstName + " " + lastName;
                    
                    Customer customer = customers.get(fullName);
                    if (customer != null) {
                        //update balances for existing customer
                        parts[columnMap.get("Checking Starting Balance")] = 
                            String.format("%.2f", customer.getAccounts().get(0).getBalance());
                        parts[columnMap.get("Savings Starting Balance")] = 
                            String.format("%.2f", customer.getAccounts().get(1).getBalance());
                        parts[columnMap.get("Credit Starting Balance")] = 
                            String.format("%.2f", customer.getAccounts().get(2).getBalance());
                        processedCustomers.add(fullName);
                    }
                    lines.add(String.join(",", parts));
                }
            }
            
            //add new customers who weren't in the original file
            for (Map.Entry<String, Customer> entry : customers.entrySet()) {
                if (!processedCustomers.contains(entry.getKey())) {
                    Customer newCustomer = entry.getValue();
                    String[] nameParts = newCustomer.getName().split(" ");
                    
                    //create new line for customer using same column structure
                    String[] newLine = new String[columnMap.size()];
                    newLine[columnMap.get("Identification Number")] = newCustomer.getCustomerID();
                    newLine[columnMap.get("First Name")] = nameParts[0];
                    newLine[columnMap.get("Last Name")] = nameParts[1];
                    newLine[columnMap.get("Date of Birth")] = newCustomer.getDateOfBirth();
                    newLine[columnMap.get("Address")] = "\"" + newCustomer.getAddress() + "\"";
                    newLine[columnMap.get("Phone Number")] = newCustomer.getPhoneNumber();
                    
                    //add account information
                    List<Account> accounts = newCustomer.getAccounts();
                    newLine[columnMap.get("Checking Account Number")] = accounts.get(0).getAccountNumber();
                    newLine[columnMap.get("Checking Starting Balance")] = String.format("%.2f", accounts.get(0).getBalance());
                    newLine[columnMap.get("Savings Account Number")] = accounts.get(1).getAccountNumber();
                    newLine[columnMap.get("Savings Starting Balance")] = String.format("%.2f", accounts.get(1).getBalance());
                    newLine[columnMap.get("Credit Account Number")] = accounts.get(2).getAccountNumber();
                    newLine[columnMap.get("Credit Max")] = String.format("%.2f", ((Credit)accounts.get(2)).getCreditLimit());
                    newLine[columnMap.get("Credit Starting Balance")] = String.format("%.2f", accounts.get(2).getBalance());
                    
                    lines.add(String.join(",", newLine));
                }
            }
            
            //write everything to the new file
            try (FileWriter writer = new FileWriter(UPDATED_CSV_FILE)) {
                for (String line : lines) {
                    writer.write(line + "\n");
                }
            }
            
            System.out.println("Updated customer data saved to: " + UPDATED_CSV_FILE);
            
        } catch (IOException e) {
            System.err.println("Error saving customer data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}