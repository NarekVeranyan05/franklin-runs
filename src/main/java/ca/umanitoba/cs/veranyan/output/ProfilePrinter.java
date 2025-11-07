package ca.umanitoba.cs.veranyan.output;

import ca.umanitoba.cs.veranyan.model.Profile;
import com.google.common.base.Preconditions;

public class ProfilePrinter {
    private final Profile profile;

    public ProfilePrinter(Profile profile){
        this.profile = profile;

        checkProfilePrinter();
    }

    public void print(){
        checkProfilePrinter();

        System.out.print(profile.getName());
    }

    private void checkProfilePrinter(){
        Preconditions.checkNotNull(profile, "profile cannot be null");
    }
}
