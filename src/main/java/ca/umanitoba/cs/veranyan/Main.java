package ca.umanitoba.cs.veranyan;

import ca.umanitoba.cs.veranyan.ui.MainMenu;

/**
 * The main class is the exercise-tracking manager
 * to interact with the application. It launches the main menu, starting the program
 * @implNote start the main method in order to run the exercise tracker program.
 */
public class Main {
    public static void main(String[] args) {
        MainMenu mainMenu = new MainMenu();
        mainMenu.startProgram();
    }
}
