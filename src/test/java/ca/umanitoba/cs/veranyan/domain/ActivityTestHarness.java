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
import java.time.LocalDateTime;
import java.time.Year;

public class ActivityTestHarness {
    private static int successes = 0;
    private static int failures = 0;

    public TestResults runTests() {
        bubblePrint("Activity Test!");

        testInvalidName();
        testCreateActivity();
        testAvgSpeed();

        testInvalidOverlowStartMonth();
        testInvalidZeroStartMonth();
        testInvalidNegativeStartMonth();

        testInvalidOverlowStartDayOfMonth();
        testInvalidZeroStartDayOfMonth();
        testInvalidNegativeStartDayOfMonth();

        testInvalidNegativeStartHour();
        testInvalidOverlowStartHour();
        testInvalidZeroStartHour();

        testInvalidNegativeStartMinute();
        testInvalidOverlowStartMinute();
        testInvalidZeroStartMinute();

        testInvalidNegativeDuration();
        testInvalidOverlowDuration();
        testInvalidZeroDuration();

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
    public static void testCreateActivity() {
        Gear gear = null;
        try{
            gear = new Gear.GearBuilder().name("gear").type(GearType.ELECTRIC_BIKE).avgSpeed(120).build();
        } catch (Exception e) { // FIXME an I catch Exception here?
            fail("Unexpected exception thrown: ");
            e.printStackTrace();
        }

        Route route = null;
        try{
            route = new Route.RouteBuilder().withCoordinate(new Coordinate(CoordinateType.ROUTE, 5, 5)).build();
        } catch (Exception e) { // FIXME an I catch Exception here?
            fail("Unexpected exception thrown: ");
            e.printStackTrace();
        }

        var builder = new Activity.ActivityBuilder().gear(gear).route(route);

        try {
            var activity = builder.startMonth(6).startDayOfMonth(15).startHour(15).startMinute(15).durationInMinutes(15).build();

            var expected = LocalDateTime.of(Year.now().getValue(), 6, 15, 15, 15);
            if(!activity.getStart().equals(expected)){
                fail("Start date was not set as expected, got " + activity.getStart() + " expected " + expected);
            } else if(!activity.getEnd().isAfter(activity.getStart())) {
                fail("End date was not set as expected, got " + activity.getEnd() + " expected " + expected.plusMinutes(15));
            }
            else {
                pass("All properties in the happy path were set as expected.");
            }
        } catch (Exception e) {
            fail("Exception thrown during happy path inputs.");
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

    public static void testInvalidOverlowStartMonth(){
        try{
            var builder = new Activity.ActivityBuilder();
            builder.startMonth(13);
        } catch (InvalidTimeRangeException e){
            pass("Successfully rejected 13 for start month.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
        }
    }

    public static void testInvalidZeroStartMonth(){
        try{
            var builder = new Activity.ActivityBuilder();
            builder.startMonth(0);
        } catch (InvalidTimeRangeException e){
            pass("Successfully rejected 0 for start month.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
        }
    }

    public static void testInvalidNegativeStartMonth(){
        try{
            var builder = new Activity.ActivityBuilder();
            builder.startMonth(-1);
        } catch (InvalidTimeRangeException e){
            pass("Successfully rejected -1 for start month.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
        }
    }

    public static void testInvalidOverlowStartDayOfMonth(){
        try{
            var builder = new Activity.ActivityBuilder();
            builder.startMonth(2).startDayOfMonth(30);
        } catch (InvalidTimeRangeException e){
            pass("Successfully rejected 30 for start day of month.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
        }
    }

    public static void testInvalidZeroStartDayOfMonth(){
        try{
            var builder = new Activity.ActivityBuilder();
            builder.startMonth(10).startDayOfMonth(0);
        } catch (InvalidTimeRangeException e){
            pass("Successfully rejected 0 for start day of month.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
        }
    }

    public static void testInvalidNegativeStartDayOfMonth(){
        try{
            var builder = new Activity.ActivityBuilder();
            builder.startMonth(6).startDayOfMonth(-1);
        } catch (InvalidTimeRangeException e){
            pass("Successfully rejected -1 for start day of month.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
        }
    }

    public static void testInvalidOverlowStartHour(){
        try{
            var builder = new Activity.ActivityBuilder();
            builder.startHour(25);
        } catch (InvalidTimeRangeException e){
            pass("Successfully rejected 25 for start hour.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
        }
    }

    public static void testInvalidZeroStartHour(){
        try{
            var builder = new Activity.ActivityBuilder();
            builder.startHour(0);
        } catch (InvalidTimeRangeException e){
            pass("Successfully rejected 0 for start hour.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
        }
    }

    public static void testInvalidNegativeStartHour(){
        try{
            var builder = new Activity.ActivityBuilder();
            builder.startHour(-1);
        } catch (InvalidTimeRangeException e){
            pass("Successfully rejected -1 for start hour");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
        }
    }

    public static void testInvalidOverlowStartMinute(){
        try{
            var builder = new Activity.ActivityBuilder();
            builder.startMinute(60);
        } catch (InvalidTimeRangeException e){
            pass("Successfully rejected 60 for start minute.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
        }
    }

    public static void testInvalidZeroStartMinute(){
        try{
            var builder = new Activity.ActivityBuilder();
            builder.startMinute(0);
        } catch (InvalidTimeRangeException e){
            pass("Successfully rejected 0 for start minute.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
        }
    }

    public static void testInvalidNegativeStartMinute(){
        try{
            var builder = new Activity.ActivityBuilder();
            builder.startMinute(-1);
        } catch (InvalidTimeRangeException e){
            pass("Successfully rejected -1 for start minute");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
        }
    }

    public static void testInvalidOverlowDuration(){
        try{
            var builder = new Activity.ActivityBuilder();
            builder.durationInMinutes(10081);
        } catch (InvalidDurationException e){
            pass("Successfully rejected 10081 for duration.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
        }
    }

    public static void testInvalidZeroDuration(){
        try{
            var builder = new Activity.ActivityBuilder();
            builder.durationInMinutes(0);
        } catch (InvalidDurationException e){
            pass("Successfully rejected 0 for duration.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
        }
    }

    public static void testInvalidNegativeDuration(){
        try{
            var builder = new Activity.ActivityBuilder();
            builder.durationInMinutes(-1);
        } catch (InvalidDurationException e){
            pass("Successfully rejected -1 for duration.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
        }
    }

    private static void testAvgSpeed() {
        try {
            var gear = new Gear.GearBuilder().name("gear").type(GearType.ELECTRIC_BIKE).avgSpeed(120).build();

            var route = new Route.RouteBuilder().withCoordinate(new Coordinate(CoordinateType.ROUTE, 5, 5)).build();

            var builder = new Activity.ActivityBuilder().gear(gear).route(route);

            var activity = builder.startMonth(6).startDayOfMonth(15).startHour(15).startMinute(15).durationInMinutes(15).build();

            double expected = (double) route.getMeasure() * 10/ (15 * 60);
            if(Math.abs(activity.getAvgSpeed() - expected) < 0.0001){
                pass("Successfully computed average speed");
            }
            else{
                fail("Did not compute average speed correctly. Expected: " + expected + ", got: " + activity.getAvgSpeed());
            }
        } catch (BlankNameException | InvalidTimeRangeException | InvalidDurationException e){
            fail("Exception thrown during happy path inputs.");
            e.printStackTrace();
        } catch (Exception e) {
            fail("Unexpected exceptions are thrown: ");
            e.printStackTrace();
        }
    }

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
