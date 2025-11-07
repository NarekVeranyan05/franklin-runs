package ca.umanitoba.cs.veranyan.ui;

import ca.umanitoba.cs.veranyan.logic.PathFinder;
import ca.umanitoba.cs.veranyan.model.exceptions.CoordinateOutOfBoundsException;
import ca.umanitoba.cs.veranyan.model.map.Map;
import ca.umanitoba.cs.veranyan.model.map.coordinate.Coordinate;
import ca.umanitoba.cs.veranyan.model.map.coordinate.CoordinateType;
import ca.umanitoba.cs.veranyan.output.Colourise;
import ca.umanitoba.cs.veranyan.output.MapPrinter;
import com.google.common.base.Preconditions;

import java.util.InputMismatchException;
import java.util.Optional;
import java.util.Scanner;

/**
 * The {@code PathFindingScreen} class manages the UI interaction for finding a route
 * between two positions on the map.
 */
public class PathFindingScreen {
    private static final int OWN = 1;

    private final PathFinder pathFinder;
    private final Scanner keyboard = new Scanner(System.in);

    public PathFindingScreen(PathFinder pathFinder){
        this.pathFinder = pathFinder;

        checkPathFindingScreen();
    }

    /**
     * Starts the route-finding UI flow
     * @return the route, if found
     */
    public Optional<Map.ProcessedRoute> startFind(){
        checkPathFindingScreen();

        Optional<Map.ProcessedRoute> route = Optional.empty();
        int choice = pathFindConfigPrompt();

        Coordinate start;
        Coordinate end;

        boolean canStartSeach = true;
        do {
            pathFinder.getMap().clearRoutes();
            new MapPrinter(pathFinder.getMap()).print();

            start = coordinateInsertionScreen();
            end = coordinateInsertionScreen();

            try {
                route = pathFinder.findPath(start, end, choice != OWN);
            } catch (CoordinateOutOfBoundsException e) {
                Colourise.red("The starting and ending coordinates were out of bounds of the map.\n");
                Colourise.red("A valid coordinate is one that's within map boundaries.\n");

                canStartSeach = false;
            }
        } while (!canStartSeach);

        if(route.isPresent()){
            new MapPrinter(pathFinder.getMap()).print();

            // fixme
            System.out.println("Here's the route that was found.");
            System.out.println("Enter 1 to select route or 0 to quit: ");
            choice = keyboard.nextInt();

            if(choice == 0)
                route = Optional.empty();
        }
        else{
            System.out.printf("There is no route between points (%d, %d) and (%d, %d).%n",
                    start.x(), start.y(), end.x(), end.y());
        }

        checkPathFindingScreen();

        return route;
    }

    /**
     * Prompts the user to insert a coordinate to participate in the route search
     * @return the inserted coordinate
     */
    private Coordinate coordinateInsertionScreen(){
        checkPathFindingScreen();

        Integer x, y;

        do {
            try {
                System.out.println("Enter the x-coordinate: ");
                x = keyboard.nextInt();
            }
            catch(InputMismatchException e){
                Colourise.red("Invalid x-coordinate.\n");
                Colourise.red("A valid input must be a number, e.g. -1.");

                x = null;
            }
            keyboard.nextLine();
        } while (x == null);

        do {
            try {
                System.out.println("Enter the y-coordinate: ");
                y = keyboard.nextInt();
            }
            catch(InputMismatchException e){
                Colourise.red("Invalid y-coordinate.\n");
                Colourise.red("A valid input must be a number, e.g. -1.");

                y = null;
            }
            keyboard.nextLine();
        } while (y == null);

        checkPathFindingScreen();

        return new Coordinate(CoordinateType.EMPTY, x, y);
    }

    /**
     * Prompts the user to choose configurations for route finding:
     * either find only from user's routes, or also from friends' routes
     * @return the configuration choice
     */
    private int pathFindConfigPrompt() {
        checkPathFindingScreen();

        int choice;
        do{
            Colourise.cyan("Enter which routes to find a path from (" + OWN + " for your own routes and any other number for your routes and your friends' routes): ");
            try {
                choice = keyboard.nextInt();
            }
            catch(InputMismatchException e){
                choice = -1;
            }

            if(choice == -1){
                Colourise.red("Invalid input.\n");
                Colourise.red("A valid input must be a number, e.g. -1.");
            }
        } while (choice == -1);

        checkPathFindingScreen();

        return choice;
    }

    /**
     * Class invariant for PathFindingScreen
     */
    private void checkPathFindingScreen(){
        Preconditions.checkNotNull(pathFinder, "pathFinder cannot be null");
        Preconditions.checkNotNull(keyboard, "keyboard cannot be null");
    }
}
