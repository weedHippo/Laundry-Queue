package App.Dubuggers;

import Model.Machine;
import Model.MachineState;
import Model.MachineType;
import Service.DataManager.Repo.MachineRepo;

public class Sandbox {

    public static void main(String[] args) {

        MachineRepo repo = new MachineRepo();

        // ID = 0 because Oracle generates it
        Machine washer = new Machine(
                0,
                8.5,
                MachineType.DRYER,
                MachineState.AVAILABLE
        );

        // Insert into Oracle
        repo.addEntry(washer);

        // Check if generated ID was assigned
        System.out.println("Machine ID: " + washer.getMachineId());
        System.out.println("Machine Type: " + washer.getMachineType());
        System.out.println("Capacity: " + washer.getMachineCapacity());
        System.out.println("Status: " + washer.getMachineState());
    }
}