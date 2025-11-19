package ca.umanitoba.cs.veranyan.output;

import ca.umanitoba.cs.veranyan.model.Profile;
import com.google.common.base.Preconditions;

public class ProfilePrinter {
    private final Profile profile;

    /**
     * Constructor for {@link ProfilePrinter}.
     * @param profile the {@link Profile} to be printed. Must not be {@code null}.
     */
    public ProfilePrinter(Profile profile){
        this.profile = profile;

        checkProfilePrinter();
    }

    /**
     * Prints out a {@link Profile}. This method prints to standard output (`System.out`).
     */
    public void print(){
        checkProfilePrinter();

        System.out.print(profile.getName());
    }

    private void checkProfilePrinter(){
        Preconditions.checkNotNull(profile, "profile cannot be null");
    }
}
