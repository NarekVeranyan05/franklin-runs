package ca.umanitoba.cs.veranyan.ui;

import ca.umanitoba.cs.veranyan.model.exceptions.InvalidGearTypeException;
import ca.umanitoba.cs.veranyan.logic.MapManager;
import ca.umanitoba.cs.veranyan.logic.ProfileRegistry;
import ca.umanitoba.cs.veranyan.output.ActivityPrinter;
import ca.umanitoba.cs.veranyan.output.Colourise;
import ca.umanitoba.cs.veranyan.output.MapPrinter;
import com.github.lalyos.jfiglet.FigletFont;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * The {@link FeedScreen} class manages displaying the feed and changing filter types,
 */
public class FeedScreen {
    private static final ProfileRegistry.ActivityFilterType DEFAULT_FILTER = ProfileRegistry.ActivityFilterType.OWN_AND_FRIENDS;
    private static final int QUIT = 0;
    private static final int FILTER = 1;

    private final ProfileRegistry profileRegistry;
    private final MapManager mapManager;
    private final Scanner keyboard;

    public FeedScreen(ProfileRegistry profileRegistry, MapManager mapManager, Scanner scanner){
        this.profileRegistry = profileRegistry;
        this.mapManager = mapManager;
        this.keyboard = scanner;

        checkFeedDisplay();
    }

    /**
     * Displays the feed of activities. Prompts for moving to next or previous page and filtering
     * Default filter is for user's own activities and their friends' activities
     */
    public void displayFeed(){
        checkFeedDisplay();

        try {
            System.out.println(FigletFont.convertOneLine("Feed"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var type = DEFAULT_FILTER;
        var profileActivityPairs = profileRegistry.getActivities(type);

        int choice;
        do{
            if(profileActivityPairs.isEmpty())
                System.out.println("The feed of activities is empty.");
            else {
                for (var profileActivityPair : profileActivityPairs) {
                    var profileName = profileActivityPair.getFirst();
                    var activity = profileActivityPair.getSecond();

                    mapManager.setUpActivity(activity); // will print only this route

                    // printing profile name
                    System.out.print("Profile: " + profileName);
                    if(profileName.equals(profileRegistry.getCurrentProfile().getName()))
                        System.out.print(" (YOU)");
                    System.out.println();

                    // printing activity
                    new ActivityPrinter(activity).print();
                    System.out.println();

                    // printing route
                    new MapPrinter(mapManager.getMap()).print();

                    System.out.println("\n");
                }
            }

            choice = promptNavigationChoice();
            if(choice == FILTER){
                type = filterSelectionScreen();
                profileActivityPairs = profileRegistry.getActivities(type);
            }
        } while (choice != QUIT);

        checkFeedDisplay();
    }

    /**
     * Prompts the user to select a filter type.
     * @return the filter type selected.
     */
    private ProfileRegistry.ActivityFilterType filterSelectionScreen() {
        checkFeedDisplay();

        ProfileRegistry.ActivityFilterType filterType;
        String filterTypeInput = "";

        do{
            for(var type : Arrays.stream(
                    ProfileRegistry.ActivityFilterType.values()).
                    filter(val -> val != ProfileRegistry.ActivityFilterType.ALL).toList()
            )
            {
                System.out.println(" - " + type.toString());
            }

            Colourise.cyan("Enter filter by number: ");
            try {
                filterTypeInput = keyboard.nextLine().trim();

                if(!filterTypeInput.equalsIgnoreCase("all")) // FIXME how to test?
                    filterType = ProfileRegistry.ActivityFilterType.fromString(filterTypeInput);
                else {
                    filterType = null;
                }

            } catch (InvalidGearTypeException e) {
                filterType = null;
            }
            
            if(filterType == null)
                Colourise.red(filterTypeInput + " is an invalid filter name. Valid filters are one of below:\n");

                
        } while (filterType == null);

        checkFeedDisplay();

        return filterType;
    }

    /**
     * Prompts the user to either move to next page, previous page, set up a filter or exit the flow.
     * @return the user choice.
     */
    private int promptNavigationChoice(){
        checkFeedDisplay();

        System.out.println("Select one of the following options:");
        System.out.println(QUIT + ". Quit");
        System.out.println(FILTER + ". Filter");

        int choice;
        do {
            Colourise.cyan("Enter a corresponding number: ");
            try{
                choice = keyboard.nextInt();

                if(choice != QUIT && choice != FILTER) {
                    Colourise.red(String.format(
                        """
                        %d is not a valid option.
                        You must enter a number that corresponds to these options:
                        %d. Quit
                        %d. Filter
                        Valid inputs are: %d, %d.
                        %n""", choice, QUIT, FILTER, QUIT, FILTER)
                    );

                    choice = -1;
                }
            } catch (final Exception e){
                Colourise.red("Invalid input: you must enter a whole number, e.g 1\n");

                choice = -1;
            }

            keyboard.nextLine();

        } while(choice == -1);

        checkFeedDisplay();

        return choice;
    }

    /**
     * Class invariants for Feed
     */
    private void checkFeedDisplay(){
        Preconditions.checkNotNull(profileRegistry, "profileRegistry cannot be null");
        Preconditions.checkNotNull(mapManager, "mapManager cannot be null");
        Preconditions.checkNotNull(keyboard, "keyboard cannot be null");
    }
}
