import java.util.*;

/**
 * Handles the creation of new bank users and their accounts.
 * This class manages:
 * - Generating unique IDs
 * - Creating accounts
 * - Setting credit limits
 * - Validating new user names
 * 
 * @author Daniel Fuentes, Rogelio Lozano
 * @version 2.0
 */
public class NewUsers {
    private int lastUserId;
    private Map<String, Customer> existingCustomers;
    private Random random;

    public NewUsers(Map<String, Customer> existingCustomers) {
        this.existingCustomers = existingCustomers;
        this.random = new Random();
        initializeLastUserId();
    }

    private void initializeLastUserId() {
        lastUserId = existingCustomers.values().stream()
            .mapToInt(c -> Integer.parseInt(c.getCustomerID()))
            .max()
            .orElse(0);
    }

    public boolean isValidNewCustomerName(String firstName, String lastName) {
        // Count customers with matching first name and last name
        int firstNameMatches = 0;
        int lastNameMatches = 0;
        String fullName = firstName + " " + lastName;

        // Check if exact full name already exists
        if (existingCustomers.containsKey(fullName)) {
            return false;
        }

        for (String existingName : existingCustomers.keySet()) {
            String[] nameParts = existingName.split(" ");
            if (nameParts[0].equalsIgnoreCase(firstName)) {
                firstNameMatches++;
            }
            if (nameParts[1].equalsIgnoreCase(lastName)) {
                lastNameMatches++;
            }

            // If we find matches for both first and last name, it's invalid
            if (firstNameMatches > 0 && lastNameMatches > 0) {
                return false;
            }
        }

        return true;
    }

    public Customer createUser(Map<String, String> userData) {
        // Validate required fields
        validateUserData(userData);
        
        // Generate customer ID
        String customerId = String.valueOf(++lastUserId);
        
        String firstName = userData.get("firstName");
        String lastName = userData.get("lastName");
        String fullName = firstName + " " + lastName;
        
        // Create new customer object
        Customer newCustomer = new Customer(fullName, customerId);
        
        // Set customer details
        newCustomer.setDateOfBirth(userData.get("dob"));
        newCustomer.setAddress(userData.get("address"));
        newCustomer.setCity(userData.get("city"));
        newCustomer.setState(userData.get("state"));
        newCustomer.setZipCode(userData.get("zip"));
        newCustomer.setPhoneNumber(userData.get("phone"));
        newCustomer.setCreditScore(Integer.parseInt(userData.get("creditScore")));
        
        // Generate account numbers
        List<String> accountNumbers = generateAccountNumbers(customerId);
        List<Account> accounts = new ArrayList<>();
        
        // Create accounts
        Account checking = new Checkings(accountNumbers.get(0), 0.0);
        checking.setOwner(newCustomer);
        accounts.add(checking);
        
        Account savings = new Savings(accountNumbers.get(1), 0.0);
        savings.setOwner(newCustomer);
        accounts.add(savings);
        
        double creditLimit = getCreditLimit(newCustomer.getCreditScore());
        Account credit = new Credit(accountNumbers.get(2), 0.0, creditLimit);
        credit.setOwner(newCustomer);
        accounts.add(credit);
        
        newCustomer.setAccounts(accounts);
        return newCustomer;
    }

    private void validateUserData(Map<String, String> userData) {
        String[] requiredFields = {
            "firstName", "lastName", "dob", "address", "city", 
            "state", "zip", "phone", "creditScore"
        };
        
        List<String> missingFields = new ArrayList<>();
        for (String field : requiredFields) {
            if (!userData.containsKey(field) || userData.get(field).trim().isEmpty()) {
                missingFields.add(field);
            }
        }
        
        if (!missingFields.isEmpty()) {
            throw new IllegalArgumentException("Missing required fields: " + 
                String.join(", ", missingFields));
        }
        
        // Validate credit score
        try {
            int creditScore = Integer.parseInt(userData.get("creditScore"));
            if (creditScore < 300 || creditScore > 850) {
                throw new IllegalArgumentException("Credit score must be between 300 and 850");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid credit score format");
        }
    }

    private List<String> generateAccountNumbers(String customerId) {
        String paddedId = String.format("%03d", Integer.parseInt(customerId));
        return Arrays.asList(
            "1" + paddedId,  // Checking
            "2" + paddedId,  // Savings
            "3" + paddedId   // Credit
        );
    }

    private double getCreditLimit(int creditScore) {
        if (creditScore <= 580) {
            return 100 + random.nextDouble() * (699 - 100);
        } else if (creditScore <= 669) {
            return 700 + random.nextDouble() * (4999 - 700);
        } else if (creditScore <= 739) {
            return 5000 + random.nextDouble() * (7499 - 5000);
        } else if (creditScore <= 799) {
            return 7500 + random.nextDouble() * (15999 - 7500);
        } else {
            return 16000 + random.nextDouble() * (25000 - 16000);
        }
    }
}