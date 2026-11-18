
package App.Dubuggers;

import Service.Managers.MachineAssignmentManager;
import java.util.Scanner;

public class Quarantine {

    public static void main(String[] args) {

        MachineAssignmentManager mam = new MachineAssignmentManager();
        Scanner scanner = new Scanner(System.in);

        int fullWashLoadId = 94;
        int dryerOnlyLoadId = 95;

        int washerId = 61;
        int dryerId = 31;

        System.out.println("=== INTERRUPTION TEST ===");

        // TEST 1: FULL_WASH
        System.out.println("\n[1] FULL_WASH INTERRUPTION");

        mam.assignMachine(fullWashLoadId, washerId, 10);

        System.out.print("Enter new washer assignment ID: ");
        int washerAssignmentId = Integer.parseInt(scanner.nextLine());

        mam.startSession(washerAssignmentId);

        System.out.println("Washing started.");
        System.out.println("Press ENTER to simulate breakdown.");
        scanner.nextLine();

        mam.interruptSession(washerAssignmentId);

        System.out.println("FULL_WASH interruption successful!");

        // TEST 2: DRYER_ONLY
        System.out.println("\n[2] DRYER_ONLY INTERRUPTION");

        mam.assignMachine(dryerOnlyLoadId, dryerId, 10);

        System.out.print("Enter new dryer assignment ID: ");
        int dryerAssignmentId = Integer.parseInt(scanner.nextLine());

        mam.startSession(dryerAssignmentId);

        System.out.println("Drying started.");
        System.out.println("Press ENTER to simulate breakdown.");
        scanner.nextLine();

        mam.interruptSession(dryerAssignmentId);

        System.out.println("DRYER_ONLY interruption successful!");

        System.out.println("\n=== ALL TESTS FINISHED ===");

        scanner.close();
    }
}
