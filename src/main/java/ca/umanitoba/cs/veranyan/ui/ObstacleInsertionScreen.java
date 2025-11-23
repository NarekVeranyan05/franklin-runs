package ca.umanitoba.cs.veranyan.ui;

import ca.umanitoba.cs.veranyan.logic.exceptions.RouteObstacleOverlapException;
import ca.umanitoba.cs.veranyan.model.exceptions.CoordinateOutOfBoundsException;
import ca.umanitoba.cs.veranyan.logic.MapManager;
import ca.umanitoba.cs.veranyan.model.exceptions.TopLeftBottomRightCoordMismatchException;
import ca.umanitoba.cs.veranyan.model.map.Obstacle;
import ca.umanitoba.cs.veranyan.output.Colourise;
import com.google.common.base.Preconditions;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * The {@link ObstacleInsertionScreen} class manages the obstacles on the {@link ca.umanitoba.cs.veranyan.model.map.Map}
 */
public class ObstacleInsertionScreen {
    private final MapManager mapManager;
    private final Scanner keyboard;

    public ObstacleInsertionScreen(MapManager mapManager, Scanner scanner){
        this.mapManager = mapManager;
        this.keyboard = scanner;

        checkObstacleInsertionScreen();
    }

    /**
     * Prompts the user to create an {@link Obstacle} on the {@link ca.umanitoba.cs.veranyan.model.map.Map}.
     */
    public void startInsert(){
        checkObstacleInsertionScreen();
        Obstacle obstacle;

        do {
            var obstacleBuilder = new Obstacle.ObstacleBuilder();

            promptTopLeftX(obstacleBuilder);
            promptTopLeftY(obstacleBuilder);

            promptBottomRightX(obstacleBuilder);
            promptBottomRightY(obstacleBuilder);

            obstacle = obstacleBuilder.build();

            // adding the obstacle
            try {
                mapManager.addObstacle(obstacle);
            } catch (RouteObstacleOverlapException e) {
                Colourise.red("The obstacle you wanted to add overlaps with an existing route.\n");
                Colourise.red("Someone else (possibly you) had a route through it, so the obstacle position is invalid\n");
                Colourise.red("A valid obstacle position is one that does not overlap with existing routes\n");

                obstacle = null;
            } catch (CoordinateOutOfBoundsException e) {
                Colourise.red("The obstacle you wanted to add is out of map bounds.\n");
                Colourise.red("A valid obstacle position is one that stays within map bounds\n");
                Colourise.red(
                    "The map has width " + mapManager.getMap().getWidth() +
                    " and length " + mapManager.getMap().getLength() + "\n"
                );

                obstacle = null;
            }
        } while (obstacle == null);

        checkObstacleInsertionScreen();
    }


    /**
     * Prompts the user to provide the {@link Obstacle} top-left point's x-coordinate
     * @param obstacleBuilder the {@link Obstacle} builder
     */
    private void promptTopLeftX(Obstacle.ObstacleBuilder obstacleBuilder) {
        Preconditions.checkNotNull(obstacleBuilder, "obstacleBuilder cannot be null");
        checkObstacleInsertionScreen();

        int topLeftX = -1;

        do {
            Colourise.cyan("Enter top-left point x-coordinate: ");
            try {
                topLeftX = keyboard.nextInt();

                obstacleBuilder.topLeftX(topLeftX);
            } catch (CoordinateOutOfBoundsException e) {
                Colourise.red(topLeftX + " is out of map bounds.\n");
                Colourise.red("valid values for an x-coordinate are whole numbers from 1 to " + mapManager.getMap().getLength() +
                        " inclusive, e.g. " + (mapManager.getMap().getLength()) + "\n");

                topLeftX = -1;
            } catch (TopLeftBottomRightCoordMismatchException e) {
                Colourise.red(topLeftX + " is greater than the entered bottom-right x-coordinate.\n");
                Colourise.red("A valid top-left x-coordinate must be less than the bottom-right x-coordinate.\n");

                topLeftX = -1;
            } catch (InputMismatchException e) {
                Colourise.red("Invalid input: you must enter a whole number, e.g. 7\n");

                topLeftX = -1;
            }

            keyboard.nextLine();
        } while (topLeftX == -1);

        checkObstacleInsertionScreen();
    }

