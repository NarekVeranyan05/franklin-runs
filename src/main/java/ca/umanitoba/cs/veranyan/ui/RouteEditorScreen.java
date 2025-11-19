package ca.umanitoba.cs.veranyan.ui;

import ca.umanitoba.cs.veranyan.logic.RouteManager;
import ca.umanitoba.cs.veranyan.logic.exceptions.RouteObstacleOverlapException;
import ca.umanitoba.cs.veranyan.model.Activity;
import ca.umanitoba.cs.veranyan.model.exceptions.CoordinateOutOfBoundsException;
import ca.umanitoba.cs.veranyan.logic.MapManager;
import ca.umanitoba.cs.veranyan.logic.ProfileRegistry;
import ca.umanitoba.cs.veranyan.model.map.Map;
import ca.umanitoba.cs.veranyan.model.map.Route;
import ca.umanitoba.cs.veranyan.model.map.coordinate.Coordinate;
import ca.umanitoba.cs.veranyan.model.map.coordinate.CoordinateType;
import ca.umanitoba.cs.veranyan.output.Colourise;
import ca.umanitoba.cs.veranyan.output.MapPrinter;
import com.google.common.base.Preconditions;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * The {@link GearEditorScreen} class manages the UI interaction for managing routes,
 * such as adding or selecting them for an {@link Activity}.
 */
public class RouteEditorScreen {
    private static final int QUIT = 0;

    private static final int UP = 1;
    private static final int RIGHT = 2;
    private static final int DOWN = 3;
    private static final int LEFT = 4;

    private final ProfileRegistry profileRegistry;
    private final MapManager mapManager;
    private final RouteManager routeManager;
    private final Scanner keyboard;

    public RouteEditorScreen(ProfileRegistry profileRegistry, MapManager mapManager, RouteManager routeManager, Scanner scanner){
        this.profileRegistry = profileRegistry;
        this.mapManager = mapManager;
        this.routeManager = routeManager;
        this.keyboard = scanner;

        checkRouteInsertionDisplay();
    }

    /**
     * Prompts the user to create a new {@link Route}
     * @return the {@link Route} created.
     */
    public MapManager.ProcessedRoute routeInsertionScreen() {
        checkRouteInsertionDisplay();

        mapManager.getMap().clearRoutes();
        new MapPrinter(mapManager.getMap()).print();
        Route route;
        MapManager.ProcessedRoute processedRoute;

        do {
            try {
                route = new Route.RouteBuilder().withCoordinate(
                    new Coordinate(CoordinateType.ROUTE, promptX(), promptY())
                ).build();

                // adds the route to the map to process it
                processedRoute = mapManager.addRoute(route);
            } catch (RouteObstacleOverlapException e) {
                Colourise.red("The route you wanted to add overlaps with an obstacle.\n");
                Colourise.red("A valid route is one that does not pass through an obstacle\n");

                processedRoute = null;
            } catch (CoordinateOutOfBoundsException e) {
                Colourise.red("The route you wanted to add is out of the map boundaries.\n");
                Colourise.red("A valid route is one that stays within map boundaries.\n");
                Colourise.red(
                    "The map has width " + mapManager.getMap().getWidth() +
                    " and length " + mapManager.getMap().getLength() + "\n"
                );

                processedRoute = null;
            }
        } while(processedRoute == null);

        moveInsertionScreen(processedRoute);

        checkRouteInsertionDisplay();

        return processedRoute;
    }

    /**
     * Prompts the user to provide the {@link Route} starting point's x-coordinate
     * @return the x-coordinate entered by the user
     */
    private int promptX() {
        checkRouteInsertionDisplay();

        Integer x;
        do {
            Colourise.cyan("Enter the starting x-coordinate: ");
            try {
                x = keyboard.nextInt();
            } catch (InputMismatchException e) {
                Colourise.red("Invalid input: an x-coordinate must be a whole number, e.g. 7\n");

                x = null;
            }

            keyboard.nextLine();
        } while (x == null);

        checkRouteInsertionDisplay();

        return x;
    }

    /**
     * Prompts the user to provide the {@link Route} starting point's y-coordinate
     * @return the y-coordinate entered by the user
     */
    private int promptY() {
        checkRouteInsertionDisplay();

        Integer y = null;
        do {
            Colourise.cyan("Enter the starting y-coordinate: ");
            try {
                y = keyboard.nextInt();
            } catch (InputMismatchException e) {
                Colourise.red("Invalid input: a y-coordinate must be a whole number, e.g. 7\n");
            }

            keyboard.nextLine();
        } while (y == null);

        checkRouteInsertionDisplay();

        return y;
    }

    /**
     * Prompts the user to insert the move operations on a {@link Route}.
     * @param route the {@link Route} to be moved in.
     */
    public void moveInsertionScreen(MapManager.ProcessedRoute route){
        checkRouteInsertionDisplay();

        mapManager.setUpRoute(route);
        var mapPrinter = new MapPrinter(mapManager.getMap());

        int choice = promptMoveInsertionChoice();

        if(choice != QUIT) // initial printing
            mapPrinter.print();

        while (choice != QUIT){
            System.out.println("Directions: ");
            System.out.println( UP + ". UP");
            System.out.println( RIGHT + ". RIGHT");
            System.out.println( DOWN + ". DOWN");
            System.out.println( LEFT + ". LEFT");

            int direction = promptRouteDirection();
            int numSteps = promptNumberOfSteps();

            try{
                routeManager.doMove(route, direction, numSteps);
            } catch (RouteObstacleOverlapException e){
                Colourise.red("Cannot move in " + numSteps + " steps in that direction because route will overlap with an obstacle.\n");
            } catch (CoordinateOutOfBoundsException e) {
                Colourise.red("Cannot move in " + numSteps + " steps in that direction because route will go out of map bounds.\n");
            }

            // updating the route to display
            // no need to remove the route from map since exit upon this method results in valid route
            mapManager.setUpRoute(route);
            mapPrinter.print();

            choice = promptMoveInsertionChoice();
        }

        checkRouteInsertionDisplay();
    }

