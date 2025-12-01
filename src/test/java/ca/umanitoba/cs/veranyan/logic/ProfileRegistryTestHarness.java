package ca.umanitoba.cs.veranyan.logic;

import ca.umanitoba.cs.veranyan.logic.exceptions.DuplicateProfileException;
import ca.umanitoba.cs.veranyan.logic.exceptions.EmptyProfilesException;
import ca.umanitoba.cs.veranyan.logic.exceptions.NoNameMatchException;
import ca.umanitoba.cs.veranyan.logic.mocks.ProfilePersistenceMock;
import ca.umanitoba.cs.veranyan.model.Activity;
import ca.umanitoba.cs.veranyan.model.Profile;
import ca.umanitoba.cs.veranyan.model.assets.Pair;
import ca.umanitoba.cs.veranyan.model.exceptions.*;
import ca.umanitoba.cs.veranyan.model.gear.Gear;
import ca.umanitoba.cs.veranyan.model.gear.GearType;
import ca.umanitoba.cs.veranyan.model.map.Route;
import ca.umanitoba.cs.veranyan.model.map.coordinate.Coordinate;
import ca.umanitoba.cs.veranyan.model.map.coordinate.CoordinateType;
import ca.umanitoba.cs.veranyan.output.Colourise;
import ca.umanitoba.cs.veranyan.tests.TestResults;

import java.util.ArrayList;
import java.util.List;

import static ca.umanitoba.cs.veranyan.tests.TestHarness.bubblePrint;

public class ProfileRegistryTestHarness {
    private int successes = 0;
    private int failures = 0;

