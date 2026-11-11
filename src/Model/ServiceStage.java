package Model;


//This enums is to trigger specific
public enum ServiceStage {
    QUEUED("Waiting"),
    WASHING("Washing"),
    TRANSFER("Transferring"),
    DRYING("Drying"),
    COMPLETED("Finished"),
    CANCELED("Canceled");

    private final String servState;
    ServiceStage(String servState) {
        this.servState = servState;
    }

    public String getServState() {
        return servState;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
