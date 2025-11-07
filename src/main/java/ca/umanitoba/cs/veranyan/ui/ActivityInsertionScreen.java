package ca.umanitoba.cs.veranyan.ui;

import ca.umanitoba.cs.veranyan.logic.*;
import ca.umanitoba.cs.veranyan.model.Activity;
import ca.umanitoba.cs.veranyan.model.exceptions.CoordinateOutOfBoundsException;
import ca.umanitoba.cs.veranyan.model.exceptions.DuplicateActivityException;
import ca.umanitoba.cs.veranyan.model.exceptions.InvalidTimeRangeException;
import ca.umanitoba.cs.veranyan.model.exceptions.RouteObstacleOverlapException;
import ca.umanitoba.cs.veranyan.model.gear.Gear;
import ca.umanitoba.cs.veranyan.model.map.Map;
import ca.umanitoba.cs.veranyan.model.map.Obstacle;
import ca.umanitoba.cs.veranyan.model.map.Route;
import ca.umanitoba.cs.veranyan.output.Colourise;
import ca.umanitoba.cs.veranyan.output.MapPrinter;
import com.github.lalyos.jfiglet.FigletFont;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.time.Year;
import java.time.YearMonth;
import java.util.InputMismatchException;
import java.util.Optional;
import java.util.Scanner;

/**
 * The {@code ActivityInsertionScreen} class manages the activities of the logged in profile.
 */
public class ActivityInsertionScreen {
    private static final int QUIT = 0;

    private static final int INSERT = 1;
    private static final int FIND_ROUTE = 2;
    private static final int SELECT = 3;

    private static final int UP = 1;
    private static final int RIGHT = 2;
    private static final int DOWN = 3;
    private static final int LEFT = 4;

    private final ProfileRegistry profileRegistry;
    private final MapManager mapManager;
    private final Scanner keyboard;

    public ActivityInsertionScreen(ProfileRegistry profileRegistry, MapManager mapManager){
        this.profileRegistry = profileRegistry;
        this.mapManager = mapManager;
        this.keyboard = new Scanner(System.in);

        checkActivityInsertionScreen();
    }

