package ca.umanitoba.cs.veranyan.logic;

import ca.umanitoba.cs.veranyan.logic.exceptions.EndCoordOutOfBoundsException;
import ca.umanitoba.cs.veranyan.logic.exceptions.StartCoordOutOfBoundsException;
import ca.umanitoba.cs.veranyan.model.Activity;
import ca.umanitoba.cs.veranyan.model.Profile;
import ca.umanitoba.cs.veranyan.model.gear.Gear;
import ca.umanitoba.cs.veranyan.model.gear.GearType;
import ca.umanitoba.cs.veranyan.model.map.Map;
import ca.umanitoba.cs.veranyan.model.map.Route;
import ca.umanitoba.cs.veranyan.model.map.coordinate.Coordinate;
import ca.umanitoba.cs.veranyan.model.map.coordinate.CoordinateType;
import ca.umanitoba.cs.veranyan.output.Colourise;
import ca.umanitoba.cs.veranyan.tests.TestResults;

import java.util.ArrayList;
import java.util.List;

import static ca.umanitoba.cs.veranyan.tests.TestHarness.bubblePrint;

public class PathFinderTestHarness {
    private int successes = 0;
    private int failures = 0;

    public TestResults runTests() {
        bubblePrint("PathFinder Test Harness");

        testPathFound();
        testPathNotFound();

        testNegativeStartX();
        testZeroStartX();
        testOverflowStartX();

        testNegativeEndX();
        testZeroEndX();
        testOverflowEndX();

        testNegativeStartY();
        testZeroStartY();
        testOverflowStartY();

        testNegativeEndY();
        testZeroEndY();
        testOverflowEndY();

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

    private void testPathFound(){
        Map.destroyInstance();

        try{
            List<List<Coordinate>> routeCoordinates = new ArrayList<>(5);
            List<Route> routes = new ArrayList<>();

            routeCoordinates.add(0, Coordinate.generateRectangle(CoordinateType.ROUTE, 5, 6, 8, 6));
            routeCoordinates.get(0).addAll(Coordinate.generateRectangle(CoordinateType.ROUTE, 6, 6, 6, 10));

            routeCoordinates.add(1, Coordinate.generateRectangle(CoordinateType.ROUTE, 12, 8, 12, 10));
            routeCoordinates.get(1).addAll(Coordinate.generateRectangle(CoordinateType.ROUTE, 12, 8, 12, 10));
            routeCoordinates.get(1).addAll(Coordinate.generateRectangle(CoordinateType.ROUTE, 10, 8, 12, 8));

            routeCoordinates.add(2, Coordinate.generateRectangle(CoordinateType.ROUTE, 8, 11, 8, 17));

            routeCoordinates.add(3,  Coordinate.generateRectangle(CoordinateType.ROUTE, 10, 4, 10, 8));
            routeCoordinates.get(3).addAll(Coordinate.generateRectangle(CoordinateType.ROUTE, 10, 4, 17, 4));

            routeCoordinates.add(4,  Coordinate.generateRectangle(CoordinateType.ROUTE, 6, 10, 12, 10));

            for(var routeCoordinateList : routeCoordinates){
                var builder = new Route.RouteBuilder();
                for(var coord : routeCoordinateList){
                    builder.withCoordinate(coord);
                }
                routes.add(builder.build());
            }

            List<Activity> allActivities = new ArrayList<>();

            allActivities.add(
                    new Activity.ActivityBuilder()
                            .route(routes.get(0))
                            .gear(new Gear.GearBuilder().type(GearType.ELECTRIC_BIKE).name("N").avgSpeed(120).build()).
                            startMonth(11).startDayOfMonth(11).startHour(11).startMinute(11).durationInMinutes(11).build()
            );
            allActivities.add(
                    new Activity.ActivityBuilder()
                            .route(routes.get(1))
                            .gear(new Gear.GearBuilder().type(GearType.ELECTRIC_BIKE).name("Not_a_duplicate").avgSpeed(120).build()).
                            startMonth(11).startDayOfMonth(12).startHour(11).startMinute(11).durationInMinutes(11).build()
            );
            allActivities.add(
                    new Activity.ActivityBuilder()
                            .route(routes.get(2))
                            .gear(new Gear.GearBuilder().type(GearType.ELECTRIC_BIKE).name("activity3").avgSpeed(120).build()).
                            startMonth(11).startDayOfMonth(13).startHour(11).startMinute(11).durationInMinutes(11).build()
            );
            allActivities.add(
                    new Activity.ActivityBuilder()
                            .route(routes.get(3))
                            .gear(new Gear.GearBuilder().type(GearType.ELECTRIC_BIKE).name("activity4").avgSpeed(120).build()).
                            startMonth(11).startDayOfMonth(14).startHour(11).startMinute(11).durationInMinutes(11).build()
            );
            allActivities.add(
                    new Activity.ActivityBuilder()
                            .route(routes.get(4))
                            .gear(new Gear.GearBuilder().type(GearType.ELECTRIC_BIKE).name("activity4").avgSpeed(120).build()).
                            startMonth(11).startDayOfMonth(15).startHour(11).startMinute(11).durationInMinutes(11).build()
            );

            var profile1 = new Profile.ProfileBuilder().name("Narek").build();
            var profile2 = new Profile.ProfileBuilder().name("Davit").build();
            profile1.addActivity(allActivities.get(0));
            profile1.addActivity(allActivities.get(1));
            profile1.addActivity(allActivities.get(2));
            profile2.addActivity(allActivities.get(3));
            profile2.addActivity(allActivities.get(4));

            profile1.follow(profile2);

            var pathFinder = new PathFinder(profile1, Map.getInstance());
            if(!pathFinder.findPath(new Coordinate(CoordinateType.ROUTE, 7, 6), new Coordinate(CoordinateType.ROUTE, 16, 4), true))
                fail("Was expected to find path");
            else pass("Successfully found the path");

        } catch (Exception e) {
            fail("Unexpected exception was thrown.");
            throw new RuntimeException(e);
        }
    }

    private void testPathNotFound(){
        Map.destroyInstance();

        try{
            List<List<Coordinate>> routeCoordinates = new ArrayList<>(5);
            List<Route> routes = new ArrayList<>();

            routeCoordinates.add(0, Coordinate.generateRectangle(CoordinateType.ROUTE, 5, 6, 8, 6));
            routeCoordinates.get(0).addAll(Coordinate.generateRectangle(CoordinateType.ROUTE, 6, 6, 6, 10));

            routeCoordinates.add(1, Coordinate.generateRectangle(CoordinateType.ROUTE, 12, 8, 12, 10));
            routeCoordinates.get(1).addAll(Coordinate.generateRectangle(CoordinateType.ROUTE, 12, 8, 12, 10));
            routeCoordinates.get(1).addAll(Coordinate.generateRectangle(CoordinateType.ROUTE, 10, 8, 12, 8));

            routeCoordinates.add(2, Coordinate.generateRectangle(CoordinateType.ROUTE, 8, 11, 8, 17));

            routeCoordinates.add(3,  Coordinate.generateRectangle(CoordinateType.ROUTE, 10, 4, 10, 8));
            routeCoordinates.get(3).addAll(Coordinate.generateRectangle(CoordinateType.ROUTE, 10, 4, 17, 4));

            for(var routeCoordinateList : routeCoordinates){
                var builder = new Route.RouteBuilder();
                for(var coord : routeCoordinateList){
                    builder.withCoordinate(coord);
                }
                routes.add(builder.build());
            }

            List<Activity> allActivities = new ArrayList<>();

            allActivities.add(
                    new Activity.ActivityBuilder()
                            .route(routes.get(0))
                            .gear(new Gear.GearBuilder().type(GearType.ELECTRIC_BIKE).name("N").avgSpeed(120).build()).
                            startMonth(11).startDayOfMonth(11).startHour(11).startMinute(11).durationInMinutes(11).build()
            );
            allActivities.add(
                    new Activity.ActivityBuilder()
                            .route(routes.get(1))
                            .gear(new Gear.GearBuilder().type(GearType.ELECTRIC_BIKE).name("Not_a_duplicate").avgSpeed(120).build()).
                            startMonth(11).startDayOfMonth(12).startHour(11).startMinute(11).durationInMinutes(11).build()
            );
            allActivities.add(
                    new Activity.ActivityBuilder()
                            .route(routes.get(2))
                            .gear(new Gear.GearBuilder().type(GearType.ELECTRIC_BIKE).name("activity3").avgSpeed(120).build()).
                            startMonth(11).startDayOfMonth(13).startHour(11).startMinute(11).durationInMinutes(11).build()
            );
            allActivities.add(
                    new Activity.ActivityBuilder()
                            .route(routes.get(3))
                            .gear(new Gear.GearBuilder().type(GearType.ELECTRIC_BIKE).name("activity4").avgSpeed(120).build()).
                            startMonth(11).startDayOfMonth(14).startHour(11).startMinute(11).durationInMinutes(11).build()
            );

            var profile = new Profile.ProfileBuilder().name("Narek").build();
            for(var activity : allActivities)
                profile.addActivity(activity);

            var pathFinder = new PathFinder(profile, Map.getInstance());
            if(pathFinder.findPath(new Coordinate(CoordinateType.ROUTE, 7, 6), new Coordinate(CoordinateType.ROUTE, 16, 4), true))
                fail("Should not be able to find path between (7, 6) and (16, 4)");
            else pass("Did not find path, as expected");

            Map.destroyInstance();
        } catch (Exception e) {
            fail("Unexpected exception was thrown.");
            e.printStackTrace();
        }
    }

    private void testNegativeStartX(){
        Map.destroyInstance();

        try{
            var profile = new Profile.ProfileBuilder().name("Narek").build();
            var finder = new PathFinder(profile, Map.getInstance());

            try {
                finder.findPath(new Coordinate(CoordinateType.ROUTE, -1, 2), new Coordinate(CoordinateType.ROUTE, 3, 3), false);

                fail("Should not be able to find path with start position x=-1");
            } catch (StartCoordOutOfBoundsException e) {
                pass("Successfully rejected start coordinate with x=-1");
            }
        } catch (Exception e){
            fail("Unexpected exception was thrown.");
            e.printStackTrace();
        }
    }

    private void testZeroStartX(){
        Map.destroyInstance();

        try{
            var profile = new Profile.ProfileBuilder().name("Narek").build();
            var finder = new PathFinder(profile, Map.getInstance());

            try {
                finder.findPath(new Coordinate(CoordinateType.ROUTE, 0, 10), new Coordinate(CoordinateType.ROUTE, 3, 3), false);

                fail("Should not be able to find path with start position x=0");
            } catch (StartCoordOutOfBoundsException e) {
                pass("Successfully rejected start coordinate with x=0");
            }
        } catch (Exception e){
            fail("Unexpected exception was thrown.");
            e.printStackTrace();
        }
    }

    private void testOverflowStartX(){
        Map.destroyInstance();

        try{
            var profile = new Profile.ProfileBuilder().name("Narek").build();
            var finder = new PathFinder(profile, Map.getInstance());

            try {
                finder.findPath(new Coordinate(CoordinateType.ROUTE, Map.getInstance().getLength() + 1, 10), new Coordinate(CoordinateType.ROUTE, 3, 3), false);

                fail("Should not be able to find path with start position x=" + (Map.getInstance().getLength() + 1));
            } catch (StartCoordOutOfBoundsException e) {
                pass("Successfully rejected start coordinate with x=" + (Map.getInstance().getLength() + 1));
            }
        } catch (Exception e){
            fail("Unexpected exception was thrown.");
            e.printStackTrace();
        }
    }

    private void testNegativeStartY(){
        Map.destroyInstance();

        try{
            var profile = new Profile.ProfileBuilder().name("Narek").build();
            var finder = new PathFinder(profile, Map.getInstance());

            try {
                finder.findPath(new Coordinate(CoordinateType.ROUTE, 2, -1), new Coordinate(CoordinateType.ROUTE, 3, 3), false);

                fail("Should not be able to find path with start position y=-1");
            } catch (StartCoordOutOfBoundsException e) {
                pass("Successfully rejected start coordinate with y=-1");
            }
        } catch (Exception e){
            fail("Unexpected exception was thrown.");
            e.printStackTrace();
        }
    }

    private void testZeroStartY(){
        Map.destroyInstance();

        try{
            var profile = new Profile.ProfileBuilder().name("Narek").build();
            var finder = new PathFinder(profile, Map.getInstance());

            try {
                finder.findPath(new Coordinate(CoordinateType.ROUTE, 3, 0), new Coordinate(CoordinateType.ROUTE, 3, 3), false);

                fail("Should not be able to find path with start position y=0");
            } catch (StartCoordOutOfBoundsException e) {
                pass("Successfully rejected start coordinate with y=0");
            }
        } catch (Exception e){
            fail("Unexpected exception was thrown.");
            e.printStackTrace();
        }
    }

    private void testOverflowStartY(){
        Map.destroyInstance();

        try{
            var profile = new Profile.ProfileBuilder().name("Narek").build();
            var finder = new PathFinder(profile, Map.getInstance());

            try {
                finder.findPath(new Coordinate(CoordinateType.ROUTE, 1, Map.getInstance().getWidth() + 1), new Coordinate(CoordinateType.ROUTE, 3, 3), false);

                fail("Should not be able to find path with start position y=" + (Map.getInstance().getWidth() + 1));
            } catch (StartCoordOutOfBoundsException e) {
                pass("Successfully rejected start coordinate with y=" + (Map.getInstance().getWidth() + 1));
            }
        } catch (Exception e){
            fail("Unexpected exception was thrown.");
            e.printStackTrace();
        }
    }

    private void testNegativeEndX(){
        Map.destroyInstance();

        try{
            var profile = new Profile.ProfileBuilder().name("Narek").build();
            var finder = new PathFinder(profile, Map.getInstance());

            try {
                finder.findPath(new Coordinate(CoordinateType.ROUTE, 3, 2), new Coordinate(CoordinateType.ROUTE, -1, 3), false);

                fail("Should not be able to find path with end position x=-1");
            } catch (EndCoordOutOfBoundsException e) {
                pass("Successfully rejected end coordinate with x=-1");
            }
        } catch (Exception e){
            fail("Unexpected exception was thrown.");
            e.printStackTrace();
        }
    }

    private void testZeroEndX(){
        Map.destroyInstance();

        try{
            var profile = new Profile.ProfileBuilder().name("Narek").build();
            var finder = new PathFinder(profile, Map.getInstance());

            try {
                finder.findPath(new Coordinate(CoordinateType.ROUTE, 3, 10), new Coordinate(CoordinateType.ROUTE, 0, 3), false);

                fail("Should not be able to find path with end position x=0");
            } catch (EndCoordOutOfBoundsException e) {
                pass("Successfully rejected end coordinate with x=0");
            }
        } catch (Exception e){
            fail("Unexpected exception was thrown.");
            e.printStackTrace();
        }
    }

    private void testOverflowEndX(){
        Map.destroyInstance();

        try{
            var profile = new Profile.ProfileBuilder().name("Narek").build();
            var finder = new PathFinder(profile, Map.getInstance());

            try {
                finder.findPath(new Coordinate(CoordinateType.ROUTE, 3, 10), new Coordinate(CoordinateType.ROUTE, Map.getInstance().getLength() + 1, 3), false);

                fail("Should not be able to find path with end position x=" + (Map.getInstance().getLength() + 1));
            } catch (EndCoordOutOfBoundsException e) {
                pass("Successfully rejected end coordinate with x=" + (Map.getInstance().getLength() + 1));
            }
        } catch (Exception e){
            fail("Unexpected exception was thrown.");
            e.printStackTrace();
        }
    }

    private void testNegativeEndY(){
        Map.destroyInstance();

        try{
            var profile = new Profile.ProfileBuilder().name("Narek").build();
            var finder = new PathFinder(profile, Map.getInstance());

            try {
                finder.findPath(new Coordinate(CoordinateType.ROUTE, 2, 3), new Coordinate(CoordinateType.ROUTE, 3, -1), false);

                fail("Should not be able to find path with end position y=-1");
            } catch (EndCoordOutOfBoundsException e) {
                pass("Successfully rejected end coordinate with y=-1");
            }
        } catch (Exception e){
            fail("Unexpected exception was thrown.");
            e.printStackTrace();
        }
    }

    private void testZeroEndY(){
        Map.destroyInstance();

        try{
            var profile = new Profile.ProfileBuilder().name("Narek").build();
            var finder = new PathFinder(profile, Map.getInstance());

            try {
                finder.findPath(new Coordinate(CoordinateType.ROUTE, 3, 3), new Coordinate(CoordinateType.ROUTE, 3, 0), false);

                fail("Should not be able to find path with end position y=0");
            } catch (EndCoordOutOfBoundsException e) {
                pass("Successfully rejected end coordinate with y=0");
            }
        } catch (Exception e){
            fail("Unexpected exception was thrown.");
            e.printStackTrace();
        }
    }

    private void testOverflowEndY(){
        Map.destroyInstance();

        try{
            var profile = new Profile.ProfileBuilder().name("Narek").build();
            var finder = new PathFinder(profile, Map.getInstance());

            try {
                finder.findPath(new Coordinate(CoordinateType.ROUTE, 1, 2), new Coordinate(CoordinateType.ROUTE, 3, Map.getInstance().getWidth() + 1), false);

                fail("Should not be able to find path with end position y=" + (Map.getInstance().getWidth() + 1));
            } catch (EndCoordOutOfBoundsException e) {
                pass("Successfully rejected end coordinate with y=" + (Map.getInstance().getWidth() + 1));
            }
        } catch (Exception e){
            fail("Unexpected exception was thrown.");
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
