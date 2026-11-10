package Model;

public enum MachineState {
    AVAILABLE("Available"),
    IN_USE("Occupied"),
    MAINTENANCE("Maintenance"),
    OUT_OF_SERVICE("Broken");

    private final String macState;

    MachineState(String macState) {
        this.macState = macState;
    }

    public String getMacState() {
        return macState;
    }

    @Override
    public String toString() {
        return macState;
    }
}