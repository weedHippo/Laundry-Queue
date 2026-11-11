package Model;

public class Machine {

    private int machineId;
    private double machineCapacity;
    private MachineType machineType;
    private MachineState machineState;

    public Machine(int machineId, double machineCapacity, MachineType machineType, MachineState machineState) {
        this.machineId = machineId;
        this.machineType = machineType;
        this.machineState = machineState;
        this.machineCapacity = machineCapacity;
    }

    public int getMachineId() {
        return machineId;
    }

    public MachineType getMachineType() {
        return machineType;
    }

    public MachineState getMachineState() {
        return machineState;
    }

    public double getMachineCapacity() {
        return machineCapacity;
    }

    public void setMachineState(MachineState machineState) {
        this.machineState = machineState;
    }

    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }

    @Override
    public String toString() {
        return machineId + " - " + machineType
                + " - " + machineState;
    }
}