    public TestResults runTests() {
        bubblePrint("ProfileRegistry Test Harness");

        testGetEmptyProfiles();
        testNoProfileMatch();

        testAddProfile();
        testInvalidAddDuplicateProfile();

        testLoadProfile();
        testUnloadProfile();

        testFollow();
        testInvalidFollowSelf();
        testInvalidFollowAlreadyFriend();

        testUnfollow();
        testInvalidUnfollowSelf();
        testInvalidUnfollowNonFriend();

        testAddActivity();
        testInvalidAddDuplicateActivity();
        testGetActivities();

        testAddGear();
        testInvalidAddDuplicateGear();

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

    private void testGetEmptyProfiles(){
        try {
            var registry = new ProfileRegistry(new ProfilePersistenceMock());

            try {
                registry.getProfiles();

                fail("Should not be able to return profiles: none exist");
            } catch (EmptyProfilesException e) {
                pass("Successfully did not return any profiles, as expected");
            }
        } catch (Exception e) {
            fail("Unexpected exception thrown: ");
            e.printStackTrace();
        }
    }

    private void testNoProfileMatch() {
        try {
            var registry = new ProfileRegistry(new ProfilePersistenceMock());

            try {
                registry.getProfile("Narek");

                fail("Should not be able to return a profile: none exist");
            } catch (NoNameMatchException e) {
                pass("Was not able to find profile with name Narek");
            }
        } catch (Exception e) {
            fail("FAIL: Unexpected exception thrown: ");
            e.printStackTrace();
        }
    }

    private void testAddProfile() {
        try {
            var profile = new Profile.ProfileBuilder().name("Arthur").build();

            var registry = new ProfileRegistry(new ProfilePersistenceMock());
            registry.addProfile(profile);

            if (registry.getProfiles().isEmpty()) {
                fail("Did not add any profiles to the registry");
            } else if (registry.getProfiles().size() > 1) {
                fail("Added more profiles than necessary, expected 1, got " + registry.getProfiles().size());
            } else if (!registry.getProfiles().contains(profile) || !registry.getProfile("Arthur").equals(profile)) {
                fail("Did not add profile Arthur to the registry");
            } else{
                pass("Successfully added profile Arthur to the registry");
            }
        } catch (Exception e) {
            fail("FAIL: Unexpected exception thrown: ");
            e.printStackTrace();
        }
    }

    private void testInvalidAddDuplicateProfile() {
        ProfileRegistry registry = null;
        Profile profile1 = null;
        Profile profile2 = null;

        try {
            profile1 = new Profile.ProfileBuilder().name("Arthur").build();
            profile2 = new Profile.ProfileBuilder().name("Arthur").build();

            registry = new ProfileRegistry(new ProfilePersistenceMock());
            registry.addProfile(profile1);
            registry.addProfile(profile2);

            fail("Should not have been able to add duplicate profile with name Arthur");
        } catch (DuplicateProfileException e) {
            try {
                if (registry.getProfiles().contains(profile1) && !registry.getProfiles().contains(profile2))
                    pass("Successfully rejected Arthur as duplicate profile");
                else if(!registry.getProfiles().contains(profile1))
                    fail("Rejected the original profile instead of the duplicate");
                else if(registry.getProfiles().size() == 2)
                    fail("Should not have been able to add duplicate profile with name Arthur");
            } catch (Exception ee) {
                fail("Unexpected exception thrown.");
                e.printStackTrace();
            }
        } catch (Exception e) {
            fail("Unexpected exception thrown.");
            e.printStackTrace();
        }
    }

    private void testLoadProfile() {
        try {
            var profile = new Profile.ProfileBuilder().name("Arthur").build();

            var registry = new ProfileRegistry(new ProfilePersistenceMock());
            registry.addProfile(profile);
            registry.loadProfile(profile);

            if (!registry.getCurrentProfile().equals(profile)) {
                fail("Did not load the profile");
            } else if (!registry.checkStatus(ProfileRegistry.Status.ONLINE)) {
                fail("Did not change status to ONLINE");
            } else {
                pass("Successfully loaded Arthur as the current profile");
            }
        } catch (Exception e) {
            fail("Unexpected exception thrown: ");
            e.printStackTrace();
        }
    }

    private void testUnloadProfile() {
        try {
            var profile = new Profile.ProfileBuilder().name("Arthur").build();

            var registry = new ProfileRegistry(new ProfilePersistenceMock());
            registry.addProfile(profile);
            registry.loadProfile(profile);
            registry.unloadProfile();

            if(!registry.checkStatus(ProfileRegistry.Status.OFFLINE)) {
                fail("Did not change status to OFFLINE");
            } else {
                pass("Successfully unloaded Arthur from the system");
            }
        } catch (Exception e) {
            fail("Unexpected exception thrown: ");
            e.printStackTrace();
        }
    }

    public void testAddActivity() {
        try {
            var activity1 = new Activity.ActivityBuilder().route(
                            new Route.RouteBuilder().withCoordinate(
                                    new Coordinate(CoordinateType.ROUTE, 1, 1)).build()
                    ).gear(new Gear.GearBuilder().type(GearType.ELECTRIC_BIKE).name("N").avgSpeed(120).build()).
                    startMonth(11).startDayOfMonth(11).startHour(11).startMinute(11).durationInMinutes(11).build();

            var activity2 = new Activity.ActivityBuilder().route(
                            new Route.RouteBuilder().withCoordinate(
                                    new Coordinate(CoordinateType.ROUTE, 2, 2)).build()
                    ).gear(new Gear.GearBuilder().type(GearType.ELECTRIC_BIKE).name("Not_a_duplicate").avgSpeed(120).build()).
                    startMonth(11).startDayOfMonth(12).startHour(11).startMinute(11).durationInMinutes(11).build();

            var profile = new Profile.ProfileBuilder().name("Narek").build();

            var registry = new ProfileRegistry(new ProfilePersistenceMock());
            registry.addProfile(profile);
            registry.loadProfile(profile);
            registry.addActivity(activity1);
            registry.addActivity(activity2);

            if (profile.getActivities().contains(activity1) && profile.getActivities().contains(activity2))
                pass("Activities successfully added");
            else fail("Was expected to add the activities to the profile");
        } catch (Exception e) {
            fail("Unexpected exception thrown.");
            e.printStackTrace();
        }
    }

    public void testInvalidAddDuplicateActivity() {
        try {
            var activity1 = new Activity.ActivityBuilder().route(
                            new Route.RouteBuilder().withCoordinate(
                                    new Coordinate(CoordinateType.ROUTE, 1, 1)).build()
                    ).gear(new Gear.GearBuilder().type(GearType.ELECTRIC_BIKE).name("N").avgSpeed(120).build()).
                    startMonth(11).startDayOfMonth(11).startHour(11).startMinute(11).durationInMinutes(11).build();

            var activity2 = new Activity.ActivityBuilder().route(
                            new Route.RouteBuilder().withCoordinate(
                                    new Coordinate(CoordinateType.ROUTE, 2, 2)).build()
                    ).gear(new Gear.GearBuilder().type(GearType.ELECTRIC_BIKE).name("Not_a_duplicate").avgSpeed(120).build()).
                    startMonth(11).startDayOfMonth(11).startHour(11).startMinute(11).durationInMinutes(11).build();

            var profile = new Profile.ProfileBuilder().name("Narek").build();

            var registry = new ProfileRegistry(new ProfilePersistenceMock());
            registry.addProfile(profile);
            registry.loadProfile(profile);
            registry.addActivity(activity1);

            try {
                registry.addActivity(activity2);

                fail("Should not have succeeded in adding the duplicate activity.");
            } catch (DuplicateActivityException e) {
                if (profile.getActivities().size() == 1)
                    pass("Successfully rejected an activity with same start date");
                else fail("Was expected to reject the duplicate activity");
            }
        } catch (Exception e) {
            fail("Unexpected exception thrown.");
            e.printStackTrace();
        }
    }

    public void testGetActivities() {
        try {
            List<Activity> allActivities = new ArrayList<>();
            allActivities.add(
                    new Activity.ActivityBuilder()
                            .route(new Route.RouteBuilder().withCoordinate(new Coordinate(CoordinateType.ROUTE, 1, 1)).build())
                            .gear(new Gear.GearBuilder().type(GearType.ELECTRIC_BIKE).name("N").avgSpeed(120).build()).
                            startMonth(11).startDayOfMonth(11).startHour(11).startMinute(11).durationInMinutes(11).build()
            );

            allActivities.add(
                    new Activity.ActivityBuilder()
                            .route(new Route.RouteBuilder().withCoordinate(new Coordinate(CoordinateType.ROUTE, 1, 1)).build())
                            .gear(new Gear.GearBuilder().type(GearType.ELECTRIC_BIKE).name("Not_a_duplicate").avgSpeed(120).build()).
                            startMonth(11).startDayOfMonth(12).startHour(11).startMinute(11).durationInMinutes(11).build()
            );

            allActivities.add(
                    new Activity.ActivityBuilder()
                            .route(new Route.RouteBuilder().withCoordinate(new Coordinate(CoordinateType.ROUTE, 1, 1)).build())
                            .gear(new Gear.GearBuilder().type(GearType.ELECTRIC_BIKE).name("activity3").avgSpeed(120).build()).
                            startMonth(11).startDayOfMonth(12).startHour(11).startMinute(11).durationInMinutes(11).build()
            );


            allActivities.add(
                    new Activity.ActivityBuilder()
                            .route(new Route.RouteBuilder().withCoordinate(new Coordinate(CoordinateType.ROUTE, 1, 1)).build())
                            .gear(new Gear.GearBuilder().type(GearType.ELECTRIC_BIKE).name("activity4").avgSpeed(120).build()).
                            startMonth(11).startDayOfMonth(12).startHour(11).startMinute(11).durationInMinutes(11).build()
            );

            var profile1 = new Profile.ProfileBuilder().name("profile1").build();
            var profile2 = new Profile.ProfileBuilder().name("profile2").build();
            var profile3 = new Profile.ProfileBuilder().name("profile3").build();

            var registry = new ProfileRegistry(new ProfilePersistenceMock());
            registry.addProfile(profile1);
            registry.addProfile(profile2);
            registry.addProfile(profile3);

            registry.loadProfile(profile1);
            registry.addActivity(allActivities.get(0));
            registry.addActivity(allActivities.get(1));
            registry.unloadProfile();

            registry.loadProfile(profile2);
            registry.addActivity(allActivities.get(2));
            registry.unloadProfile();

            registry.loadProfile(profile3);
            registry.addActivity(allActivities.get(3));
            registry.follow(profile1);

            var ownActivities = registry.getActivities(ProfileRegistry.ActivityFilterType.OWN);
            var friendsActivities = registry.getActivities(ProfileRegistry.ActivityFilterType.FRIENDS);
            var ownAndFriendsActivities = registry.getActivities(ProfileRegistry.ActivityFilterType.OWN_AND_FRIENDS);

            if (!registry.getActivities(ProfileRegistry.ActivityFilterType.ALL).stream().map(Pair::getSecond).toList().containsAll(allActivities))
                fail("Did not include all the activites, expected 4, got " + registry.getActivities(ProfileRegistry.ActivityFilterType.ALL).size());
            else if(ownActivities.size() != 1){
                fail("Did not include correct number of own activities, expected 1, got " + ownActivities.size());
            } else if(ownActivities.get(0).getSecond() != allActivities.get(3)){
                fail("Did not return enough own activities");
            } else if(friendsActivities.size() != 2){
                fail("Did not include correct number of friends activities, expected 2, got " + friendsActivities.size());
            } else if(!friendsActivities.stream().map(Pair::getSecond).toList().containsAll(allActivities.subList(0, 2))){
                fail("Did not return enough friends activities");
            } else if(ownAndFriendsActivities.size() != 3) {
                fail("Did not include correct number of own and friends activities, expected 3, got " + ownAndFriendsActivities.size());
            } else if(!ownAndFriendsActivities.stream().map(Pair::getSecond).toList().containsAll(friendsActivities.stream().map(Pair::getSecond).toList())){
                fail("Did not return enough friends activities in the own and friends activities list");
            } else if(!ownAndFriendsActivities.stream().map(Pair::getSecond).toList().containsAll(ownActivities.stream().map(Pair::getSecond).toList())){
                fail("Did not return enough own activities in the own and friends activities list");
            }
        } catch (Exception e) {
            fail("Unexpected exception thrown.");
            e.printStackTrace();
        }
    }

    public void testAddGear() {
        try {
            var gear1 = new Gear.GearBuilder().type(GearType.ROAD_BIKE).name("name1").avgSpeed(120).build();
            var gear2 = new Gear.GearBuilder().type(GearType.ELECTRIC_BIKE).name("name2").avgSpeed(120).build();

            var profile = new Profile.ProfileBuilder().name("Narek").build();

            var registry = new ProfileRegistry(new ProfilePersistenceMock());

            registry.addProfile(profile);
            registry.loadProfile(profile);

            registry.addGear(gear1);
            registry.addGear(gear2);

            if (profile.getGears().contains(gear1) && profile.getGears().contains(gear2))
                pass("Gears successfully added.");
            else fail("Was expected to add gears to the profile");
        } catch (Exception e) {
            fail("Unexpected exception thrown.");
            e.printStackTrace();
        }
    }

    public void testInvalidAddDuplicateGear() {
        try {
            var gear1 = new Gear.GearBuilder().type(GearType.ROAD_BIKE).name("same_name").avgSpeed(120).build();
            var gear2 = new Gear.GearBuilder().type(GearType.ELECTRIC_BIKE).name("same_name").avgSpeed(120).build();

            var profile = new Profile.ProfileBuilder().name("Narek").build();

            var registry = new ProfileRegistry(new ProfilePersistenceMock());
            registry.addProfile(profile);
            registry.loadProfile(profile);

            try {
                registry.addGear(gear1);
                registry.addGear(gear2);

                fail("Should not have succeeded in adding the duplicate gear.");
            } catch (DuplicateGearException e) {
                if (profile.getGears().size() == 1)
                    pass("Successfully rejected a duplicate gear with same name");
                else fail("Was expected to reject the duplicate gear");
            }
        } catch (Exception e) {
            fail("Unexpected exception thrown.");
            e.printStackTrace();
        }
    }

    public void testFollow() {
        try {
            var profile1 = new Profile.ProfileBuilder().name("Narek").build();
            var profile2 = new Profile.ProfileBuilder().name("Arthur").build();

            var registry = new ProfileRegistry(new ProfilePersistenceMock());
            registry.addProfile(profile1);
            registry.addProfile(profile2);
            registry.loadProfile(profile1);

            registry.follow(profile2);

            if (registry.getFriends().contains(profile2)){
                try{
                    if(registry.getProfilesNotInCircle().contains(profile2))
                        fail("Failed to add Arthur as friend");
                } catch (EmptyProfilesException e) {
                    pass("Successfully added Arthur as friend");
                }
            } else fail("Failed to add Arthur as friend");
        } catch (Exception e) {
            fail("Unexpected exception thrown.");
            e.printStackTrace();
        }
    }

    public void testInvalidFollowSelf() {
        try {
            var profile = new Profile.ProfileBuilder().name("Narek").build();

            var registry = new ProfileRegistry(new ProfilePersistenceMock());
            registry.addProfile(profile);
            registry.loadProfile(profile);

            try {
                registry.follow(profile);

                fail("Should not have been able to follow self.");
            } catch (CannotFollowSelfException e1) {
                try{
                    registry.getFriends().contains(profile);

                    fail("Should not have been able to get friends: there are none.");
                } catch (EmptyProfilesException e2){
                    pass("Successfully rejected following self");
                }
            }
        } catch (Exception e) {
            fail("Unexpected exception thrown.");
            e.printStackTrace();
        }
    }

    public void testInvalidFollowAlreadyFriend() {
        try {
            var profile1 = new Profile.ProfileBuilder().name("Narek").build();
            var profile2 = new Profile.ProfileBuilder().name("Arthur").build();

            var registry = new ProfileRegistry(new ProfilePersistenceMock());
            registry.addProfile(profile1);
            registry.addProfile(profile2);
            registry.loadProfile(profile1);

            registry.follow(profile2);

            try {
                registry.follow(profile2);

                fail("Should not have been able to follow friend again.");
            } catch (CannotFollowAgainException e) {
                if (registry.getFriends().size() == 1)
                    pass("Successfully rejected following a friend again");
                else fail("Should not have been able to follow friend again.");
            }
        } catch (Exception e) {
            fail("Unexpected exception thrown.");
            e.printStackTrace();
        }
    }

    public void testUnfollow() {
        try {
            var profile1 = new Profile.ProfileBuilder().name("Narek").build();
            var profile2 = new Profile.ProfileBuilder().name("Arthur").build();

            var registry = new ProfileRegistry(new ProfilePersistenceMock());
            registry.addProfile(profile1);
            registry.addProfile(profile2);
            registry.loadProfile(profile1);

            registry.follow(profile2);
            registry.unfollow(profile2);


            if (registry.getProfilesNotInCircle().contains(profile2)){
                try{
                    if(registry.getFriends().contains(profile2))
                        fail("Failed to add Arthur as friend");
                } catch (EmptyProfilesException e) {
                    pass("Successfully removed Arthur from friends");
                }
            } else fail("Failed to add Arthur as friend");
        } catch (Exception e) {
            fail("Unexpected exception thrown.");
            e.printStackTrace();
        }
    }

    public void testInvalidUnfollowSelf() {
        try {
            var profile = new Profile.ProfileBuilder().name("Narek").build();

            var registry = new ProfileRegistry(new ProfilePersistenceMock());
            registry.addProfile(profile);
            registry.loadProfile(profile);

            try {
                registry.unfollow(profile);

                fail("Should not have been able to unfollow self.");
            } catch (CannotUnfollowSelfException e) {
                try {
                    if (registry.getFriends().contains(profile))
                        fail("Should not have been able to unfollow self.");
                } catch (EmptyProfilesException ex) {
                    pass("Successfully rejected unfollowing self");
                }
            }
        } catch (Exception e) {
            fail("Unexpected exception thrown.");
            e.printStackTrace();
        }
    }

    public void testInvalidUnfollowNonFriend() {
        try {
            var profile1 = new Profile.ProfileBuilder().name("Narek").build();
            var profile2 = new Profile.ProfileBuilder().name("Arthur").build();

            var registry = new ProfileRegistry(new ProfilePersistenceMock());
            registry.addProfile(profile1);
            registry.addProfile(profile2);
            registry.loadProfile(profile1);

            registry.unfollow(profile2);

            fail("Should not have been able to unfollow a profile that was never a friend.");
        } catch (CannotUnfollowNonFriendException e) {
            pass("Successfully rejected unfollowing non-friend");
        } catch (Exception e) {
            fail("Unexpected exception thrown.");
            e.printStackTrace();
        }
    }

    private void pass(String message) {
        successes++;
        Colourise.green("PASS: " + message + "\n");
    }

    private void fail(String message) {
        failures++;
        Colourise.red("FAIL: " + message + "\n");
    }
}
