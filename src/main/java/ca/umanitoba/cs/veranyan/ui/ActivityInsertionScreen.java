package ca.umanitoba.cs.veranyan.ui;

import ca.umanitoba.cs.veranyan.logic.*;
import ca.umanitoba.cs.veranyan.model.exceptions.DuplicateActivityException;
import ca.umanitoba.cs.veranyan.model.exceptions.InvalidDurationException;
import ca.umanitoba.cs.veranyan.model.exceptions.InvalidTimeRangeException;
import ca.umanitoba.cs.veranyan.model.Activity;
import ca.umanitoba.cs.veranyan.model.map.Route;
import ca.umanitoba.cs.veranyan.output.Colourise;
import com.github.lalyos.jfiglet.FigletFont;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.time.Year;
import java.time.YearMonth;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * The {@link ActivityInsertionScreen} class manages the activities of the logged in {@link ca.umanitoba.cs.veranyan.model.Profile}.
 */
public class ActivityInsertionScreen {
    private static final int QUIT = 0;

    private static final int INSERT = 1;
    private static final int SELECT = 2;

    private final ProfileRegistry profileRegistry;
    private final MapManager mapManager;
    private final RouteManager routeManager;
    private final Scanner keyboard;

    public ActivityInsertionScreen(ProfileRegistry profileRegistry, MapManager mapManager, RouteManager routeManager, Scanner scanner){
        this.profileRegistry = profileRegistry;
        this.mapManager = mapManager;
        this.routeManager = routeManager;
        this.keyboard = scanner;

        checkActivityInsertionScreen();
    }

    /**
     * Starts the flow of recording a new {@link Activity}.
     */
    public void startInsert() {
        checkActivityInsertionScreen();

        if(profileRegistry.getCurrentProfile().getGears().isEmpty()) {
            System.out.println("Add gear first in order to add an activity.\n");
        }
        else {
            var activityBuilder = new Activity.ActivityBuilder();
            int choice;

            try {
                System.out.println(FigletFont.convertOneLine("ACTIVITY EDITOR"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // select gear
            var gear = new GearEditorScreen(profileRegistry, keyboard).gearSelectionScreen();
            activityBuilder.gear(gear);

            // insert, select, or find route
            Route route = promptRouteConstruction();
            activityBuilder.route(route);

            // activities for the same profile must have different start dates
            promptDateDuration(activityBuilder);

            // obstacle insertion
            choice = promptObstacleInsertionChoice();
            if (choice == INSERT)
                new ObstacleInsertionScreen(
                        mapManager,
                        keyboard
                ).startInsert();
        }

        checkActivityInsertionScreen();
    }

    /**
     * Prompts the user to either create, select, or find {@link Route}.
     * @return the {@link Route} created or selected. Must not be {@code null}.
     */
    private Route promptRouteConstruction() {
        checkActivityInsertionScreen();

        Route route = null;

        int choice;
        var routeEditorDisplay = new RouteEditorScreen(profileRegistry, mapManager, routeManager, keyboard);

        do {
            choice = promptRouteConstructionChoice();

            switch (choice) {
                case INSERT -> route = routeEditorDisplay.routeInsertionScreen();
                case SELECT -> route = routeEditorDisplay.routeSelectionScreen().orElse(null);
            }
        } while (route == null);

        checkActivityInsertionScreen();

        return route;
    }

    /**
     * Prompts the user to provide the start date and duration of the {@link Activity},
     * then builds the {@link Activity} and adds to the {@link ca.umanitoba.cs.veranyan.model.Profile}
     *
     * @param builder the builder of {@link Activity} for which to prompt the start date
     */
    private void promptDateDuration(Activity.ActivityBuilder builder) {
        boolean isUniqueStartDate;
        do {
            try {
                // prompting start date and duration
                startDateInsertionScreen(builder);
                durationInsertionScreen(builder);

                profileRegistry.addActivity(builder.build());

                isUniqueStartDate = true;
            } catch (DuplicateActivityException e) {
                Colourise.red("A duplicate activity cannot be added.\n");
                Colourise.red("You already had an activity at the same start date.\n");
                Colourise.red("A valid activity is one that does not have any other activity with same start date.\n");

                isUniqueStartDate = false;
            }
        } while (!isUniqueStartDate);
    }

    /**
     * Prompts the user to provide the start date of the {@link Activity}
     * @param builder the builder of {@link Activity} for which to prompt the start date
     */
    private void startDateInsertionScreen(Activity.ActivityBuilder builder){
        checkActivityInsertionScreen();

        int monthNumber = monthNumberInsertionScreen(builder);
        dayOfMonthInsertionScreen(builder, monthNumber);
        startHourInsertionScreen(builder);
        startMinuteInsertionScreen(builder);

        checkActivityInsertionScreen();
    }

    /**
     * Prompts the user to provide the start minute of the {@link Activity}
     * @param builder the builder of {@link Activity} for which to prompt the start date
     */
    private void startMinuteInsertionScreen(Activity.ActivityBuilder builder) {
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
            } catch (InputMismatchException e) {
                Colourise.red("Invalid input: you must enter a whole number, e.g. 12\n");

                startMinute = -1;
            }

            keyboard.nextLine();
        } while (startMinute == -1);

        checkActivityInsertionScreen();
    }

    /**
     * Prompts the user to provide the start hour of the {@link Activity}
     * @param builder the builder of {@link Activity} for which to prompt the start hour
     */
    private void startHourInsertionScreen(Activity.ActivityBuilder builder) {
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
            } catch (InputMismatchException e) {
                Colourise.red("Invalid input: you must enter a whole number, e.g. 12\n");

                startHour = -1;
            }

            keyboard.nextLine();
        } while (startHour == -1);

