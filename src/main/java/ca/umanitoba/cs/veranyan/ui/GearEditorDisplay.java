package ca.umanitoba.cs.veranyan.ui;

import ca.umanitoba.cs.veranyan.logic.GearManager;
import ca.umanitoba.cs.veranyan.model.exceptions.*;
import ca.umanitoba.cs.veranyan.model.gear.Gear;
import ca.umanitoba.cs.veranyan.model.gear.GearType;
import ca.umanitoba.cs.veranyan.output.Colourise;
import ca.umanitoba.cs.veranyan.output.GearPrinter;
import com.google.common.base.Preconditions;

import java.util.Scanner;

/**
 * The {@code GearEditorDisplay} class manages the UI interaction for managing gears,
 * such as adding or selecting them for an activity.
 */
public class GearEditorDisplay {
    private final GearManager gearManager;
    private final Scanner keyboard = new Scanner(System.in);

    public GearEditorDisplay(GearManager gearManager){
        this.gearManager = gearManager;

        checkGearEditorDisplay();
    }

    /**
     * Prompts the user to create a new gear.
     * @return the new gear. Must not be {@code null}
     */
    public Gear gearInsertionScreen(){
        checkGearEditorDisplay();

        // setup
        var builder = new Gear.GearBuilder();
        Gear newGear = null;

        // gear attributes
        String gearTypeInput = null;
        GearType gearType;
        String name = null;
        int avgSpeed;

        // gear type insertion
        for(var type : GearType.values())
            System.out.println(" - " + type.toString());

        do{
            Colourise.cyan("Enter gear type: ");

            try{
                gearTypeInput = keyboard.nextLine();
                gearType = GearType.fromString(gearTypeInput);
                builder.type(gearType);
            } catch (InvalidGearTypeException e) {
                Colourise.red(gearTypeInput + " is not a valid Gear type. Valid types are one of: ");
                for(var type : GearType.values())
                    Colourise.red(" - " + type.toString());

                gearTypeInput = null;
            }
        } while (gearTypeInput == null);

        // gear average speed insertion
        do {
            Colourise.cyan("Enter gear average speed, measured in meters per second: ");
            try {
                avgSpeed = keyboard.nextInt();
                builder.avgSpeed(avgSpeed);
            } catch (NonPositiveSpeedException e) {
                Colourise.red("Your gear average speed must be positive, e.g 20");
                avgSpeed = -1;
            }catch (Exception e){
                Colourise.red("Your gear average speed must be a number, e.g. 20");
                avgSpeed = -1;
            }

            keyboard.nextLine();
        } while (avgSpeed == -1);


        // gear name insertion and build
        do{
            Colourise.cyan("Enter gear name: ");
            try{
                name = keyboard.nextLine();
                builder.name(name);

                newGear = builder.build();
                gearManager.addGear(newGear);
            } catch (BlankNameException e) {
                Colourise.red("Your gear name must contain at least one letter, e.g mybike\n");

                name = null;
            } catch (DuplicateGearException e) {
                Colourise.red("A gear with existing name " + name + " already exists.\n");
                Colourise.red("Your new gear name cannot be one of:\n");
                for(var gear : gearManager.getGears()){
                    new GearPrinter(gear).print();
                    System.out.println();
                }

                name = null;
            }
        } while (name == null);

        checkGearEditorDisplay();

        return newGear;
    }

    /**
     * Prompts the user to select a gear.
     * @return the new gear.
     */
    public Gear gearSelectionScreen(){
        checkGearEditorDisplay();

        String gearNameInput = null;
        Gear selectedGear;
        do {
            for(var gear : gearManager.getGears()){
                System.out.print(" - ");
                new GearPrinter(gear).print();
                System.out.println();
            }

            Colourise.cyan("Enter selected gear name: ");
            try {
                gearNameInput = keyboard.nextLine();
                selectedGear = gearManager.getGear(gearNameInput);
            } catch (InvalidGearNameException e) {
                Colourise.red("Profile does not have gear with name " + gearNameInput + "\n");
                Colourise.red("A valid gear name is one of:\n");

                selectedGear = null;
            }
        } while(selectedGear == null);

        checkGearEditorDisplay();

        return selectedGear;
    }

    /**
     * Prompts the user to remove a gear.
     * Removes the selected gear.
     */
    public void gearRemovalScreen() {
        Gear toRemove = gearSelectionScreen();
        gearManager.removeGear(toRemove);
    }

    private void checkGearEditorDisplay(){
        Preconditions.checkNotNull(gearManager, "gearManager cannot be null");
        Preconditions.checkNotNull(keyboard, "keyboard cannot be null");
    }
}
