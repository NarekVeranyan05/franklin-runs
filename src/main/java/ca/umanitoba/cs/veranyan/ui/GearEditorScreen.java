package ca.umanitoba.cs.veranyan.ui;

import ca.umanitoba.cs.veranyan.logic.ProfileRegistry;
import ca.umanitoba.cs.veranyan.model.exceptions.GearNotFoundException;
import ca.umanitoba.cs.veranyan.model.Activity;
import ca.umanitoba.cs.veranyan.model.exceptions.*;
import ca.umanitoba.cs.veranyan.model.gear.Gear;
import ca.umanitoba.cs.veranyan.model.gear.GearType;
import ca.umanitoba.cs.veranyan.output.Colourise;
import ca.umanitoba.cs.veranyan.output.GearPrinter;
import com.google.common.base.Preconditions;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * The {@link GearEditorScreen} class manages the UI interaction for managing gears,
 * such as adding or selecting them for an {@link Activity}.
 */
public class GearEditorScreen {
    private final ProfileRegistry profileRegistry;
    private final Scanner keyboard;

    public GearEditorScreen(ProfileRegistry profileRegistry, Scanner scanner){
        this.profileRegistry = profileRegistry;
        this.keyboard = scanner;

        checkGearEditorDisplay();
    }

    /**
     * Prompts the user to create a new {@link Gear}
     */
    public void gearInsertionScreen(){
        checkGearEditorDisplay();

        // setup
        var builder = new Gear.GearBuilder();

        // gear type insertion
        promptGearType(builder);

        // gear average speed insertion
        promptAvgSpeed(builder);

        // gear name insertion and build
        String name = null;
        do{
            try{
                name = promptGearName(builder);
                var gear = builder.build();
                profileRegistry.addGear(gear);
            } catch (DuplicateGearException e) {
                Colourise.red("A gear with name " + name + " already exists. Your gear name must be unique.\n");

                name = null;
            }
        } while (name == null);


        checkGearEditorDisplay();

    }

    /**
     * Prompts the user to provide gear type for the new {@link Gear}
     * @param builder the builder for the {@link Gear}. Must not be {@code null}
     */
    private void promptGearType(Gear.GearBuilder builder) {
        Preconditions.checkNotNull(builder, "builder must not be null");
        checkGearEditorDisplay();

        String gearTypeInput = null;
        GearType gearType;

        for(var type : GearType.values())
            System.out.println(" - " + type.toString());

        do{
            Colourise.cyan("Enter gear type: ");

            try{
                gearTypeInput = keyboard.nextLine().trim();
                gearType = GearType.fromString(gearTypeInput);
                builder.type(gearType);
            } catch (InvalidGearTypeException e) {
                Colourise.red(gearTypeInput + " is not a valid gear type. Valid types are one of:\n");
                for(var type : GearType.values())
                    Colourise.red(" - " + type.toString() + "\n");

                gearTypeInput = null;
            }
        } while (gearTypeInput == null);

        checkGearEditorDisplay();
    }

    /**
     * Prompts the user to provide average speed for the new {@link Gear}
     * @param builder the builder for the {@link Gear}. Must not be {@code null}
     */
    private void promptAvgSpeed(Gear.GearBuilder builder) {
        Preconditions.checkNotNull(builder, "builder must not be null");
        checkGearEditorDisplay();

        int avgSpeed = -1;

        do {
            Colourise.cyan("Enter gear average speed, measured in meters per second: ");
            try {
                avgSpeed = keyboard.nextInt();
                builder.avgSpeed(avgSpeed);
            } catch (NonPositiveSpeedException e) {
                Colourise.red(avgSpeed + " is not a valid speed. A valid speed must be positive, e.g 20\n");

                avgSpeed = -1;
            }catch (InputMismatchException e){
                Colourise.red("Invalid input: you must enter a whole number, e.g. 20\n");

                avgSpeed = -1;
            }

            keyboard.nextLine();
        } while (avgSpeed == -1);

        checkGearEditorDisplay();
    }

    /**
     * Prompts the user to provide a name for the new {@link Gear}
     * @param builder the builder for the {@link Gear}
     * @return the name of the {@link Gear}. Must not be {@code null}
     */
    private String promptGearName(Gear.GearBuilder builder) {
        Preconditions.checkNotNull(builder, "builder must not be null");
        checkGearEditorDisplay();

        String name;
        do{
            Colourise.cyan("Enter gear name: ");
            try{
                name = keyboard.nextLine().trim();
                builder.name(name);
            } catch (BlankNameException e) {
                Colourise.red("Your gear name must contain at least one letter, e.g m\n");

                name = null;
            }
        } while (name == null);

        checkGearEditorDisplay();

        return name;
    }

    /**
     * Prompts the user to select a {@link Gear}.
     * @return the new {@link Gear}.
     */
    public Gear gearSelectionScreen() {
        checkGearEditorDisplay();

        String gearNameInput = null;
        Gear selectedGear;
        do {
            for(var gear : profileRegistry.getCurrentProfile().getGears()){
                System.out.print(" - ");
                new GearPrinter(gear).print();
                System.out.println();
            }

            Colourise.cyan("Enter selected gear name: ");
            try {
                gearNameInput = keyboard.nextLine().trim();
                selectedGear = profileRegistry.getCurrentProfile().getGear(gearNameInput);
            } catch (GearNotFoundException e) {
                Colourise.red("You don't have gear with name " + gearNameInput + "\n");
                Colourise.red("A valid gear name is one of:\n");

                selectedGear = null;
            }
        } while(selectedGear == null);

        checkGearEditorDisplay();

        return selectedGear;
    }

    /**
     * Class invariants for GearEditorDisplay
     */
    private void checkGearEditorDisplay(){
        Preconditions.checkNotNull(profileRegistry, "gearManager cannot be null");
        Preconditions.checkNotNull(keyboard, "keyboard cannot be null");
    }
}
