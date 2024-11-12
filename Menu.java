/**
 * Menu interface defines the contract for different types of menus in the banking system.
 * This interface is part of the Strategy pattern implementation, allowing different
 * menu behaviors to be swapped at runtime while maintaining a consistent interface.
 * 
 * Each menu type (customer menu, bank manager menu) will implement this interface
 * but provide its own specific implementation of how to:
 * - Display menu options
 * - Handle user choices
 * - Get user input
 * 
 * @author Daniel Fuentes, Rogelio Lozano
 * @version 1.0
 */
public interface Menu {
    /**
     * Displays the menu options to the user.
     * Each implementing class will define its own specific menu layout and options.
     */
    void displayMenu();
    
    /**
     * Handles the user's menu choice and performs the corresponding action.
     * 
     * @param choice the user's selected menu option
     * @return boolean indicating if the menu should continue showing (true) or exit (false)
     */
    boolean handleChoice(String choice);
    
    /**
     * Gets input from the user in a standardized way.
     * This method can be customized by each menu type to handle specific input requirements.
     * 
     * @return the user's input as a String
     */
    String getInput();
}