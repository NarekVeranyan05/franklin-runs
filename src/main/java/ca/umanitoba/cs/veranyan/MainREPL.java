package ca.umanitoba.cs.veranyan;

import ca.umanitoba.cs.veranyan.logic.MapManager;
import ca.umanitoba.cs.veranyan.logic.ProfileRegistry;
import ca.umanitoba.cs.veranyan.logic.RouteManager;
import ca.umanitoba.cs.veranyan.logic.exceptions.DuplicateProfileException;
import ca.umanitoba.cs.veranyan.model.exceptions.GearNotFoundException;
import ca.umanitoba.cs.veranyan.logic.exceptions.RouteObstacleOverlapException;
import ca.umanitoba.cs.veranyan.model.Activity;
import ca.umanitoba.cs.veranyan.model.Profile;
import ca.umanitoba.cs.veranyan.model.exceptions.*;
import ca.umanitoba.cs.veranyan.model.gear.Gear;
import ca.umanitoba.cs.veranyan.model.gear.GearType;
import ca.umanitoba.cs.veranyan.model.map.Map;
import ca.umanitoba.cs.veranyan.model.map.Obstacle;
import ca.umanitoba.cs.veranyan.model.map.Route;
import ca.umanitoba.cs.veranyan.output.ActivityPrinter;
import ca.umanitoba.cs.veranyan.output.GearPrinter;
import ca.umanitoba.cs.veranyan.output.MapPrinter;
import ca.umanitoba.cs.veranyan.output.ObstaclePrinter;
import ca.umanitoba.cs.veranyan.persistence.json.ObstaclePersistenceJson;
import ca.umanitoba.cs.veranyan.persistence.json.ProfilePersistenceJson;
import ca.umanitoba.cs.veranyan.ui.ActivityInsertionScreen;

import java.nio.file.Path;
import java.util.Scanner;

/**
 * The main class is the exercise-tracking manager
 * to interact with the application.
 * @implNote start the main method in order to run the exercise tracker program.
 */
public class MainREPL {
    private static Scanner scnr;
    private static Profile profile;
    private static final Map map = Map.getInstance();

    private static final ProfilePersistenceJson profilePersistence = new ProfilePersistenceJson(Path.of("profiles.json"));
    private static final ObstaclePersistenceJson obstaclePersistence = new ObstaclePersistenceJson(Path.of("obstacles.json"));

    private static final ProfileRegistry profileRegistry = new ProfileRegistry(profilePersistence);
    private static final MapManager mapManager = new MapManager(obstaclePersistence, Map.getInstance());
    private static final RouteManager routeManager = new RouteManager(map);

