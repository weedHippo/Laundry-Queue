package Model;

public enum MachineState {
    AVAILABLE("Available"),
    IN_USE("In_Use"),
    MAINTENANCE("Maintenance"),
    OUT_OF_SERVICE("Out_Of_Service"),
    TRANSFER("Transfer");

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