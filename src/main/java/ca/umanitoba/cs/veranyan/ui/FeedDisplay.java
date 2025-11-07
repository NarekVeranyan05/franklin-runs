package ca.umanitoba.cs.veranyan.ui;

import ca.umanitoba.cs.veranyan.logic.MapManager;
import ca.umanitoba.cs.veranyan.logic.ProfileRegistry;
import ca.umanitoba.cs.veranyan.logic.assets.RouteFilterType;
import ca.umanitoba.cs.veranyan.model.Activity;
import ca.umanitoba.cs.veranyan.model.exceptions.InvalidGearTypeException;
import ca.umanitoba.cs.veranyan.output.ActivityPrinter;
import ca.umanitoba.cs.veranyan.output.Colourise;
import ca.umanitoba.cs.veranyan.output.MapPrinter;
import com.github.lalyos.jfiglet.FigletFont;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.SortedSet;

/**
 * The {@code FeedDisplay} class manages displaying the feed and changing filter types,
 */
public class FeedDisplay {
    private static final int QUIT = 0;
    private static final int PREV_PAGE = 1;
    private static final int NEXT_PAGE = 2;
    private static final int FILTER = 3;

    private final ProfileRegistry profileRegistry;
    private final MapManager mapManager;
    private final Scanner keyboard;

    public FeedDisplay(ProfileRegistry profileRegistry, MapManager mapManager){
        this.profileRegistry = profileRegistry;
        this.mapManager = mapManager;
        this.keyboard = new Scanner(System.in);

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

        var type = RouteFilterType.OWN_AND_FRIENDS;
        SortedSet<Activity> activities = profileRegistry.getActivities(type);

        int choice = -1;
        do{
            if(activities.isEmpty())
                System.out.println("The feed of activities is empty.");
            else {
                for (var activity : activities) {
                    mapManager.setUpRoute(activity.getRoute()); // printing only this activity

                    new ActivityPrinter(activity).print();
                    System.out.println();
                    new MapPrinter(mapManager.getMap()).print();

                    System.out.println("\n");
                }
            }

            choice = promptNavigationChoice();
            if(choice == FILTER){
                type = filterSelectionScreen();
                activities = profileRegistry.getActivities(type);
            }
        } while (choice != QUIT);

        checkFeedDisplay();
    }

    /**
     * Prompts the user to select a filter type.
     * @return the filter type selected.
     */
    private RouteFilterType filterSelectionScreen() {
        checkFeedDisplay();

        RouteFilterType filterType = null;
        String filterTypeInput = null;

        do{
            for(var type : Arrays.stream(
                    RouteFilterType.values()).
                    filter(val -> val != RouteFilterType.ALL).toList()
            )
            {
                System.out.println(" - " + type.toString());
            }


            Colourise.cyan("Enter filter by name: ");
            try {
                filterTypeInput = keyboard.nextLine();
                filterType = RouteFilterType.fromString(filterTypeInput);
            } catch (InvalidGearTypeException e) {
                Colourise.red(filterTypeInput + " is not a valid Gear type. Valid types are one of below: ");


                filterTypeInput = null;
            }
        } while (filterTypeInput == null);

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
        System.out.println(PREV_PAGE + ". Previous page");
        System.out.println(NEXT_PAGE + ". Next page");
        System.out.println(FILTER + ". Filter");

        int choice;
        do {
            Colourise.cyan("Enter a corresponding number: ");
            try{
                choice = keyboard.nextInt();
            } catch (final Exception e){
                choice = -1;
            }

            keyboard.nextLine();
            if(choice != QUIT && choice != PREV_PAGE && choice != NEXT_PAGE && choice != FILTER)
                Colourise.red(String.format(
                        """
                        You must enter a number that corresponds to these options:
                        %d. Quit
                        %d. Previous page
                        %d. Next page
                        %d. Filter
                        Valid inputs are: %d, %d, %d, %d.
                        %n""", QUIT, PREV_PAGE, NEXT_PAGE, FILTER, QUIT, PREV_PAGE, NEXT_PAGE, FILTER));
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
