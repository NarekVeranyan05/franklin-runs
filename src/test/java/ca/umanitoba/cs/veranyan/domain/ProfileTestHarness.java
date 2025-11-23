package ca.umanitoba.cs.veranyan.domain;

import ca.umanitoba.cs.veranyan.model.Activity;
import ca.umanitoba.cs.veranyan.model.Profile;
import ca.umanitoba.cs.veranyan.model.exceptions.*;
import ca.umanitoba.cs.veranyan.model.gear.Gear;
import ca.umanitoba.cs.veranyan.model.gear.GearType;
import ca.umanitoba.cs.veranyan.model.map.Route;
import ca.umanitoba.cs.veranyan.model.map.coordinate.Coordinate;
import ca.umanitoba.cs.veranyan.model.map.coordinate.CoordinateType;
import ca.umanitoba.cs.veranyan.output.Colourise;
import ca.umanitoba.cs.veranyan.tests.TestResults;
import com.github.lalyos.jfiglet.FigletFont;

import java.io.IOException;

public class ProfileTestHarness {
    private static int successes = 0;
    private static int failures = 0;

    public TestResults runTests() {
        bubblePrint("Profile Test!");

        testInvalidName();
        testCreateProfile();

        testFollow();
        testInvalidFollowSelf();
        testInvalidFollowAlreadyFriend();

        testUnfollow();
        testInvalidUnfollowSelf();
        testInvalidUnfollowNonFriend();

        testAddActivity();

        bubblePrint("Test results");
        System.out.printf("Total tests: %d\n", successes + failures);
        System.out.printf("\tSuccesses: %d\n", successes);
        System.out.printf("\tFailures: %d\n", failures);

        if (failures > 0) {
            Colourise.red("There were test failures.\n");
        } else {
            Colourise.green("All tests passed!\n");
        }

        return new TestResults(successes, failures);
    }

    // this is my happy path:
    public static void testCreateProfile() {
        var builder = new Profile.ProfileBuilder();

        try {
            Profile p = builder.name("Narek").build();

            if (!p.getName().equals("Narek")) {
                fail("Name was not set as expected, got " + p.getName() + " expected Narek");
            } else {
                pass("All properties in the happy path were set as expected.");
            }
        } catch (Exception e) {
            fail("Exception thrown during happy path inputs.");
            e.printStackTrace();
        }
    }

    public static void testAddActivity() {
        Activity activity = null;
        try{
            activity = new Activity.ActivityBuilder().route(
                        new Route.RouteBuilder().withCoordinate(
                                new Coordinate(CoordinateType.ROUTE, 1, 1)).build()
                        ).gear(new Gear.GearBuilder().type(GearType.ELECTRIC_BIKE).name("N").avgSpeed(120).build()).
                        startMonth(11).startDayOfMonth(11).startHour(11).startMinute(11).durationInMinutes(11).build();
        } catch (CoordinateOutOfBoundsException | BlankNameException | NonPositiveSpeedException |
                 InvalidTimeRangeException | InvalidDurationException e){
            fail("Unexpected exception thrown: ");
            e.printStackTrace();
        }

        var builder = new Profile.ProfileBuilder();

        try{
            var profile = builder.name("Narek").build();
            profile.addActivity(activity);

            if(profile.getActivities().contains(activity))
                pass("Activity successfully added");
            else fail("Was expected to add the activity to the profile");
        }
        catch (Exception e) {
            fail("Exception thrown during valid input.");
            e.printStackTrace();
        }
    }

    public static void testFollow(){
        var builder1 = new Profile.ProfileBuilder();
        var builder2 = new Profile.ProfileBuilder();

        try{
            var profile1 = builder1.name("Narek").build();
            var profile2 = builder2.name("Arthur").build();
            profile1.follow(profile2);

            if(profile1.getFriends().contains(profile2)){
               pass("Successfully added Arthur as friend");
            } else{
                fail("Arthur was not a friend of Narek as expected");
            }
        } catch (Exception e) {
            fail("Exception thrown during valid input.");
            e.printStackTrace();
        }
    }

    public static void testInvalidFollowSelf(){
        var builder = new Profile.ProfileBuilder();

        try{
            var profile = builder.name("Narek").build();
            profile.follow(profile);

            fail("Exception was not thrown as expected");
        } catch (CannotFollowSelfException e){
            pass("Successfully rejected following self");
        }
        catch (Exception e) {
            fail("Exception thrown during valid input.");
            e.printStackTrace();
        }
    }

    public static void testInvalidFollowAlreadyFriend(){
        var builder1 = new Profile.ProfileBuilder();
        var builder2 = new Profile.ProfileBuilder();

        try{
            var profile1 = builder1.name("Narek").build();
            var profile2 = builder2.name("Arthur").build();
            profile1.follow(profile2);
            profile1.follow(profile2);

            fail("Exception was not thrown as expected");
        } catch (CannotFollowAgainException e){
            pass("Successfully rejected following a friend again");
        }
        catch (Exception e) {
            fail("Exception thrown during valid input.");
            e.printStackTrace();
        }
    }

    public static void testUnfollow(){
        var builder1 = new Profile.ProfileBuilder();
        var builder2 = new Profile.ProfileBuilder();

        try{
            var profile1 = builder1.name("Narek").build();
            var profile2 = builder2.name("Arthur").build();
            profile1.follow(profile2);
            profile1.unfollow(profile2);

            pass("Successfully removed Arthur from Narek's friends");
        } catch (Exception e) {
            fail("Exception thrown during valid input.");
            e.printStackTrace();
        }
    }

    public static void testInvalidUnfollowSelf(){
        var builder = new Profile.ProfileBuilder();

        try{
            var profile = builder.name("Narek").build();
            profile.unfollow(profile);

            fail("Exception was not thrown as expected");
        } catch (CannotUnfollowSelfException e){
            pass("Successfully rejected unfollowing self");
        }
        catch (Exception e) {
            fail("Exception thrown during valid input.");
            e.printStackTrace();
        }
    }

    public static void testInvalidUnfollowNonFriend(){
        var builder1 = new Profile.ProfileBuilder();
        var builder2 = new Profile.ProfileBuilder();

        try{
            var profile1 = builder1.name("Narek").build();
            var profile2 = builder2.name("Arthur").build();

            profile1.unfollow(profile2);

            fail("Exception was not thrown as expected");
        } catch (CannotUnfollowNonFriendException e){
            pass("Successfully rejected unfollowing non-friend");
        }
        catch (Exception e) {
            fail("Exception thrown during valid input.");
            e.printStackTrace();
        }
    }

    public static void testInvalidName() {
        var builder = new Profile.ProfileBuilder();

        try {
            builder.name("");

            fail("Should not have succeeded in setting empty string as nickname.");
        } catch(BlankNameException e) {
            pass("Successfully rejected empty string.");
        } catch( Exception e ) {
            fail("Some other exception was thrown.");
            e.printStackTrace();
        }
    }

    // FIXME add withGear, withActivity, withFriend validations

    private static void pass(String message) {
        successes++;
        Colourise.green("PASS: " + message + "\n");
    }

    private static void fail(String message) {
        failures++;

        Colourise.red("FAIL: " + message + "\n");
    }

    private static void bubblePrint(String message) {
        try {
            System.out.println(FigletFont.convertOneLine(message));
        } catch (IOException ignored) { }
    }
}