    /**
     * Prompts the user to provide the {@link Obstacle} top-left point's y-coordinate
     * @param obstacleBuilder the {@link Obstacle} builder
     */
    private void promptTopLeftY(Obstacle.ObstacleBuilder obstacleBuilder) {
        Preconditions.checkNotNull(obstacleBuilder, "obstacleBuilder cannot be null");
        checkObstacleInsertionScreen();

        int topLeftY = -1;
        do {
            Colourise.cyan("Enter top-left point y-coordinate: ");
            try {
                topLeftY = keyboard.nextInt();
                obstacleBuilder.topLeftY(topLeftY);
            } catch (CoordinateOutOfBoundsException e) {
                Colourise.red(topLeftY + " is out of map bounds.\n");
                Colourise.red("valid values for a y-coordinate are whole numbers from 1 to " + mapManager.getMap().getWidth() +
                        " inclusive, e.g. " + (mapManager.getMap().getWidth()) + "\n");

                topLeftY = -1;
            } catch (TopLeftBottomRightCoordMismatchException e) {
                Colourise.red(topLeftY + " is greater than the entered bottom-right y-coordinate.\n");
                Colourise.red("A valid top-left y-coordinate must be less than the bottom-right y-coordinate.\n");

                topLeftY = -1;
            } catch (InputMismatchException e) {
                Colourise.red("Invalid input: you must enter a whole number, e.g. 7\n");

                topLeftY = -1;
            }

            keyboard.nextLine();
        } while (topLeftY == -1);

        checkObstacleInsertionScreen();
    }

    /**
     * Prompts the user to provide the {@link Obstacle} bottom-right point's x-coordinate
     * @param obstacleBuilder the {@link Obstacle} builder
     */
    private void promptBottomRightX(Obstacle.ObstacleBuilder obstacleBuilder) {
        Preconditions.checkNotNull(obstacleBuilder, "obstacleBuilder cannot be null");
        checkObstacleInsertionScreen();

        int bottomRightX = -1;

        do {
            Colourise.cyan("Enter bottom-right point x-coordinate: ");
            try {
                bottomRightX = keyboard.nextInt();
                obstacleBuilder.bottomRightX(bottomRightX);
            } catch (CoordinateOutOfBoundsException e) {
                Colourise.red(bottomRightX + " is out of map bounds.\n");
                Colourise.red("valid values for a x-coordinate are whole numbers from 1 to " + mapManager.getMap().getLength() +
                        " inclusive, e.g. " + (mapManager.getMap().getLength()) + "\n");

                bottomRightX = -1;
            } catch (TopLeftBottomRightCoordMismatchException e) {
                Colourise.red(bottomRightX + " is less than the entered top-left x-coordinate.\n");
                Colourise.red("A valid bottom-right x-coordinate must be greater than the top-left x-coordinate.\n");

                bottomRightX = -1;
            } catch (InputMismatchException e) {
                Colourise.red("Invalid input: you must enter a whole number, e.g. 7\n");

                bottomRightX = -1;
            }

            keyboard.nextLine();
        } while (bottomRightX == -1);

        checkObstacleInsertionScreen();
    }

    /**
     * Prompts the user to provide the {@link Obstacle} top-left point's y-coordinate
     * @param obstacleBuilder the {@link Obstacle} builder
     */
    private void promptBottomRightY(Obstacle.ObstacleBuilder obstacleBuilder) {
        Preconditions.checkNotNull(obstacleBuilder, "obstacleBuilder cannot be null");
        checkObstacleInsertionScreen();

        int bottomRightY = -1;

        do {
            Colourise.cyan("Enter bottom-right point y-coordinate: ");
            try {
                bottomRightY = keyboard.nextInt();
                obstacleBuilder.bottomRightY(bottomRightY);
            } catch (CoordinateOutOfBoundsException e) {
                Colourise.red(bottomRightY + " is out of map bounds.\n");
                Colourise.red("valid values for a y-coordinate are whole numbers from 1 to " + mapManager.getMap().getWidth() +
                        " inclusive, e.g. " + (mapManager.getMap().getWidth()) + "\n");

                bottomRightY = -1;
            } catch (TopLeftBottomRightCoordMismatchException e) {
                Colourise.red(bottomRightY + " is less than the entered top-left y-coordinate.\n");
                Colourise.red("A valid bottom-right y-coordinate must be greater than the top-left y-coordinate.\n");

                bottomRightY = -1;
            } catch (InputMismatchException e) {
                Colourise.red("Invalid input: you must enter a whole number, e.g. 7\n");

                bottomRightY = -1;
            }

            keyboard.nextLine();
        } while (bottomRightY == -1);

        checkObstacleInsertionScreen();
    }

    /**
     * Invariants for {@link ObstacleInsertionScreen}
     */
    private void checkObstacleInsertionScreen(){
        Preconditions.checkNotNull(mapManager, "mapManager cannot be null");
        Preconditions.checkNotNull(keyboard, "keyboard cannot be null");
    }
}
