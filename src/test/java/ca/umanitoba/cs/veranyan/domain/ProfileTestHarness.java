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

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.temporal.ChronoUnit;

import static ca.umanitoba.cs.veranyan.tests.TestHarness.bubblePrint;

public class ProfileTestHarness {
    private int successes = 0;
    private int failures = 0;

    public TestResults runTests() {
        bubblePrint("Profile Test Harness");

        testInvalidName();
        testCreateProfile();

        testGetTotalNumbOfStepsMonth();
        testGetTotalNumbOfStepsWeek();

        testGetGear();
        testGetGearEmpty();
        testRemoveGear();

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
    public void testCreateProfile() {
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

    public void testRemoveGear(){
        Gear gear = null;
        try {
            gear = new Gear.GearBuilder().type(GearType.ROAD_BIKE).name("name1").avgSpeed(120).build();
        } catch (BlankNameException | NonPositiveSpeedException e) {
            fail("Unexpected exception thrown: ");
            e.printStackTrace();
        }

        var builder = new Profile.ProfileBuilder();
        Profile profile = null;

        try{
            profile = builder.name("Narek").build();
            profile.addGear(gear);
            profile.removeGear(gear);
        } catch (DuplicateGearException e) {
            if(!profile.getGears().contains(gear))
                pass("Gear successfully removed");
            else fail("Was expected to remove gear from the profile");
        }
        catch (Exception e) {
            fail("Exception thrown during valid input.");
            e.printStackTrace();
        }
    }

    private void testGetGear(){
        Gear gear = null;
        try {
            gear = new Gear.GearBuilder().type(GearType.ROAD_BIKE).name("name1").avgSpeed(120).build();
        } catch (BlankNameException | NonPositiveSpeedException e) {
            fail("Unexpected exception thrown: ");
            e.printStackTrace();
        }

        var builder = new Profile.ProfileBuilder();
        Profile profile = null;

        try{
            profile = builder.name("Narek").build();
            profile.addGear(gear);
            var gear2 = profile.getGear("name1");

            if(gear == gear2)
                pass("Successfully found gear with matching name");
        } catch (GearNotFoundException e) {
            pass("Was expected to find the gear with name1");
        } catch (Exception e){
            fail("Exception thrown during valid input.");
            e.printStackTrace();
        }
    }

    private void testGetGearEmpty(){
        var builder = new Profile.ProfileBuilder();
        Profile profile = null;

        try{
            profile = builder.name("Narek").build();
            profile.getGear("no_such_gear");

            fail("Should not have succeeded in getting non-existent gear");
        } catch (GearNotFoundException e) {
            pass("Did not find the gear, as expected");
        } catch (Exception e){
            fail("Exception thrown during valid input.");
            e.printStackTrace();
        }
    }

    public void testInvalidName() {
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

    public void testGetTotalNumbOfStepsMonth(){
        Activity activity1 = null;
        Activity activity2 = null;
        try{
            activity1 = new Activity.ActivityBuilder().route(
                            new Route.RouteBuilder()
                                    .withCoordinate(new Coordinate(CoordinateType.ROUTE, 1, 1))
                                    .withCoordinate(new Coordinate(CoordinateType.ROUTE, 2, 2))
                                    .withCoordinate(new Coordinate(CoordinateType.ROUTE, 3, 3))
                                    .withCoordinate(new Coordinate(CoordinateType.ROUTE, 5, 4)).build()
                    ).gear(new Gear.GearBuilder().type(GearType.ELECTRIC_BIKE).name("N").avgSpeed(120).build()).
                    startMonth(11).startDayOfMonth(12).startHour(11).startMinute(11).durationInMinutes(11).build();

            activity2 = new Activity.ActivityBuilder().route(
                            new Route.RouteBuilder()
                                    .withCoordinate(new Coordinate(CoordinateType.ROUTE, 10, 10))
                                    .withCoordinate(new Coordinate(CoordinateType.ROUTE, 11, 11)).build()
                    ).gear(new Gear.GearBuilder().type(GearType.ELECTRIC_BIKE).name("N").avgSpeed(120).build()).
                    startMonth(11).startDayOfMonth(11).startHour(11).startMinute(11).durationInMinutes(11).build();
        } catch (CoordinateOutOfBoundsException | BlankNameException | NonPositiveSpeedException |
                 InvalidTimeRangeException | InvalidDurationException e){
            fail("Unexpected exception thrown: ");
            e.printStackTrace();
        }

        var builder = new Profile.ProfileBuilder();
        Profile profile;

        try{
            profile = builder.name("Narek").build();

            profile.addActivity(activity1);
            profile.addActivity(activity2);

            int totalNumSteps = profile.getTotalNumSteps(
                    LocalDate.of(Year.now().getValue(), Month.NOVEMBER, 15),
                    ChronoUnit.MONTHS);
            if(totalNumSteps == 6){
                pass("Correctly computed the total number of steps.");
            } else{
                fail("Computed the total number of steps wrong. Expected 6, got " + totalNumSteps);
            }
        }
        catch (Exception e) {
            fail("Exception thrown during valid input.");
            e.printStackTrace();
        }
    }

    public void testGetTotalNumbOfStepsWeek(){
        Activity activity1 = null;
        Activity activity2 = null;
        try{
            activity1 = new Activity.ActivityBuilder().route(
                            new Route.RouteBuilder()
                                    .withCoordinate(new Coordinate(CoordinateType.ROUTE, 1, 1))
                                    .withCoordinate(new Coordinate(CoordinateType.ROUTE, 2, 2))
                                    .withCoordinate(new Coordinate(CoordinateType.ROUTE, 3, 3))
                                    .withCoordinate(new Coordinate(CoordinateType.ROUTE, 5, 4)).build()
                    ).gear(new Gear.GearBuilder().type(GearType.ELECTRIC_BIKE).name("N").avgSpeed(120).build()).
                    startMonth(11).startDayOfMonth(30).startHour(11).startMinute(11).durationInMinutes(11).build();

            activity2 = new Activity.ActivityBuilder().route(
                            new Route.RouteBuilder()
                                    .withCoordinate(new Coordinate(CoordinateType.ROUTE, 10, 10))
                                    .withCoordinate(new Coordinate(CoordinateType.ROUTE, 11, 11)).build()
                    ).gear(new Gear.GearBuilder().type(GearType.ELECTRIC_BIKE).name("N").avgSpeed(120).build()).
                    startMonth(11).startDayOfMonth(11).startHour(11).startMinute(11).durationInMinutes(11).build();
        } catch (CoordinateOutOfBoundsException | BlankNameException | NonPositiveSpeedException |
                 InvalidTimeRangeException | InvalidDurationException e){
            fail("Unexpected exception thrown: ");
            e.printStackTrace();
        }

        var builder = new Profile.ProfileBuilder();
        Profile profile;

        try{
            profile = builder.name("Narek").build();

            profile.addActivity(activity1);
            profile.addActivity(activity2);

            int totalNumSteps = profile.getTotalNumSteps(
                    LocalDate.of(Year.now().getValue(), Month.NOVEMBER, 11),
                    ChronoUnit.WEEKS);
            if(totalNumSteps == 2){
                pass("Correctly computed the total number of steps.");
            } else{
                fail("Computed the total number of steps wrong. Expected 6, got " + totalNumSteps);
            }
        }
        catch (Exception e) {
            fail("Exception thrown during valid input.");
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
