package ca.umanitoba.cs.veranyan.domain;

import ca.umanitoba.cs.veranyan.model.exceptions.CoordinateOutOfBoundsException;
import ca.umanitoba.cs.veranyan.model.map.Route;
import ca.umanitoba.cs.veranyan.model.map.coordinate.Coordinate;
import ca.umanitoba.cs.veranyan.model.map.coordinate.CoordinateType;
import ca.umanitoba.cs.veranyan.output.Colourise;
import ca.umanitoba.cs.veranyan.tests.TestResults;

import static ca.umanitoba.cs.veranyan.tests.TestHarness.bubblePrint;

public class RouteTestHarness {
    private int successes = 0;
    private int failures = 0;

    public TestResults runTests() {
        bubblePrint("Route Test Harness");

        testCreateRoute();

        testInvalidNegativeXCoordinate();
        testInvalidZeroXCoordinate();

        testInvalidNegativeYCoordinate();
        testInvalidZeroYCoordinate();

        testContains();

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
    public void testCreateRoute() {
        var builder = new Route.RouteBuilder();

        try {
            var coordinate = new Coordinate(CoordinateType.ROUTE, 10, 10);
            var route = builder.withCoordinate(coordinate).build();
            var coordinates = route.getCoordinates();
            int numCoordinates = route.getMeasure();

            if (numCoordinates != 1) {
                fail("Coordinates were not set as expected, got " + numCoordinates + " coordinates, expected " + 1);
            } else if(!coordinates.get(0).equals(coordinate)){
                fail(
                    "Coordinates were not set as expected, got (" +
                    coordinates.get(0).x() + ", " + coordinates.get(0).y() +
                    "), expected (" + coordinate.x() + ", " + coordinate.y() + ")"
                );
            } else {
                pass("All properties in the happy path were set as expected.");
            }
        } catch (Exception e) {
            fail("Exception thrown during happy path inputs.");
            e.printStackTrace();
        }
    }

    public void testInvalidNegativeXCoordinate(){
        var builder = new Route.RouteBuilder();

        try {
            var coordinate = new Coordinate(CoordinateType.ROUTE, -1, 10);
            builder.withCoordinate(coordinate).build();

            fail("Should not have succeeded in adding coordinate with negative x to coordinates");
        } catch (CoordinateOutOfBoundsException e) {
            pass("Successfully rejected coordinate with negative x for coordinates");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
            throw new RuntimeException(e);
        }
    }

    public void testInvalidZeroXCoordinate(){
        var builder = new Route.RouteBuilder();

        try {
            var coordinate = new Coordinate(CoordinateType.ROUTE, 0, 10);
            builder.withCoordinate(coordinate).build();

            fail("Should not have succeeded in adding coordinate with x=0 to coordinates");
        } catch (CoordinateOutOfBoundsException e) {
            pass("Successfully rejected coordinate with x=0 for coordinates");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
            throw new RuntimeException(e);
        }
    }

    public void testInvalidNegativeYCoordinate(){
        var builder = new Route.RouteBuilder();

        try {
            var coordinate = new Coordinate(CoordinateType.ROUTE, 10, -1);
            builder.withCoordinate(coordinate).build();

            fail("Should not have succeeded in adding coordinate with negative y to coordinates");
        } catch (CoordinateOutOfBoundsException e) {
            pass("Successfully rejected coordinate with negative y for coordinates");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
            throw new RuntimeException(e);
        }
    }

    public void testInvalidZeroYCoordinate(){
        var builder = new Route.RouteBuilder();

        try {
            var coordinate = new Coordinate(CoordinateType.ROUTE, 10, 0);
            builder.withCoordinate(coordinate).build();

            fail("Should not have succeeded in adding coordinate with y=0 to coordinates");
        } catch (CoordinateOutOfBoundsException e) {
            pass("Successfully rejected coordinate with y=0 for coordinates");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
            throw new RuntimeException(e);
        }
    }

    public void testContains() {
        var builder = new Route.RouteBuilder();

        try {
            var route = builder
                    .withCoordinate(new Coordinate(CoordinateType.ROUTE, 10, 10))
                    .withCoordinate(new Coordinate(CoordinateType.ROUTE, 11, 10))
                    .withCoordinate(new Coordinate(CoordinateType.ROUTE, 11, 9)).build();
            if(!route.contains(10, 10)){
                fail("Expected route to find that starting coordinate (10, 10) is in route");
            }
            else if(!route.contains(11, 9)){
                fail("Expected route to find that ending coordinate (11, 9) is in route");
            }
            else if(route.contains(9, 9)){
                fail("Expected route to evaluate that (9, 9) is not in route");
            }
            else{
                pass("All results for contains were as expected.");
            }
        }
        catch(Exception e){
            fail("Some other exception was thrown.");
            throw new RuntimeException(e);
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
