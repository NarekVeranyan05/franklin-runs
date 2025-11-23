package ca.umanitoba.cs.veranyan;

import ca.umanitoba.cs.veranyan.logic.MapManager;
import ca.umanitoba.cs.veranyan.logic.ProfileRegistry;
import ca.umanitoba.cs.veranyan.logic.RouteManager;
import ca.umanitoba.cs.veranyan.logic.exceptions.DuplicateProfileException;
import ca.umanitoba.cs.veranyan.logic.exceptions.RouteObstacleOverlapException;
import ca.umanitoba.cs.veranyan.model.exceptions.CoordinateOutOfBoundsException;
import ca.umanitoba.cs.veranyan.model.map.Map;
import ca.umanitoba.cs.veranyan.persistence.json.ObstaclePersistenceJson;
import ca.umanitoba.cs.veranyan.persistence.json.ProfilePersistenceJson;
import ca.umanitoba.cs.veranyan.ui.MainMenu;

import java.nio.file.Path;
import java.util.Scanner;

/**
 * The main class is the exercise-tracking manager
 * to interact with the application. It launches the main menu, starting the program
 * @implNote start the main method in order to run the exercise tracker program.
 */
public class Main {
    public static void main(String[] args) {
        ProfilePersistenceJson profilePersistence = new ProfilePersistenceJson(Path.of("profiles.json"));
        ObstaclePersistenceJson obstaclePersistence = new ObstaclePersistenceJson(Path.of("obstacles.json"));

        ProfileRegistry profileRegistry = new ProfileRegistry(profilePersistence);
        MapManager mapManager = new MapManager(obstaclePersistence, Map.getInstance());

        try {
            for (var profile : profilePersistence.loadProfiles())
                profileRegistry.addProfile(profile);

            for(var obstacle : obstaclePersistence.loadObstacles())
                mapManager.addObstacle(obstacle);
        } catch (DuplicateProfileException | CoordinateOutOfBoundsException | RouteObstacleOverlapException e) {
            e.printStackTrace(); // FIXME is this ok?
        }

        RouteManager routeManager = new RouteManager(
                Map.getInstance()
        );

        Scanner scanner = new Scanner(System.in);

        MainMenu mainMenu = new MainMenu(profileRegistry, mapManager, routeManager, scanner);
        mainMenu.startProgram();
    }
}