    /**
     * Starts the flow of recording a new activity.
     */
    public void startRecord() {
        checkActivityInsertionScreen();

        var activityBuilder = new Activity.ActivityBuilder();
        int choice;
        Map.ProcessedRoute route = null;

        try {
            System.out.println(FigletFont.convertOneLine("ACTIVITY EDITOR"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // insert or select gear
        activityBuilder.gear(
                promptGear()
        );

        // insert or select route
        do {
            choice = promptRouteConstructionChoice();

            switch (choice) {
                case INSERT -> {
                    route = routeInsertionScreen();

                    // prompt to build the route
                    moveInsertionScreen(route);
                }
                case FIND_ROUTE -> {

                    Optional<Map.ProcessedRoute> found = new PathFindingScreen(
                            new PathFinder(profileRegistry.getCurrentProfile(), mapManager.getMap())
                    ).startFind();

                    route = found.orElse(null);
                }
                case SELECT -> {
                    if (profileRegistry.getCurrentProfile().getRoutes().isEmpty()) {
                        Colourise.red("There are no previous routes to select from.\n\n");
                    } else {
                        route = routeSelectionScreen();
                    }
                }
            }
        } while (route == null);

        activityBuilder.route(route);

        // activities for the same profile must have different start dates
        boolean isUniqueStartDate;
        do {
            try {
                // prompting start date and duration
                startDateInsertionScreen(activityBuilder);
                durationInsertionScreen(activityBuilder);

                profileRegistry.addActivity(activityBuilder.build());

                isUniqueStartDate = true;
            } catch (DuplicateActivityException e) {
                Colourise.red("A duplicate activity cannot be added.\n");
                Colourise.red("You already had an activity at the same start date.\n");
                Colourise.red("A valid activity is one that does not have any other activity with same start date.\n");

                isUniqueStartDate = false;
            }
        } while (!isUniqueStartDate);

        // obstacle insertion
        choice = promptObstacleInsertionChoice();
        if(choice == INSERT)
            obstacleInsertionScreen();

        checkActivityInsertionScreen();
    }

    /**
     * Prompts the user to either create or select an existing gear.
     * @return the Gear created or selected. Must not be {@code null}.
     */
    private Gear promptGear() {
        checkActivityInsertionScreen();

        Gear gear;

        if (profileRegistry.getCurrentProfile().hasGear()) {
            gear = new GearEditorDisplay(
                    new GearManager(profileRegistry.getCurrentProfile())
            ).gearSelectionScreen();
        } else {
            gear = new GearEditorDisplay(
                    new GearManager(profileRegistry.getCurrentProfile())
            ).gearInsertionScreen();
        }

        checkActivityInsertionScreen();

        return gear;
    }

    /**
     * Prompts the user to provide the start date of the activity
     * @param builder the builder of activity for which to prompt the start date
     */
    private void startDateInsertionScreen(Activity.ActivityBuilder builder){
        checkActivityInsertionScreen();

        int monthNumber = getMonthNumber(builder);
        dayOfMonth(builder, monthNumber);
        startHour(builder);
        startMinute(builder);

        checkActivityInsertionScreen();
    }

    /**
     * Prompts the user to provide the start minute of the activity
     * @param builder the builder of activity for which to prompt the start date
     */
    private void startMinute(Activity.ActivityBuilder builder) {
        checkActivityInsertionScreen();

        int startMinute = -1;

        do {
            Colourise.cyan("Enter the start minute (from 0 to 59 inclusive): ");
            try {
                startMinute = keyboard.nextInt();
                builder.startMinute(startMinute);
            } catch (InvalidTimeRangeException e) {
                Colourise.red(startMinute + "is not a valid minute. A valid minute is a whole number from 0 to 59, e.g, 12\n");

                startMinute = -1;
            } catch (RuntimeException e) {
                Colourise.red("a minute must be a number, e.g. 12\n");

                startMinute = -1;
            }

            keyboard.nextLine();
        } while (startMinute == -1);

        checkActivityInsertionScreen();
    }

    /**
     * Prompts the user to provide the start hour of the activity
     * @param builder the builder of activity for which to prompt the start hour
     */
    private void startHour(Activity.ActivityBuilder builder) {
        checkActivityInsertionScreen();

        int startHour = -1;

        do {
            Colourise.cyan("Enter the start hour (from 0 to 23 inclusive): ");
            try {
                startHour = keyboard.nextInt();
                builder.startHour(startHour);
            } catch (InvalidTimeRangeException e) {
                Colourise.red(startHour + "is not a valid hour. A valid hour is a whole number from 0 to 23, e.g, 12\n");

                startHour = -1;
            } catch (RuntimeException e) {
                Colourise.red("an hour must be a number, e.g. 12\n");

                startHour = -1;
            }

            keyboard.nextLine();
        } while (startHour == -1);

        checkActivityInsertionScreen();
    }

    /**
     * Prompts the user to provide the start day of month of the activity
     * @param builder the builder of activity for which to prompt the start day of month
     */
    private void dayOfMonth(Activity.ActivityBuilder builder, int monthNumber) {
        checkActivityInsertionScreen();

        int dayOfMonth = 0;

        do {
            var yearMonth = YearMonth.of(Year.now().getValue(), monthNumber);
            Colourise.cyan(
                    "Enter the day of the start of activity in " +
                            yearMonth.getMonth() + " in range range 1-" +
                            yearMonth.getMonth().length(yearMonth.isLeapYear()) + "inclusive: "
            );
            try {
                dayOfMonth = keyboard.nextInt();
                builder.startDayOfMonth(dayOfMonth);
            } catch (InvalidTimeRangeException e) {
                Colourise.red(String.format(
                        "%d is not a valid day in %s. A valid day of %s is a whole number from 1 to %d, e.g. 7.%n",
                        dayOfMonth, yearMonth.getMonth().toString(), yearMonth.getMonth().toString(),
                        yearMonth.getMonth().length(yearMonth.isLeapYear())
                ));

                dayOfMonth = -1;
            } catch (InputMismatchException e) {
                Colourise.red("A day in month must be a number, e.g. 7\n");
                dayOfMonth = -1;
            }

            keyboard.nextLine();
        } while (dayOfMonth == -1);

        checkActivityInsertionScreen();
    }

    /**
     * Prompts the user to provide the start month number of the activity
     * @param builder the builder of activity for which to prompt start month number
     */
    private int getMonthNumber(Activity.ActivityBuilder builder) {
        checkActivityInsertionScreen();

        int monthNumber = -1;

        do {
            Colourise.cyan("Enter the month of the start of the activity (as a number from 1 to 12 inclusive): ");
            try {
                monthNumber = keyboard.nextInt();
                builder.startMonth(monthNumber);
            } catch (InvalidTimeRangeException e) {
                Colourise.red(monthNumber + " is not a valid month number. A valid month number is a whole number from 1 to 12, e.g. 7.\n");

                monthNumber = -1;
            } catch (RuntimeException e) {
                Colourise.red("A month number must be a number, e.g. 7");
                monthNumber = -1;
            }

            keyboard.nextLine();
        } while (monthNumber == -1);

        checkActivityInsertionScreen();

        return monthNumber;
    }

    /**
     * Prompts the user to provide the duration of an activity
     * @param builder the builder of activity for which to prompt duration
     */
    private void durationInsertionScreen(Activity.ActivityBuilder builder){
        checkActivityInsertionScreen();

        int durationInMinutes = -1;

        do {
            Colourise.cyan("Enter duration of activity (in minutes): ");
            try{
                durationInMinutes = keyboard.nextInt();
                builder.durationInMinutes(durationInMinutes);
            } catch (InvalidTimeRangeException e) {
                Colourise.red(durationInMinutes + "is not a valid duration. Duration in minutes must be a whole number from 1 to 6000, e.g. 44\n");

                durationInMinutes = -1;
            } catch (RuntimeException e) {
                Colourise.red("Duration must be a number, e.g. 44\n");

                durationInMinutes = -1;
            }
        } while (durationInMinutes == -1);

        checkActivityInsertionScreen();
    }

    /**
     * Prompts the user to select an option of how to construct a route.
     * @return the option selected.
     */
    private int promptRouteConstructionChoice(){
        checkActivityInsertionScreen();

        System.out.println(INSERT + ". Create a new route");
        System.out.println(FIND_ROUTE + ". Find a route (automatically constructs a route for you)");
        System.out.println(SELECT + ". Select from my previous routes");

        int choice;
        do {
            try {
                Colourise.cyan("Select one of the above options by number: ");
                choice = keyboard.nextInt();

                if(choice != INSERT && choice != FIND_ROUTE && choice != SELECT)
                    choice = -1;

            } catch (Exception e) {
                choice = -1;
            }

            keyboard.nextLine();
            if(choice == -1) {
                Colourise.red(String.format(
                        """
                        You must enter a number that corresponds to these options:
                        %d. Create a new route
                        %d. Find a route (automatically constructs a route for you)
                        %d. Select from my previous routes
                        Valid inputs are: %d, %d, %d.
                        %n""", INSERT, FIND_ROUTE, SELECT, INSERT, FIND_ROUTE, SELECT));
            }
        } while(choice == -1);

        checkActivityInsertionScreen();

        return choice;
    }

    /**
     * Prompts the user to create a new route
     * @return the route created.
     */
    private Map.ProcessedRoute routeInsertionScreen() {
        checkActivityInsertionScreen();

        mapManager.getMap().clearRoutes();
        new MapPrinter(mapManager.getMap()).print();
        Route route;
        Map.ProcessedRoute processedRoute;

        int x = -1;
        int y = -1;

        // x
        do {
            var builder = new Route.RouteBuilder();

            do {
                Colourise.cyan("Enter upper-left point x-coordinate: ");
                try {
                    x = keyboard.nextInt();
                    builder.x(x);
                } catch (CoordinateOutOfBoundsException e) {
                    Colourise.red("x-coordinate " + x + " is out of bounds of a map with length " + mapManager.getMap().getLength() + ".\n");
                    Colourise.red("valid values for an x-coordinate are whole numbers from 1 to " + mapManager.getMap().getLength() +
                            " inclusive, e.g. " + (mapManager.getMap().getLength()));
                    x = -1;
                } catch (Exception e) {
                    Colourise.red("an x-coordinate must be a whole number, e.g. 7");
                }

                keyboard.nextLine();
            } while (x == -1);

            // y
            do {
                Colourise.cyan("Enter upper-left point y-coordinate: ");
                try {
                    y = keyboard.nextInt();
                    builder.y(y);
                } catch (CoordinateOutOfBoundsException e) {
                    Colourise.red("y-coordinate " + y + " is out of bounds of a map with width " + mapManager.getMap().getWidth() + ".\n");
                    Colourise.red("valid values for a y-coordinate are whole numbers from 1 to " + mapManager.getMap().getWidth() +
                            " inclusive, e.g. " + (mapManager.getMap().getWidth()));
                    y = -1;
                } catch (Exception e) {
                    Colourise.red("a y-coordinate must be a whole number, e.g. 7");
                }

                keyboard.nextLine();
            } while (y == -1);

            route = builder.build();

            try {
                processedRoute = mapManager.addRoute(route);
            } catch (RouteObstacleOverlapException e) {
                Colourise.red("The route you wanted to add overlaps with an obstacle.\n");
                Colourise.red("A valid route is one that does not pass through an obstacle\n");

                processedRoute = null;
            } catch (CoordinateOutOfBoundsException e) {
                Colourise.red("The route you wanted to add is out of map boundaries.\n");
                Colourise.red("A valid route is one that stays within map boundaries.\n");

                processedRoute = null;
            }
        } while(processedRoute == null);

        checkActivityInsertionScreen();

        return processedRoute;
    }

    /**
     * Prompts the user to insert the move operations on a route.
     * @param route the route to be moved in.
     */
    private void moveInsertionScreen(Map.ProcessedRoute route){
        checkActivityInsertionScreen();

        mapManager.setUpRoute(route);
        var mapPrinter = new MapPrinter(mapManager.getMap());

        int choice;
        do{
            Colourise.cyan("Enter " + QUIT + " to end route or any other number to proceed: ");
            try{
                choice = keyboard.nextInt();
            } catch (final Exception e){
                choice = -1;
            }

            keyboard.nextLine();
            if(choice == -1)
                Colourise.red("Invalid input. valid input must be a whole number, e.g. 1.\n");
        } while (choice == -1);

        if(choice != QUIT) // initial printing
            mapPrinter.print();

        while (choice != QUIT){
            System.out.println("Directions: ");
            System.out.println( UP + ". UP");
            System.out.println( RIGHT + ". RIGHT");
            System.out.println( DOWN + ". DOWN");
            System.out.println( LEFT + ". LEFT");

            int direction = getDirection(choice);
            int numSteps = getNumSteps();

            try{
                mapManager.doMove(route, direction, numSteps);
            } catch (RouteObstacleOverlapException e){
                Colourise.red("Cannot move in " + numSteps + " steps in that direction because route will overlap with an obstacle.\n");
            } catch (CoordinateOutOfBoundsException e) {
                Colourise.red("Cannot move in " + numSteps + " steps in that direction because route will go out of map bounds.\n");
            }

            // updating the route to display
            // no need to remove the route from map since exit upon this method results in valid route
            mapManager.setUpRoute(route);
            mapPrinter.print();

            do{
                Colourise.cyan("Enter " + QUIT + " to end route or any other number to proceed: ");
                try{
                    choice = keyboard.nextInt();
                } catch (final Exception e){
                    choice = -1;
                }

                keyboard.nextLine();
                if(choice == -1)
                    Colourise.red("Invalid input. You must provide a number, e.g. 10\n");
            } while (choice == -1);
        }

        checkActivityInsertionScreen();
    }

    /**
     * Prompts the user to provide the number of steps for a single move operation on a route
     * @return the selected number of steps
     */
    private int getNumSteps() {
        checkActivityInsertionScreen();

        int numSteps;
        do {
            Colourise.cyan("Enter number of steps: ");
            try {
                numSteps = keyboard.nextInt();
            } catch (Exception e) {
                numSteps = -1;
            }

            keyboard.nextLine();
            if (numSteps <= 0) {
                Colourise.red(numSteps + " is not a valid number of steps.\n");
                Colourise.red("A valid number of steps is a whole number greater than 0, e.g. 3\n");
            }
        } while (numSteps < 0);

        checkActivityInsertionScreen();

        return numSteps;
    }

    /**
     * Prompts the user to provide the direction for a single move operation on a route
     * @return the selected direction
     */
    private int getDirection(int choice) {
        checkActivityInsertionScreen();

        int direction;
        do {
            Colourise.cyan("Select one of the above options by number: ");
            try {
                direction = keyboard.nextInt();
                if (direction != UP && direction != RIGHT && direction != DOWN && direction != LEFT)
                    choice = -1;
            } catch (Exception e) {
                direction = -1;
            }

            keyboard.nextLine();
            if (choice == -1)
                Colourise.red(String.format(
                        """
                        You must enter a number that corresponds to these options:
                        %d. UP
                        %d. RIGHT
                        %d DOWN
                        %d LEFT
                        Valid inputs are: %d, %d, %d, %d.
                        %n""", UP, RIGHT, DOWN, LEFT, UP, RIGHT, DOWN, LEFT));
        } while (direction != UP && direction != RIGHT && direction != DOWN && direction != LEFT);

        checkActivityInsertionScreen();

        return direction;
    }

    /**
     * Prompts the user to select an existing route from their own routes
     * @return the selected route
     */
    private Map.ProcessedRoute routeSelectionScreen(){
        checkActivityInsertionScreen();

        int i = 1;
        for(var route : profileRegistry.getCurrentProfile().getRoutes()) {
            System.out.println("Route #" + i + ":");

            mapManager.setUpRoute(route);
            new MapPrinter(mapManager.getMap()).print();
        }

        int choice;
        do{
            Colourise.cyan("Select one of the above routes by number: ");
            try{
                choice = keyboard.nextInt();
            } catch (final Exception e){
                choice = 0;
            }

            keyboard.nextLine();
            if(choice < 1 || choice > profileRegistry.getCurrentProfile().getRoutes().size()){
                Colourise.red("input does not match with any route.\n");
                Colourise.red("A valid input is a whole number that matches with a route. Valid inputs are:\n");
            }
        } while (choice < 1 || choice > profileRegistry.getCurrentProfile().getRoutes().size());

        checkActivityInsertionScreen();

        return profileRegistry.getCurrentProfile().getRoutes().get(choice-1).clone();
    }

    /**
     * Prompts the user to choose to either create an obstacle on the map or not.
     * @return the user choice.
     */
    private int promptObstacleInsertionChoice(){
        checkActivityInsertionScreen();

        int choice;
        do{
            try{
                Colourise.cyan("Enter " + INSERT + " to add an obstacle or " + QUIT + " to skip: ");
                choice = keyboard.nextInt();
            } catch (final Exception e){
                choice = -1;
            }

            keyboard.nextLine();
            if(choice != INSERT && choice != QUIT)
                Colourise.red("Invalid input. Valid inputs are: " + INSERT + " and " + QUIT + ".\n");
        } while(choice != INSERT && choice != QUIT);

        checkActivityInsertionScreen();

        return choice;
    }

    /**
     * Prompts the user to create an obstacle on the map.
     */
    private void obstacleInsertionScreen(){
        checkActivityInsertionScreen();
        Obstacle obstacle;

        do {
            var obstacleBuilder = new Obstacle.ObstacleBuilder();

            int topLeftX = -1;
            int topLeftY = -1;
            int bottomRightX = -1;
            int bottomRightY = -1;


            do {
                Colourise.cyan("Enter upper-left point x-coordinate: ");
                try {
                    topLeftX = keyboard.nextInt();
                    obstacleBuilder.topLeftX(topLeftX);
                } catch (CoordinateOutOfBoundsException e) {
                    Colourise.red(topLeftX + " is not a valid x-coordinate.\n");
                    Colourise.red("valid values for an x-coordinate are whole numbers from 1 to " + mapManager.getMap().getLength() +
                            " inclusive, e.g. " + (mapManager.getMap().getLength()));

                    topLeftX = -1;
                } catch (Exception e) {
                    Colourise.red("an x-coordinate must be a whole number, e.g. 7");
                    topLeftX = -1;
                }

                keyboard.nextLine();
            } while (topLeftX == -1);

            // topLeftY
            do {
                Colourise.cyan("Enter upper-left point y-coordinate: ");
                try {
                    topLeftY = keyboard.nextInt();
                    obstacleBuilder.topLeftY(topLeftY);
                } catch (CoordinateOutOfBoundsException e) {
                    Colourise.red(topLeftY + " is not a valid y-coordinate.\n");
                    Colourise.red("valid values for a y-coordinate are whole numbers from 1 to " + mapManager.getMap().getWidth() +
                            " inclusive, e.g. " + (mapManager.getMap().getWidth()));
                    topLeftY = -1;
                } catch (Exception e) {
                    Colourise.red("a y-coordinate must be a whole number, e.g. 7");
                    topLeftY = -1;
                }

                keyboard.nextLine();
            } while (topLeftY == -1);

            // bottomRightY
            do {
                Colourise.cyan("Enter bottom-right point x-coordinate: ");
                try {
                    bottomRightX = keyboard.nextInt();
                    obstacleBuilder.bottomRightX(bottomRightX);
                } catch (CoordinateOutOfBoundsException e) {
                    Colourise.red(bottomRightX + " is not a valid x-coordinate.\n");
                    Colourise.red("valid values for a x-coordinate are whole numbers from 1 to " + mapManager.getMap().getLength() +
                            " inclusive, e.g. " + (mapManager.getMap().getLength()));
                    bottomRightX = -1;
                } catch (Exception e) {
                    Colourise.red("an x-coordinate must be a whole number, e.g. 7");
                    bottomRightX = -1;
                }

                keyboard.nextLine();
            } while (bottomRightX == -1);

            // bottomRightY
            do {
                Colourise.cyan("Enter bottom-right point y-coordinate: ");
                try {
                    bottomRightY = keyboard.nextInt();
                    obstacleBuilder.bottomRightY(bottomRightY);
                } catch (CoordinateOutOfBoundsException e) {
                    Colourise.red(bottomRightY + " is not a valid y-coordinate.\n");
                    Colourise.red("valid values for a y-coordinate are whole numbers from 1 to " + mapManager.getMap().getWidth() +
                            " inclusive, e.g. " + (mapManager.getMap().getWidth()));
                    bottomRightY = -1;
                } catch (Exception e) {
                    Colourise.red("a y-coordinate must be a whole number, e.g. 7");
                    bottomRightY = -1;
                }

                keyboard.nextLine();
            } while (bottomRightY == -1);

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
            }
        } while (obstacle == null);

        checkActivityInsertionScreen();
    }

    /**
     * Class invariants for checkActivityInsertionScreen
     */
    private void checkActivityInsertionScreen(){
        Preconditions.checkNotNull(profileRegistry, "profileRegistry cannot be null");
        Preconditions.checkNotNull(mapManager, "activityManager cannot be null");
        Preconditions.checkNotNull(keyboard, "keyboard cannot be null");
    }
}
