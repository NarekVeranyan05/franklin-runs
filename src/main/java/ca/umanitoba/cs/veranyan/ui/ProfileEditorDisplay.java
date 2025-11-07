package ca.umanitoba.cs.veranyan.ui;

import ca.umanitoba.cs.veranyan.logic.GearManager;
import ca.umanitoba.cs.veranyan.logic.ProfileRegistry;
import ca.umanitoba.cs.veranyan.model.Profile;
import ca.umanitoba.cs.veranyan.model.exceptions.BlankNameException;
import ca.umanitoba.cs.veranyan.model.exceptions.DuplicateProfileException;
import ca.umanitoba.cs.veranyan.model.exceptions.NoNameMatchException;
import ca.umanitoba.cs.veranyan.output.Colourise;
import ca.umanitoba.cs.veranyan.output.ProfilePrinter;
import com.github.lalyos.jfiglet.FigletFont;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * The {@code ProfileEditorDisplay} class manages the UI interaction for updating
 * profile information, such as name, follow / unfollow another profile,
 * adding gear, etc.
 */
public class ProfileEditorDisplay {
    private static final int CHANGE_NAME = 1;
    private static final int ADD_GEAR = 2;
    private static final int REMOVE_GEAR = 3;
    private static final int FOLLOW = 4;
    private static final int UNFOLLOW = 5;

    private final ProfileRegistry profileRegistry;
    private final Scanner keyboard = new Scanner(System.in);

    public ProfileEditorDisplay(ProfileRegistry profileRegistry){
        this.profileRegistry = profileRegistry;
        checkProfileEditorDisplay();
    }

    /**
     * Starts the profile update UI flow
     */
    public void startUpdate(){
        checkProfileEditorDisplay();

        try {
            System.out.println(FigletFont.convertOneLine("Profile Settings"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int choice = promptMenuChoice();
        keyboard.nextLine();

        switch (choice){
            case CHANGE_NAME -> profileNameInsertionScreen();
            case ADD_GEAR ->
                new GearEditorDisplay(
                        new GearManager(profileRegistry.getCurrentProfile())
                ).gearInsertionScreen();
            case REMOVE_GEAR ->
                new GearEditorDisplay(
                        new GearManager(profileRegistry.getCurrentProfile())
                ).gearRemovalScreen();
            case FOLLOW -> followProfileScreen();
            case UNFOLLOW -> unfollowProfileScreen();
        }

        checkProfileEditorDisplay();
    }

    /**
     * Prompts the user to choose a profile to follow
     */
    private void followProfileScreen() {
        checkProfileEditorDisplay();

        String name = null;

        do {
            if(profileRegistry.getProfilesNotInCircle().isEmpty()){
                Colourise.red("You are friends with everyone! No one left to be followed.\n");
            }
            else {
                for (Profile curr : profileRegistry.getProfilesNotInCircle()) {
                    new ProfilePrinter(curr).print();
                    System.out.println();
                }

                Colourise.cyan("Enter the name of the profile to follow: ");
                try {
                    name = keyboard.nextLine();
                    Profile toFollow = profileRegistry.getProfile(name);
                    profileRegistry.follow(toFollow);
                    System.out.println("You now follow " + toFollow.getName());
                } catch (NoNameMatchException e) {
                    Colourise.red("Profile with name " + name + "does not exist.\n");
                    Colourise.red("Valid profile names are one of:\n\n");

                    name = null;
                }
            }
        } while (name == null);

        checkProfileEditorDisplay();
    }

    /**
     * Prompts the user to choose a profile to unfollow
     */
    private void unfollowProfileScreen(){
        checkProfileEditorDisplay();

        String name = null;
        do {
            if(profileRegistry.getFriends().isEmpty()){
                Colourise.red("You have no friends. Follow another profile to see their activities in the feed.\n");
            }
            else {
                for (Profile curr : profileRegistry.getFriends()) {
                    new ProfilePrinter(curr).print();
                    System.out.println();
                }

                Colourise.cyan("Enter the name of the profile to unfollow: ");
                try {
                    name = keyboard.nextLine();
                    Profile toUnfollow = profileRegistry.getProfile(name);
                    profileRegistry.unfollow(toUnfollow);
                    System.out.println("You have unfollowed " + toUnfollow.getName());
                } catch (NoNameMatchException e) {
                    Colourise.red("Profile with name " + name + "does not exist.\n");
                    Colourise.red("Valid profile names are one of:\n\n");

                    name = null;
                }
            }
        } while (name == null);

        checkProfileEditorDisplay();
    }

    /**
     * Prompts the user for a new name for user profile
     * Sets the new name to the one provided.
     */
    private void profileNameInsertionScreen() {
        checkProfileEditorDisplay();

        Profile replacement;
        var builder = new Profile.ProfileBuilder();

        do {
            Colourise.cyan("Enter new getName of your profile: ");
            String newName = keyboard.nextLine();

            try {
                replacement = builder.name(newName).build();
                profileRegistry.replaceCurrentProfileName(replacement);
            } catch (BlankNameException e) {
                Colourise.red("The new getName of your profile has to contain at least one letter, e.g. Mark\n\n");
                replacement = null;
            } catch (DuplicateProfileException e) {
                Colourise.red("Profile with getName" + newName + " already exists. Your new profile getName must not match with existing names, which are:\n\n");
                for(var other : profileRegistry.getOtherProfiles()){
                    System.out.println(other);
                    System.out.println();
                }

                replacement = null;
            }
        } while (replacement == null);

        checkProfileEditorDisplay();
    }

    /**
     * Prompts user to select one of the menu choices.
     * @return the option selected
     */
    private int promptMenuChoice() {
        checkProfileEditorDisplay();

        System.out.println("Select one of the following options:");
        System.out.println(CHANGE_NAME + ". Change getName");
        System.out.println(ADD_GEAR + ". Add gear");
        System.out.println(REMOVE_GEAR + ". Remove gear");
        System.out.println(FOLLOW + ". Follow profile");
        System.out.println(UNFOLLOW + ". Unfollow profile");

        int choice;
        do{
            Colourise.cyan("Enter selected option number: ");
            try{
                choice = keyboard.nextInt();
                if(!List.of(CHANGE_NAME, ADD_GEAR, REMOVE_GEAR, FOLLOW, UNFOLLOW).contains(choice))
                    choice = -1;
            } catch (final InputMismatchException e){
                choice = -1;
            }

            if(choice == -1){
                Colourise.red(String.format(
                        """
                        You must enter a number that corresponds to these options:
                        %d. Change getName
                        %d. Add gear
                        %d Remove gear
                        %d Follow profile
                        %d Unfollow profile
                        Valid inputs are: %d, %d, %d, %d, and %d.
                        %n""", CHANGE_NAME, ADD_GEAR, REMOVE_GEAR, FOLLOW, UNFOLLOW,
                        CHANGE_NAME, ADD_GEAR, REMOVE_GEAR, FOLLOW, UNFOLLOW));
            }
        } while (choice == -1);

        checkProfileEditorDisplay();

        return choice;
    }

    /**
     * Class invariants for ProfileEditorDisplay
     */
    private void checkProfileEditorDisplay(){
        Preconditions.checkNotNull(profileRegistry, "profileRegistry cannot be null");
        Preconditions.checkNotNull(keyboard, "keyboard cannot be null");
    }
}
