package ca.umanitoba.cs.veranyan.ui;

import ca.umanitoba.cs.veranyan.logic.exceptions.EmptyProfilesException;
import ca.umanitoba.cs.veranyan.model.exceptions.BlankNameException;
import ca.umanitoba.cs.veranyan.logic.exceptions.DuplicateProfileException;
import ca.umanitoba.cs.veranyan.logic.exceptions.NoNameMatchException;
import ca.umanitoba.cs.veranyan.logic.ProfileRegistry;
import ca.umanitoba.cs.veranyan.model.Profile;
import ca.umanitoba.cs.veranyan.output.Colourise;
import ca.umanitoba.cs.veranyan.output.ProfilePrinter;
import com.google.common.base.Preconditions;

import java.util.InputMismatchException;
import java.util.Optional;
import java.util.Scanner;

/**
 * The {@link LoginScreen} class manages the UI interaction for logging in to or
 * signing in to the exercise management system.
 */
public class LoginScreen {
    private static final int LOGIN = 1;
    private static final int SIGNUP = 2;

    private final ProfileRegistry profileRegistry;
    private final Scanner keyboard;

    public LoginScreen(ProfileRegistry profileRegistry, Scanner scanner) {
        this.profileRegistry = profileRegistry;
        this.keyboard = scanner;

        checkLoginDisplay();
    }

    /**
     * Starts the login process.
     * Guarantees to log in the {@link Profile} into the system.
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

        Optional<Profile> profile = promptProfileSelection();

        profile.ifPresent(profileRegistry::loadProfile);

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
                Colourise.red("Profile with name " + candidate.getName() + " already exists. Your profile name must be unique.\n");

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

                if(choice != LOGIN && choice != SIGNUP) {
                    Colourise.red(String.format(
                        """
                        %d is not a valid option.
                        You must enter a number that corresponds to these options:
                        %d. Log in
                        %d. Sign up
                        Valid inputs are: %d and %d.
                        %n""", choice, LOGIN, SIGNUP, LOGIN, SIGNUP)
                    );

                    choice = -1;
                }
            } catch (final InputMismatchException e){
                Colourise.red("Invalid input: you must enter a whole number, e.g. 1\n");

                choice = -1;
            }

            keyboard.nextLine();

        } while(choice == -1);

        checkLoginDisplay();

        return choice;
    }

    /**
     * Prompts the user to select an existing {@link Profile} to sign in
     * @return the {@link Profile} that the user selected, or {@code null} if there are no profiles in the system
     */
    private Optional<Profile> promptProfileSelection() {
        checkLoginDisplay();

        Optional<Profile> selectedProfile = Optional.empty();
        try {
            do {
                for (var profile : profileRegistry.getProfiles()) {
                    new ProfilePrinter(profile).print();
                    System.out.println();
                }

                String name = "";
                try {
                    Colourise.cyan("Enter profile name (one of the above): ");
                    name = keyboard.nextLine().trim();
                    selectedProfile = Optional.of(profileRegistry.getProfile(name));
                } catch (NoNameMatchException e) {
                    Colourise.red(String.format(
                            """
                            The profile %s you provided didn't match with any existing profile.
                            The name you must enter must be a name of an existing profile.
                            The existing profile names are provided below:
                            %n""", name));

                    selectedProfile = Optional.empty();
                }
            } while (selectedProfile.isEmpty());
        } catch(EmptyProfilesException e){
            Colourise.red("There are no existing profiles. You have to create a profile to log in.\n");
        }

        checkLoginDisplay();

        return selectedProfile;
    }

    /**
     * Prompts the user to create a new {@link Profile}.
     * @return the new {@link Profile}. Must not be {@code null}
     */
    private Profile promptProfileInsertion() {
        checkLoginDisplay();

        Profile.ProfileBuilder builder = new Profile.ProfileBuilder();
        String name;

        do {
            try {
                Colourise.cyan("Enter profile name: ");
                name = keyboard.nextLine().trim();

                builder.name(name);
            } catch (final BlankNameException e){
                Colourise.red("Name of a profile must contain at least one letter, e.g. Larry\n");
                name = "";
            }
        } while(name.isBlank());

        checkLoginDisplay();

        return builder.build();
    }

    /**
     * Class invariants for {@link LoginScreen}
     */
    private void checkLoginDisplay(){
        Preconditions.checkNotNull(profileRegistry, "profileRegistry cannot be null");
        Preconditions.checkNotNull(keyboard, "keyboard cannot be null");
    }
}
