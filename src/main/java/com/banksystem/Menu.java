package com.banksystem;
/**
 * Menu interface defines different types of menus in the banking system.
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
     * Gets input from the user
     * This method is customized by each menu type to handle specific input requirements.
     * 
     * @return the user's input as a String
     */
    String getInput();
}