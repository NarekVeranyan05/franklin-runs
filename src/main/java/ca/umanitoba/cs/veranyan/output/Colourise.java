package ca.umanitoba.cs.veranyan.output;

/**
 * {@link Colourise} colourises a {@link String} and prints out to the standard output stream (`System.out`)
 */
public class Colourise {
    /**
     * Prints the given message in red to the standard output stream (`System.out`)
     * @param message the message to print
     */
    public static void red(String message) {
        System.out.printf("\u001B[31m%s\u001B[0m", message);
    }

    /**
     * Prints the given message in cyan to the standard output stream (`System.out`)
     * @param message the message to print
     */
    public static void cyan(String message) {
        System.out.printf("\u001B[36m%s\u001B[0m", message);
    }

    /**
     * Prints the given message in green to the standard output stream (`System.out`)
     * @param message the message to print
     */
    public static void green(String message){
        System.out.printf("\u001B[32m%s\u001B[0m", message);
    }

}
