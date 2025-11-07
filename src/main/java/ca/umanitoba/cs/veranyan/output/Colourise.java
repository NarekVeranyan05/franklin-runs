package ca.umanitoba.cs.veranyan.output;

public class Colourise {
    public static void red(String message) {
        System.out.printf("\u001B[31m%s\n\u001B[0m", message);
    }

    public static void cyan(String message) {
        System.out.printf("\u001B[36m%s\u001B[0m", message);
    }

}