    /**
     * Prints out to standard output stream (System.out).
     * Takes input from standard input stream (System.in).
     * @param args arguments from command line.
     */
    public static void main(String[] args) {
        scnr = new Scanner(System.in);

        try {
            // account set-up process. Initial gear needs to be added to create the account
            System.out.println("Welcome to the exercise tracker.");
            System.out.println("Getting started with your account...");
            addProfile();
            System.out.println("Add new gear to the account:");
            addGear();
            System.out.println("Account creation completed. Let's get started.\n");

            printMenu();

            // prompting user input to start processing
            int optionResponse = promptInt("Enter selected option number or -1 to quit");
            scnr.nextLine();
            while (optionResponse != -1) {
                switch (optionResponse) {
                    case 1:
                        addGear();
                        break;
                    case 2:
                        addObstacle();
                        break;
                    case 3:
                        addActivity();
                        break;
                    case 4:
                        showMap(true);
                        break;
                    case 5:
                        showGear();
                        break;
                    case 6:
                        showObstacles();
                        break;
                    case 7:
                        showActivities();
                        break;
                    case 8:
                        showActivity();
                        break;
                    case 9:
                        removeGear();
                        break;
                    case 10:
                        removeActivity();
                        break;
                    case 11:
                        removeObstacle();
                        break;
                    case 12:
                        removeMap();
                        break;
                    default:
                        System.out.println("Invalid entry. Please try again.");
                }

                System.out.println();
                printMenu();
                optionResponse = promptInt("Enter selected option number or -1 to quit");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Prompts user to add new {@link Profile}. Prints out to standard output stream (System.out).
     * Takes input from standard input stream (System.in).
     */
    private static void addProfile() throws BlankNameException, DuplicateProfileException {
        String name = promptString("Enter profile name (cannot be blank)");
        profile = new Profile.ProfileBuilder().name(name).build();
        profileRegistry.addProfile(profile);
        profileRegistry.loadProfile(profile);
    }

    /**
     * Prompts user to add new {@link Gear}. Prints out to standard output stream (System.out).
     * Takes input from standard input stream (System.in).
     */
    private static void addGear() throws BlankNameException, NonPositiveSpeedException, DuplicateGearException {
        // prints out GearType values
        System.out.println("Gear types:");
        int i = 1;
        for(var type : GearType.values()){
            System.out.print(i + ". ");
            System.out.println(type);
            i++;
        }
        int gearTypeNumber = promptInt(
                String.format("Enter select gear type number (should be value from 0 to %d inclusive)", --i));

        var type = GearType.values()[gearTypeNumber-1];
        scnr.nextLine();
        String name = promptString("Enter gear name (cannot be blank)");
        int avgSpeed = promptInt("Enter the average speed in meters per second (must be positive)");

        profile.addGear(new Gear.GearBuilder().type(type).name(name).avgSpeed(avgSpeed).build());
    }

    /**
     * Prompts user to add new {@link Obstacle}.
     * Prints out to standard output stream (System.out).
     * Takes input from standard input stream (System.in).
     */
    private static void addObstacle() throws CoordinateOutOfBoundsException, TopLeftBottomRightCoordMismatchException, RouteObstacleOverlapException {
        showMap(false);

        System.out.println("---Obstacle coordinates must be within map boundaries---");
        int topLeftX = promptInt("Enter upper-left x-coordinate");
        int upperLeftY = promptInt("Enter upper-left y-coordinate");
        int lowerRightX = promptInt("Enter lower-right x-coordinate");
        int lowerRightY = promptInt("Enter lower-right y-coordinate");

        mapManager.addObstacle(
            new Obstacle.ObstacleBuilder().topLeftX(topLeftX).topLeftY(upperLeftY).bottomRightX(lowerRightX).bottomRightY(lowerRightY).build()
        );
    }

    /**
     * Prompts user to add new {@link Obstacle}.
     * Prints out to standard output stream (System.out).
     * Takes input from standard input stream (System.in).
     */
    private static void addActivity() {
        new ActivityInsertionScreen(profileRegistry, mapManager, routeManager, scnr).startInsert();
    }

    /**
     * Displays {@link Map}.
     * Prints out to standard output stream (System.out).
     * @param summarise true if summed distance of all routes is to be printed, false otherwise
     */
    private static void showMap(boolean summarise) {
        var printer = new MapPrinter(map);

        if(summarise)
            printer.print(profile);
        else printer.print();
    }

    /**
     * Displays gears. Prints out to standard output stream (System.out)
     */
    private static void showGear() {
        if(!profile.getGears().isEmpty()){
            int i = 1;
            for(var gear : profile.getGears()){
                System.out.print(i + ". ");
                new GearPrinter(gear).print();
                System.out.println();
                i++;
            }
        }
        else System.out.println("No gear added yet.");
    }

    /**
     * Displays obstacles. Prints out to standard output stream (System.out)
     */
    private static void showObstacles() {
        if(!map.getObstacles().isEmpty()){
            int i = 1;
            for(Obstacle obstacle : map.getObstacles()) {
                System.out.print(i + ". ");
                new ObstaclePrinter(obstacle).print();
                System.out.println();
                i++;
            }
        }
        else System.out.println("There are no obstacles added yet.");
    }

    /**
     * Displays activities. Prints out to standard output stream (System.out).
     */
    private static void showActivities() {
        if(!profile.getActivities().isEmpty()){
            int i = 1;
            for(Activity activity : profile.getActivities()){
                System.out.print(i + ". ");
                new ActivityPrinter(activity).print();
                System.out.println();
                i++;
            }
        }
        else System.out.println("There are no activities added yet.");
    }

    /**
     * Displays a single {@link Activity}'s {@link Route} on the {@link Map}.
     * Prints out to standard output stream (System.out).
     * Takes input from the standard output stream (System.in).
     */
    private static void showActivity() {
        if(!profile.getActivities().isEmpty()){
            showActivities();
            int activityNumber = promptInt("Enter selected activity number");

            map.clearRoutes();
            map.addActivity(profile.getActivities().stream().toList().get(activityNumber - 1));
            new MapPrinter(map).print();
        }
        else System.out.println("There are no activities added yet.");
    }

    /**
     * Removes {@link Gear} from the system.
     * Prints out to standard output stream (System.out).
     * Takes input from the standard output stream (System.in).
     */
    private static void removeGear() throws GearNotFoundException {
        if(!profile.getGears().isEmpty()){
            showGear();

            String gearName = promptString(
                    String.format("Enter selected gear name (must be from 1 to %d inclusive)",
                            profile.getGears().size()));
            profile.removeGear(profile.getGear(gearName));
        }
        else System.out.println("No gears to remove.");
    }

    /**
     * Removes an {@link Activity} from the system.
     * Prints out to standard output stream (System.out).
     * Takes input from the standard output stream (System.in).
     */
    private static void removeActivity() {
        if(!profile.getActivities().isEmpty()) {
            showActivities();
            int activityNumber = promptInt(
                    String.format("Enter selected activity number (must be from 1 to %d inclusive)",
                            profile.getActivities().size()));

            profile.removeActivity(activityNumber - 1);
        }
        else System.out.println("No activities to remove.");
    }

    /**
     * Removes an {@link Obstacle} from the system.
     * Prints out to standard output stream (System.out).
     * Takes input from the standard output stream (System.in).
     */
    private static void removeObstacle() {
        if(!map.getObstacles().isEmpty()) {
            showObstacles();
            int obstacleNumber = promptInt(
                    String.format("Enter selected obstacle number (must be from 1 to %d inclusive)",
                            map.getObstacles().size()));

            map.removeObstacle(obstacleNumber-1);
        }
        else System.out.println("No obstacles to remove.");
    }

    /**
     * Removes the {@link Map} singleton from the system.
     * Prints out to standard output stream (System.out).
     */
    private static void removeMap() {
        Map.destroyInstance();
        System.out.println("Map removed successfully");
    }

    /**
     * Prints the control menu.
     * Prints out to standard output stream (System.out).
     */
    public static void printMenu(){
        System.out.println("""
        Select one of the options below:
        1.  ADD GEAR
        2.  ADD OBSTACLE
        3.  ADD ACTIVITY
        4.  SHOW MAP
        5.  SHOW GEAR
        6.  SHOW OBSTACLES
        7.  SHOW ACTIVITIES
        8.  SHOW ACTIVITY
        9. REMOVE GEAR
        10. REMOVE ACTIVITY
        11. REMOVE OBSTACLE
        12. REMOVE MAP
        """);
    }

    /**
     * Prompts to enter an integer. Reads input from a Scanner with unspecified input stream.
     * @param prompt the description of what needs to be passed as input.
     * @return the value that was passed as input.
     */
    private static int promptInt(String prompt){
        System.out.print(prompt + ": ");
        return scnr.nextInt();
    }

    /**
     * Prompts to enter a double. Reads input from a Scanner with unspecified input stream.
     * @param prompt the description of what needs to be passed as input.
     * @return the value that was passed as input.
     */
    private static double promptDouble(String prompt) {
        System.out.print(prompt + ": ");
        return scnr.nextDouble();
    }

    /**
     * Prompts to enter a String. Reads input from a Scanner with unspecified input stream.
     * @param prompt the description of what needs to be passed as input.
     * @return the value that was passed as input.
     */
    private static String promptString(String prompt){
        System.out.print(prompt + ": ");
        return scnr.nextLine();
    }
}