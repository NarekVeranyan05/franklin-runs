package ca.umanitoba.cs.veranyan.ui;

import ca.umanitoba.cs.veranyan.logic.MapManager;
import ca.umanitoba.cs.veranyan.logic.PathFinder;
import ca.umanitoba.cs.veranyan.logic.ProfileRegistry;
import ca.umanitoba.cs.veranyan.logic.RouteManager;
import ca.umanitoba.cs.veranyan.output.Colourise;
import com.github.lalyos.jfiglet.FigletFont;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * The main menu of the exercise management system.
 */
public class MainMenu {
    private static final int QUIT = 0;
    private static final int ENTER = 1;

    private static final int UPDATE_PROFILE = 1;
    private static final int FIND_ROUTE = 2;
    private static final int ADD_ACTIVITY = 3;
    private static final int FEED = 4;

    private final ProfileRegistry profileRegistry;
    private final MapManager mapManager;
    private final RouteManager routeManager;
    private final Scanner keyboard;

    public MainMenu(ProfileRegistry profileRegistry, MapManager mapManager, RouteManager routeManager, Scanner scanner){
        this.profileRegistry = profileRegistry;
        this.mapManager = mapManager;
        this.routeManager = routeManager;
        this.keyboard = scanner;

        checkMainMenu();
    }

    /**
     * Launches the exercise management system
     */
    public void startProgram(){
        checkMainMenu();

        int enterOrExit;

        do {
            System.out.println();
            enterOrExit = promptEntrance();
            System.out.println();

            if (enterOrExit == ENTER) {
                new LoginScreen(profileRegistry, keyboard).startLogin(); // guaranteed logging into profile

                // already logged in, may display main menu
                System.out.println();
                displayMainMenu();
            }
        } while(enterOrExit != QUIT);

        checkMainMenu();
    }

    /**
     * Prints the main menu and prompts the user for menu choice
     */
    public void displayMainMenu(){
        checkMainMenu();

        int menuChoice;

        do {
            menuChoice = promptMenuOption();

            switch (menuChoice){
                case UPDATE_PROFILE -> {
                    System.out.println();
                    new ProfileEditorScreen(profileRegistry, keyboard).startUpdate();
                }
                case FIND_ROUTE -> {
                    new PathFindingScreen(
                        new PathFinder(profileRegistry.getCurrentProfile(), mapManager.getMap()),
                        keyboard
                    ).startFind();
                }
                case ADD_ACTIVITY -> {
                    System.out.println();
                    new ActivityInsertionScreen(
                            profileRegistry,
                            mapManager,
                            routeManager,
                            keyboard
                    ).startInsert();
                }
                case FEED -> {
                    System.out.println();
                    new FeedScreen(
                            profileRegistry,
                            mapManager,
                            keyboard
                    ).displayFeed();
                }
            }
        } while (menuChoice != QUIT);

        profileRegistry.unloadProfile(); // logging out upon quitting

        checkMainMenu();
    }

    /**
     * Prompts the user for menu choice
     * @return the choice of the user to which menu option to hit
     */
    public int promptMenuOption() {
        checkMainMenu();

        try {
            System.out.println(FigletFont.convertOneLine("Main Menu"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int choice;
        do{
            System.out.println(QUIT + ". Log out");
            System.out.println(UPDATE_PROFILE + ". Update Profile");
            System.out.println(FIND_ROUTE + ". Find Route");
            System.out.println(ADD_ACTIVITY + ". Add Activity");
            System.out.println(FEED + ". Display Feed");

            Colourise.cyan("Select one of the above options by number: ");
            try {
                choice = keyboard.nextInt();

                if(choice != QUIT && choice != UPDATE_PROFILE && choice != FIND_ROUTE && choice != ADD_ACTIVITY && choice != FEED){
                    Colourise.red(String.format(
                            """
                            %d is not a valid menu option.
                            You must enter a number that corresponds to these options:
                            %d. Log out
                            %d. Update Profile
                            %d. Find Route
                            %d. Add Activity
                            %d. Display Feed
                            Valid inputs are: %d, %d, %d, %d, and %d.
                            %n""", choice, QUIT, UPDATE_PROFILE, FIND_ROUTE, ADD_ACTIVITY, FEED, QUIT, UPDATE_PROFILE, FIND_ROUTE, ADD_ACTIVITY, FEED)
                    );

                    choice = -1;
                }
            } catch (InputMismatchException e) {
                Colourise.red("A valid menu choice should be a whole number, e.g. 1\n");

                choice = -1;
            }

            keyboard.nextLine();
        } while(choice == -1);

        checkMainMenu();

        return choice;
    }

    /**
     * Prompts the user to choose to either enter or quit the system.
     * @return the choice of the user
     */
    public int promptEntrance(){
        checkMainMenu();

        try {
            System.out.println(FigletFont.convertOneLine("Track-Me-Riding"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Welcome. Choose one of the following options:");
        System.out.println(QUIT + ". Quit system");
        System.out.println(ENTER + ". Enter the system");

        int choice;
        do {
            Colourise.cyan("Enter selected option number: ");
            try {
                choice = keyboard.nextInt();

                if(choice != QUIT && choice != ENTER) {
                    Colourise.red(String.format(
                            """
                            %d is not a valid option.
                            You must enter a number that corresponds to these options:
                            %d. Quit system
                            %d. Enter the system
                            Valid inputs are: %d and %d.
                            %n""", choice, QUIT, ENTER, QUIT, ENTER)
                    );

                    choice = -1;
                }
            } catch (final InputMismatchException e){
                Colourise.red("Invalid input: you must enter a whole number, e.g. 2\n");

                choice = -1;
            }

            keyboard.nextLine();

        } while(choice == -1);

        checkMainMenu();

        return choice;
    }

    /**
     * Class invariants for {@link MainMenu}
     */
    private void checkMainMenu(){
        Preconditions.checkNotNull(profileRegistry, "profileRegistry cannot be null");
        Preconditions.checkNotNull(mapManager, "mapManager cannot be null");
        Preconditions.checkNotNull(keyboard, "keyboard cannot be null");
    }
}