    /**
     * Prompts the user to choose to either continue moving through the {@link Map} or quitting
     * @return the choice of the user
     */
    private int promptMoveInsertionChoice() {
        checkRouteInsertionDisplay();

        int choice;

        do{
            Colourise.cyan("Enter " + QUIT + " to end route or any other number to proceed: ");
            try{
                choice = keyboard.nextInt();
            } catch (final InputMismatchException e){
                Colourise.red("Invalid input. You must enter a whole number, e.g. 1.\n");

                choice = -1;
            }

            keyboard.nextLine();

        } while (choice == -1);

        checkRouteInsertionDisplay();

        return choice;
    }

    /**
     * Prompts the user to provide the number of steps for a single move operation on a {@link Route}
     * @return the selected number of steps. Must be non-negative
     */
    public int promptNumberOfSteps() {
        checkRouteInsertionDisplay();

        int numSteps;
        do {
            Colourise.cyan("Enter number of steps: ");
            try {
                numSteps = keyboard.nextInt();

                if (numSteps <= 0) {
                    Colourise.red(numSteps + " is not a valid number of steps.\n");
                    Colourise.red("You must enter a whole number greater than 0, e.g. 3\n");

                    numSteps = -1;
                }
            } catch (InputMismatchException e) {
                Colourise.red("Invalid input. You must enter a whole number, e.g 10\n");

                numSteps = -1;
            }

            keyboard.nextLine();
        } while (numSteps <= 0);

        checkRouteInsertionDisplay();

        return numSteps;
    }

    /**
     * Prompts the user to provide the direction for a single move operation on a {@link Route}
     * @return the selected direction [UP = {@code 1}, RIGHT = {@code 2}, DOWN = {@code 3}, LEFT = {@code 4}]
     */
    private int promptRouteDirection() {
        checkRouteInsertionDisplay();

        int direction;
        do {
            Colourise.cyan("Select one of the above options by number: ");
            try {
                direction = keyboard.nextInt();

                if (direction != UP && direction != RIGHT && direction != DOWN && direction != LEFT) {
                    Colourise.red(String.format(
                            """
                            %d is not a valid option.
                            You must enter a number that corresponds to these options:
                            %d. UP
                            %d. RIGHT
                            %d DOWN
                            %d LEFT
                            Valid inputs are: %d, %d, %d, %d.
                            %n""", direction, UP, RIGHT, DOWN, LEFT, UP, RIGHT, DOWN, LEFT)
                    );

                    direction = -1;
                }
            } catch (InputMismatchException e) {
                Colourise.red("Invalid input. You must enter a whole number, e.g. 3\n");

                direction = -1;
            }

            keyboard.nextLine();
        } while (direction == -1);

        checkRouteInsertionDisplay();

        return direction;
    }

    /**
     * Prompts the user to select an existing {@link Route} from their own routes
     * @return the selected {@link MapManager.ProcessedRoute} wrapped in {@link Optional},
     * or an empty {@link Optional} if there are no routes to select from
     *
     */
    public Optional<MapManager.ProcessedRoute> routeSelectionScreen(){
        checkRouteInsertionDisplay();

        Optional<MapManager.ProcessedRoute> result = Optional.empty();

        // A person should only be able to select from their own previous routes.
        List<MapManager.ProcessedRoute> routes = profileRegistry.getCurrentProfile().getRoutes();

        if(routes.isEmpty()){
            Colourise.red("There are no previous routes to select from.\n");
        }
        else {
            for (int j = 0; j < routes.size(); j++) {
                var route = routes.get(j);
                System.out.println("Route #" + (j + 1) + ":");

                mapManager.setUpRoute(route);
                new MapPrinter(mapManager.getMap()).print();
            }

            int choice;
            do {
                Colourise.cyan("Select one of the above routes by number: ");
                try {
                    choice = keyboard.nextInt();

                    if (choice < 1 || choice > routes.size()) {
                        Colourise.red(choice + " does not match with any route.\n");
                        Colourise.red("A valid input is a whole number that matches with a route. Valid inputs are:\n");

                        choice = 0;
                    }
                } catch (final InputMismatchException e) {
                    Colourise.red("Invalid input.\n");
                    Colourise.red("A valid input must be a number, e.g. 1.\n");

                    choice = 0;
                }

                keyboard.nextLine();
            } while (choice == 0);

            result = Optional.of(routes.get(choice - 1).clone());
        }

        checkRouteInsertionDisplay();

        return result;
    }

    /**
     * Class invariants for {@link RouteEditorScreen}
     */
    private void checkRouteInsertionDisplay(){
        Preconditions.checkNotNull(profileRegistry, "profileRegistry cannot be null");
        Preconditions.checkNotNull(mapManager, "activityManager cannot be null");
        Preconditions.checkNotNull(keyboard, "keyboard cannot be null");
    }
}
