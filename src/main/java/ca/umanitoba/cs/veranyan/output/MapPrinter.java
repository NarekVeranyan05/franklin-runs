package ca.umanitoba.cs.veranyan.output;

import ca.umanitoba.cs.veranyan.model.Profile;
import ca.umanitoba.cs.veranyan.model.map.Map;
import com.google.common.base.Preconditions;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/**
 * The printer class for the {@link Map}
 */
public class MapPrinter {
    private static final int METERS_PER_STEP = 10;

    // symbols to display on grid
    public static final String OBSTACLE_SLOT = "*";
    public static final String EMPTY_SLOT = ".";
    public static final String ROUTE_SLOT = ">";

    private final Map map;

    /**
     * Constructor for MapPrinter.
     * @param map the Map singleton to be printed. Must not be {@code null}.
     */
    public MapPrinter(Map map){
        this.map = map;

        checkMapPrinter();
    }

    public void print() {
        checkMapPrinter();

        System.out.println("Legend:");
        System.out.printf("Grid layout: %dx%d.\n", map.getWidth(), map.getLength());
        System.out.println("Obstacle coordinate: " + OBSTACLE_SLOT);
        System.out.println("Route coordinate: " + ROUTE_SLOT);
        System.out.println("Empty coordinate: " + EMPTY_SLOT);

        /*
        calculating largest number of digits for both x- and y-coordinates
        in order to use for indenting and proper output formatting.
         */

        int maxYLen; //
        if (map.getWidth() > 9)
            //
            maxYLen = (int) Math.log10(map.getWidth() - 1) + 1; // the number of digits in the largest x-coordinate
        else { // map width 1 crash prevention
            maxYLen = 1;//
        }

        int maxXLen; //
        if (map.getLength() > 9)
            //
            maxXLen = (int) Math.log10(map.getLength() - 1) + 1; // the number of digits in the largest y-coordinate
        else { // map length 1 crash prevention
            maxXLen = 1; //
        }

        // printing y-coordinates
        System.out.printf("%" + (maxXLen + 1) + "s", ""); // indent for y-coordinate
        for (int y = 1; y <= map.getWidth(); y++)
            System.out.printf(" %" + maxYLen + "d", y);
        System.out.println();

        for (int x = 1; x <= map.getLength(); x++) { // x-coordinates
            System.out.printf("%" + maxXLen + "d|", x); // printing the x-coordinate

            for (int y = 1; y <= map.getWidth(); y++) { // y-coordinates
                switch (map.getCoordinateType(x, y)) {
                    case ROUTE -> System.out.printf(" %" + maxYLen + "s", ROUTE_SLOT);
                    case OBSTACLE -> System.out.printf(" %" + maxYLen + "s", OBSTACLE_SLOT);
                    case EMPTY -> System.out.printf(" %" + maxYLen + "s", EMPTY_SLOT);
                }
            }
            System.out.println();
        }

//        printWithBorders();
    }

    // FIXME
    public void printWithBorders(){
        int maxYLen; //
        if (map.getWidth() > 9)
            //
            maxYLen = (int) Math.log10(map.getWidth() - 1) + 1; // the number of digits in the largest x-coordinate
        else { // map width 1 crash prevention
            maxYLen = 1;//
        }

        int maxXLen; //
        if (map.getLength() > 9)
            //
            maxXLen = (int) Math.log10(map.getLength() - 1) + 1; // the number of digits in the largest y-coordinate
        else { // map length 1 crash prevention
            maxXLen = 1; //
        }

        System.out.printf("%" + (maxXLen + 1) + "s", ""); // indent for y-coordinate
        for (int i = 0; i < map.getWidth() + 2; i++)
            System.out.printf(" %" + maxYLen + "d", i);
        System.out.println();

        for(int x = 0; x < map.getLength() + 2; x++){
            System.out.printf("%" + maxXLen + "d|", x); // printing the y-coordinate

            for(int y = 0; y < map.getWidth() + 2; y++){
                switch (map.getCoordinateType(x, y)) {
                    case ROUTE -> System.out.printf(" %" + maxYLen + "s", ROUTE_SLOT);
                    case OBSTACLE -> System.out.printf(" %" + maxYLen + "s", OBSTACLE_SLOT);
                    case EMPTY -> System.out.printf(" %" + maxYLen + "s", EMPTY_SLOT);
                    case BORDER -> System.out.printf(" %" + maxYLen + "s", "X");
                }
            }
            System.out.println();
        }
    }

    /**
     * Prints the Map and all its Activities. This method prints to standard output (`System.out`).
     * @param profile the profile for which to summarise info. Must not be {@code null}.
     */
    public void print(Profile profile){
        print();
        System.out.println();

        // printing Activity distance summary for week and month
        LocalDate today = LocalDate.now();
        int numStepsWeek = profile.getTotalNumSteps(today, ChronoUnit.WEEKS);
        int numStepsMonth = profile.getTotalNumSteps(today, ChronoUnit.MONTHS);

        System.out.println("This week, you have cycled for " + (numStepsWeek * METERS_PER_STEP) + " meters.");
        System.out.println("This month, you have cycled for " + (numStepsMonth * METERS_PER_STEP) + " meters.");

        checkMapPrinter();
    }

    /**
     * Ensures MapPrinter invariants are not violated.
     */
    private void checkMapPrinter(){
        Preconditions.checkNotNull(map, "map cannot be null.");
    }
}
