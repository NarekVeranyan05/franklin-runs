package ca.umanitoba.cs.veranyan.ui;

import ca.umanitoba.cs.veranyan.logic.exceptions.*;
import ca.umanitoba.cs.veranyan.logic.ProfileRegistry;
import ca.umanitoba.cs.veranyan.model.Profile;
import ca.umanitoba.cs.veranyan.model.exceptions.CannotFollowAgainException;
import ca.umanitoba.cs.veranyan.model.exceptions.CannotFollowSelfException;
import ca.umanitoba.cs.veranyan.model.exceptions.CannotUnfollowNonFriendException;
import ca.umanitoba.cs.veranyan.model.exceptions.CannotUnfollowSelfException;
import ca.umanitoba.cs.veranyan.output.Colourise;
import ca.umanitoba.cs.veranyan.output.ProfilePrinter;
import com.github.lalyos.jfiglet.FigletFont;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * The {@link ProfileEditorScreen} class manages the UI interaction for updating
 * {@link Profile} information, such as name, follow / unfollow another {@link Profile},
 * adding {@link ca.umanitoba.cs.veranyan.model.gear.Gear}, etc.
 */
public class ProfileEditorScreen {
    private static final int ADD_GEAR = 1;
    private static final int FOLLOW = 2;
    private static final int UNFOLLOW = 3;

    private final ProfileRegistry profileRegistry;
    private final Scanner keyboard;

    public ProfileEditorScreen(ProfileRegistry profileRegistry, Scanner scanner){
        this.profileRegistry = profileRegistry;
        this.keyboard = scanner;

        checkProfileEditorDisplay();
    }

    /**
     * Starts the {@link Profile} update UI flow
     */
    public void startUpdate(){
        checkProfileEditorDisplay();

        try {
            System.out.println(FigletFont.convertOneLine("Profile Settings"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int choice = promptMenuChoice();

        switch (choice){
            case ADD_GEAR ->
                new GearEditorScreen(
                        profileRegistry, keyboard
                ).gearInsertionScreen();
            case FOLLOW -> followProfileScreen();
            case UNFOLLOW -> unfollowProfileScreen();
        }

        checkProfileEditorDisplay();
    }

    /**
     * Prompts the user to choose a {@link Profile} to follow
     */
    private void followProfileScreen() {
        checkProfileEditorDisplay();

            String name = null;

        try {
            do {
                for (Profile curr : profileRegistry.getProfilesNotInCircle()) {
                    new ProfilePrinter(curr).print();
                    System.out.println();
                }

                Colourise.cyan("Enter the name of the profile to follow: ");
                try {
                    name = keyboard.nextLine().trim();
                    Profile toFollow = profileRegistry.getProfile(name);

                    profileRegistry.follow(toFollow);
                    System.out.println("You now follow " + toFollow.getName());
                } catch (NoNameMatchException e) {
                    Colourise.red("Profile with name " + name + " does not exist.\n");
                    Colourise.red("Valid profile names are one of:\n");

                    name = null;
                } catch (CannotFollowSelfException e){
                    Colourise.red("You cannot follow yourself.\n");
                    Colourise.red("You can only follow a profile not in your list of friends, which are the ones below:\n");

                    name = null;
                } catch (CannotFollowAgainException e){
                    Colourise.red(name + " is already in your friends list.\n");
                    Colourise.red("You can only follow a friend not in your list of friends, which are the ones below:\n");

                    name = null;
                }
            } while (name == null);
        } catch (EmptyProfilesException e) {
            Colourise.red("You are friends with everyone! No one left to follow.\n");
        }

        checkProfileEditorDisplay();
    }

    /**
     * Prompts the user to choose a {@link Profile} to unfollow
     */
    private void unfollowProfileScreen(){
        checkProfileEditorDisplay();

        try {
            String name = null;
            do {
                for (Profile curr : profileRegistry.getFriends()) {
                    new ProfilePrinter(curr).print();
                    System.out.println();
                }

                Colourise.cyan("Enter the name of the profile to unfollow: ");
                try {
                    name = keyboard.nextLine().trim();
                    Profile toUnfollow = profileRegistry.getProfile(name);

                    profileRegistry.unfollow(toUnfollow);
                    System.out.println("You have unfollowed " + toUnfollow.getName());
                } catch (NoNameMatchException e) {
                    Colourise.red("Profile with name " + name + " does not exist.\n");
                    Colourise.red("Valid profile names are one of:\n");

                    name = null;
                } catch (CannotUnfollowSelfException e){
                    Colourise.red("You cannot unfollow yourself.\n");
                    Colourise.red("You can only unfollow a friend. Your friends are:\n");

                    name = null;
                } catch (CannotUnfollowNonFriendException e){
                    Colourise.red(name + " is not in your friends list.\n");
                    Colourise.red("You can only unfollow a friend. Your friends are:\n");

                    name = null;
                }
            } while (name == null);
        } catch (EmptyProfilesException e) {
            Colourise.red("You have no friends to unfollow.\n");
        }

        checkProfileEditorDisplay();
    }

    /**
     * Prompts user to select one of the menu choices.
     * @return the option selected
     */
    private int promptMenuChoice() {
        checkProfileEditorDisplay();

        System.out.println("Select one of the following options:");
        System.out.println(ADD_GEAR + ". Add gear");
        System.out.println(FOLLOW + ". Follow profile");
        System.out.println(UNFOLLOW + ". Unfollow profile");

        int choice;
        do{
            Colourise.cyan("Enter selected option number: ");
            try{
                choice = keyboard.nextInt();

                if(!List.of(ADD_GEAR, FOLLOW, UNFOLLOW).contains(choice)) {
                    Colourise.red(String.format(
                            """
                            %d is not a valid option.
                            You must enter a number that corresponds to these options:
                            %d. Add gear
                            %d Follow profile
                            %d Unfollow profile
                            Valid inputs are: %d, %d, and %d.
                            %n""", choice, ADD_GEAR, FOLLOW, UNFOLLOW,
                            ADD_GEAR, FOLLOW, UNFOLLOW)
                    );

                    choice = -1;
                }
            } catch (final InputMismatchException e){
                Colourise.red("Invalid input. You must enter a whole number, e.g. 2\n");

                choice = -1;
            }

            keyboard.nextLine();

        } while (choice == -1);

        checkProfileEditorDisplay();

        return choice;
    }

    /**
     * Class invariants for {@link ProfileEditorScreen}
     */
    private void checkProfileEditorDisplay(){
        Preconditions.checkNotNull(profileRegistry, "profileRegistry cannot be null");
        Preconditions.checkNotNull(keyboard, "keyboard cannot be null");
    }
}
