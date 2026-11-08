package Model;

public class Machine {
    private int machineId;
    private String machineType;
    private String operationStat;

    public Machine(int machineId, String machineType, String operationStat) {
        this.machineId = machineId;
        this.machineType = machineType;
        this.operationStat = operationStat;
    }

    public int getMachineId() {
        return machineId;
    }

    public String getMachineType() {
        return machineType;
    }

    public String getOperationStat() {
        return operationStat;
    }
}
