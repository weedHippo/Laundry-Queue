package Model;

public enum ServiceStage {
    QUEUED_WASHER("Waiting_Washer"),
    QUEUED_DRYER("Waiting_Dryer"),
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
