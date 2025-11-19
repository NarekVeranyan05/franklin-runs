package ca.umanitoba.cs.veranyan.ui;

import ca.umanitoba.cs.veranyan.logic.exceptions.EndCoordOutOfBoundsException;
import ca.umanitoba.cs.veranyan.logic.exceptions.StartCoordOutOfBoundsException;
import ca.umanitoba.cs.veranyan.logic.PathFinder;
import ca.umanitoba.cs.veranyan.model.map.coordinate.Coordinate;
import ca.umanitoba.cs.veranyan.model.map.coordinate.CoordinateType;
import ca.umanitoba.cs.veranyan.output.Colourise;
import ca.umanitoba.cs.veranyan.output.MapPrinter;
import com.google.common.base.Preconditions;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * The {@code PathFindingScreen} class manages the UI interaction for finding a route
 * between two positions on the map.
 */
public class PathFindingScreen {
    private static final int OWN = 1;

    private final PathFinder pathFinder;
    private final Scanner keyboard;

    public PathFindingScreen(PathFinder pathFinder, Scanner scanner){
        this.pathFinder = pathFinder;
        this.keyboard = scanner;

        checkPathFindingScreen();
    }

    /**
     * Starts the route-finding UI flow
     */
    public void startFind(){
        checkPathFindingScreen();

        int choice = pathFindConfigPrompt();

        Coordinate start;
        Coordinate end;

        boolean canStartSeach;
        do {
            canStartSeach = true;

            pathFinder.getMap().clearRoutes();
            new MapPrinter(pathFinder.getMap()).print();

            start = coordinateInsertionScreen();
            end = coordinateInsertionScreen();

            try {
                boolean isFound = pathFinder.findPath(start, end, choice != OWN);

                if(isFound){
                    new MapPrinter(pathFinder.getMap()).print();
                    System.out.printf("Here's the route between (%d, %d) and (%d, %d), shown in the map above.%n",
                            start.x(), start.y(), end.x(), end.y());
                }
                else{
                    System.out.printf("There is no route between points (%d, %d) and (%d, %d).%n",
                            start.x(), start.y(), end.x(), end.y());
                }
            } catch (StartCoordOutOfBoundsException e) {
                Colourise.red("(" + start.x() + ", " + start.y() + ")" + " is out of bounds of a map\n");
                Colourise.red("with length " + pathFinder.getMap().getLength() + " and width " + pathFinder.getMap().getWidth() + ".\n");
                Colourise.red("A valid coordinate is one that's within map boundaries.\n");

                canStartSeach = false;
            } catch (EndCoordOutOfBoundsException e) {
                Colourise.red("(" + end.x() + ", " + end.y() + ")" + " is out of bounds of a map\n");
                Colourise.red("with length " + pathFinder.getMap().getLength() + " and width " + pathFinder.getMap().getWidth() + ".\n");
                Colourise.red("A valid coordinate is one that's within map boundaries.\n");

                canStartSeach = false;
            }
        } while (!canStartSeach);

        checkPathFindingScreen();
    }

    /**
     * Prompts the user to insert a {@link Coordinate} to participate in the {@link ca.umanitoba.cs.veranyan.model.map.Route} search
     * @return the inserted {@link Coordinate}
     */
    private Coordinate coordinateInsertionScreen(){
        checkPathFindingScreen();

        Integer x, y;

        do {
            try {
                Colourise.cyan("Enter the x-coordinate: ");
                x = keyboard.nextInt();
            }
            catch(InputMismatchException e){
                Colourise.red("Invalid x-coordinate.\n");
                Colourise.red("You must enter a whole number, e.g. 1.\n");

                x = null;
            }
            keyboard.nextLine();
        } while (x == null);

        do {
            try {
                Colourise.cyan("Enter the y-coordinate: ");
                y = keyboard.nextInt();
            }
            catch(InputMismatchException e){
                Colourise.red("Invalid y-coordinate.\n");
                Colourise.red("You must enter a whole number, e.g. 1.\n");

                y = null;
            }
            keyboard.nextLine();

        } while (y == null);

        checkPathFindingScreen();

        return new Coordinate(CoordinateType.EMPTY, x, y);
    }

    /**
     * Prompts the user to choose configurations for {@link ca.umanitoba.cs.veranyan.model.map.Route} finding:
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
                Colourise.red("Invalid input.\n");
                Colourise.red("You must enter a whole number, e.g. 1.\n");

                choice = -1;
            }

            keyboard.nextLine();

        } while (choice == -1);

        checkPathFindingScreen();

        return choice;
    }

    /**
     * Class invariant for {@link PathFinder}
     */
    private void checkPathFindingScreen(){
        Preconditions.checkNotNull(pathFinder, "pathFinder cannot be null");
        Preconditions.checkNotNull(keyboard, "keyboard cannot be null");
    }
}