        checkActivityInsertionScreen();
    }

    /**
     * Prompts the user to provide the start day of month of the {@link Activity}
     * @param builder the builder of {@link Activity} for which to prompt the start day of month
     * @param monthNumber the month whose day is being prompted
     */
    private void dayOfMonthInsertionScreen(Activity.ActivityBuilder builder, int monthNumber) {
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
                Colourise.red("Invalid input: you must enter a whole number, e.g. 12\n");

                dayOfMonth = -1;
            }

            keyboard.nextLine();
        } while (dayOfMonth == -1);

        checkActivityInsertionScreen();
    }

    /**
     * Prompts the user to provide the start month number of the {@link Activity}
     * @param builder the builder of {@link Activity} for which to prompt start month number
     * @return the entered month number
     */
    private int monthNumberInsertionScreen(Activity.ActivityBuilder builder) {
        checkActivityInsertionScreen();

        int monthNumber = 0;

        do {
            Colourise.cyan("Enter the month of the start of the activity (as a number from 1 to 12 inclusive): ");
            try {
                monthNumber = keyboard.nextInt();
                builder.startMonth(monthNumber);
            } catch (InvalidTimeRangeException e) {
                Colourise.red(monthNumber + " is not a valid month number. A valid month number is a whole number from 1 to 12, e.g. 7.\n");

                monthNumber = -1;
            } catch (InputMismatchException e) {
                Colourise.red("Invalid input: you must enter a whole number, e.g. 12\n");

                monthNumber = -1;
            }

            keyboard.nextLine();
        } while (monthNumber == -1);

        checkActivityInsertionScreen();

        return monthNumber;
    }

    /**
     * Prompts the user to provide the duration of an {@link Activity}
     * @param builder the builder of {@link Activity} for which to prompt duration
     */
    private void durationInsertionScreen(Activity.ActivityBuilder builder){
        checkActivityInsertionScreen();

        int durationInMinutes = -1;

        do {
            Colourise.cyan("Enter duration of activity (in minutes): ");
            try{
                durationInMinutes = keyboard.nextInt();
                builder.durationInMinutes(durationInMinutes);
            } catch (InvalidDurationException e) {
                Colourise.red(durationInMinutes + "is not a valid duration. Duration in minutes must be a whole number from 1 to " + Activity.ActivityBuilder.MAX_DURATION + "\n");

                durationInMinutes = -1;
            } catch (InputMismatchException e) {
                Colourise.red("Invalid input: you must enter a whole number, e.g. 12\n");

                durationInMinutes = -1;
            }

            keyboard.nextLine();

        } while (durationInMinutes == -1);

        checkActivityInsertionScreen();
    }

    /**
     * Prompts the user to select an option of how to construct a {@link Route}.
     * @return the option selected.
     */
    private int promptRouteConstructionChoice(){
        checkActivityInsertionScreen();

        System.out.println(INSERT + ". Create a new route");
        System.out.println(SELECT + ". Select from my previous routes");

        int choice;
        do {
            try {
                Colourise.cyan("Select one of the above options by number: ");
                choice = keyboard.nextInt();

                if(choice != INSERT && choice != SELECT) {
                    Colourise.red(String.format(
                        """
                        %d choice is not a valid option.
                        You must enter a number that corresponds to these options:
                        %d. Create a new route
                        %d. Select from my previous routes
                        Valid inputs are: %d, %d.
                        %n""", choice, INSERT, SELECT, INSERT, SELECT)
                    );

                    choice = -1;
                }
            } catch (InputMismatchException e) {
                Colourise.red("Invalid input: you must enter a whole number, e.g. 12\n");

                choice = -1;
            }

            keyboard.nextLine();
        } while(choice == -1);

        checkActivityInsertionScreen();

        return choice;
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

                if(choice != INSERT && choice != QUIT) {
                    Colourise.red(String.format(
                            """
                            %d choice is not a valid option.
                            You must enter a number that corresponds to these options:
                            %d. add an obstacle
                            %d. skip
                            Valid inputs are: %d, %d.
                            %n""", choice, INSERT, QUIT, INSERT, QUIT)
                    );

                    choice = -1;
                }
            } catch (final InputMismatchException e){
                Colourise.red("Invalid input: you must enter a whole number, e.g. 12\n");

                choice = -1;
            }

            keyboard.nextLine();
        } while(choice != INSERT && choice != QUIT);

        checkActivityInsertionScreen();

        return choice;
    }

    /**
     * Class invariants for {@link ActivityInsertionScreen}
     */
    private void checkActivityInsertionScreen(){
        Preconditions.checkNotNull(profileRegistry, "profileRegistry cannot be null");
        Preconditions.checkNotNull(mapManager, "activityManager cannot be null");
        Preconditions.checkNotNull(keyboard, "keyboard cannot be null");
    }
}
