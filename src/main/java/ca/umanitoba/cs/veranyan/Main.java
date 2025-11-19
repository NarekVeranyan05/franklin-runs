package ca.umanitoba.cs.veranyan;

import ca.umanitoba.cs.veranyan.logic.MapManager;
import ca.umanitoba.cs.veranyan.logic.ProfileRegistry;
import ca.umanitoba.cs.veranyan.logic.RouteManager;
import ca.umanitoba.cs.veranyan.model.map.Map;
import ca.umanitoba.cs.veranyan.ui.MainMenu;

import java.util.Scanner;

/**
 * The main class is the exercise-tracking manager
 * to interact with the application. It launches the main menu, starting the program
 * @implNote start the main method in order to run the exercise tracker program.
 */
public class Main {
    public static void main(String[] args) {
        ProfileRegistry profileRegistry = new ProfileRegistry();

        MapManager mapManager = new MapManager(
                Map.getInstance()
        );

        RouteManager routeManager = new RouteManager(
                Map.getInstance()
        );

        Scanner scanner = new Scanner(System.in);

        MainMenu mainMenu = new MainMenu(profileRegistry, mapManager, routeManager, scanner);
        mainMenu.startProgram();
    }
}
