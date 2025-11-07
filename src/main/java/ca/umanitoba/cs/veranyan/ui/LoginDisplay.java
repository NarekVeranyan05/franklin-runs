package ca.umanitoba.cs.veranyan.ui;

import ca.umanitoba.cs.veranyan.logic.ProfileRegistry;
import ca.umanitoba.cs.veranyan.model.Profile;
import ca.umanitoba.cs.veranyan.model.exceptions.BlankNameException;
import ca.umanitoba.cs.veranyan.model.exceptions.DuplicateProfileException;
import ca.umanitoba.cs.veranyan.model.exceptions.NoNameMatchException;
import ca.umanitoba.cs.veranyan.output.Colourise;
import ca.umanitoba.cs.veranyan.output.ProfilePrinter;
import com.google.common.base.Preconditions;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * The {@code LoginDisplay} class manages the UI interaction for logging in to or
 * signing in to the exercise management system.
 */
public class LoginDisplay {
    private static final int LOGIN = 1;
    private static final int SIGNUP = 2;

    private final ProfileRegistry profileRegistry;
    private final Scanner keyboard = new Scanner(System.in);

    public LoginDisplay(ProfileRegistry profileRegistry) {
        this.profileRegistry = profileRegistry;

        checkLoginDisplay();
    }

    /**
     * Starts the login process.
     * Guarantees to log in the profile into the system.
     */
    public void startLogin() {
        checkLoginDisplay();

        boolean isEntered; // user must end up logging into some profile

        // prompting user for registering in the system until successful registration
        do {
            int choice = promptLoginChoice();

            if (choice == LOGIN)
                login();
            else if (choice == SIGNUP)
                signup();

            isEntered = profileRegistry.checkStatus(ProfileRegistry.Status.ONLINE);
        } while(!isEntered);

        checkLoginDisplay();
    }

    /**
     * Logs the user in to the system, if there are any existing profiles
     */
    public void login() {
        checkLoginDisplay();

        if(profileRegistry.isEmpty()){
            Colourise.red("You have to create a profile to log in.\n");
        }
        else{
            profileRegistry.loadProfile(
                    promptProfileSelection()
            );
        }

        checkLoginDisplay();
    }

    /**
     * Signs the user in to the system.
     */
    public void signup() {
        checkLoginDisplay();

        Profile candidate = null;

        // creation of new profile until success
        do {
            try {
                candidate = promptProfileInsertion();
                profileRegistry.addProfile(candidate);
            } catch (final DuplicateProfileException e){
                Colourise.red("Profile with " + candidate.getName() + " already exists. Enter a getName that does not match with the following profile names:");

                for (var profile : profileRegistry.getProfiles()) {
                    new ProfilePrinter(profile).print();
                    System.out.println();
                }

                candidate = null;
            }
        } while(candidate == null);

        profileRegistry.loadProfile(candidate);

        checkLoginDisplay();
    }

    /**
     * Prompts the user to choose to either log in or sign up
     * @return the user choice
     */
    private int promptLoginChoice() {
        checkLoginDisplay();

        System.out.println("Select one of the following options:");
        System.out.println(LOGIN + ". Log in");
        System.out.println(SIGNUP + ". Sign up");

        int choice;
        do {
            Colourise.cyan("Enter a corresponding number: ");
            try{
                choice = keyboard.nextInt();
            } catch (final InputMismatchException e){
                choice = -1;
            }

            keyboard.nextLine();
            if(choice != LOGIN && choice != SIGNUP)
                Colourise.red(String.format(
                        """
                        You must enter a number that corresponds to these options:
                        %d. Log in
                        %d. Sign up
                        Valid inputs are: %d and %d.
                        %n""", LOGIN, SIGNUP, LOGIN, SIGNUP));
        } while(choice == -1);

        checkLoginDisplay();

        return choice;
    }

    /**
     * Prompts the user to select an existing profile to sign in
     */
    private Profile promptProfileSelection() {
        checkLoginDisplay();

        Profile selectedProfile;
        do {
            for (var profile : profileRegistry.getProfiles()) {
                new ProfilePrinter(profile).print();
                System.out.println();
            }

            String name = "";
            try {
                Colourise.cyan("Enter profile name: ");
                name = keyboard.nextLine();
                selectedProfile = profileRegistry.getProfile(name);
            } catch (NoNameMatchException e) {
                selectedProfile = null;
                Colourise.red(String.format(
                        """
                        The profile %s you provided didn't match with any existing profile.
                        The name you must enter must be a name of an existing profile.
                        The profile names are provided below:
                        """, name));
            }
        } while(selectedProfile == null);

        checkLoginDisplay();

        return selectedProfile;
    }

    /**
     * Prompts the user to create a new profile.
     * @return the new profile. Must not be {@code null}
     */
    private Profile promptProfileInsertion() {
        checkLoginDisplay();

        Profile.ProfileBuilder builder = new Profile.ProfileBuilder();
        String name;

        do {
            try {
                Colourise.cyan("Enter profile name: ");
                name = keyboard.next();
                builder.name(name);
            } catch (final BlankNameException e){
                System.out.println("Name of a profile must contain at least one letter, e.g. Larry");
                name = "";
            }
        } while(name.isBlank());

        checkLoginDisplay();

        return builder.build();
    }

    /**
     * Class invariants for checkLoginDisplay
     */
    private void checkLoginDisplay(){
        Preconditions.checkNotNull(profileRegistry, "profileRegistry cannot be null");
        Preconditions.checkNotNull(keyboard, "keyboard cannot be null");
    }
}
