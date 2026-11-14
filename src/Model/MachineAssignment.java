
package Model;

import java.time.LocalDateTime;

public class MachineAssignment {

    private int assignmentId;
    private int loadId;
    private int machineId;

    private LocalDateTime startedAt;
    private int durationMinutes;
    private LocalDateTime endedAt;

    public MachineAssignment(int assignmentId, int loadId, int machineId, LocalDateTime startedAt, int durationMinutes, LocalDateTime endedAt) {

        this.assignmentId = assignmentId;
        this.loadId = loadId;
        this.machineId = machineId;
        this.startedAt = startedAt;
        this.durationMinutes = durationMinutes;
        this.endedAt = endedAt;
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public int getLoadId() {
        return loadId;
    }

    public int getMachineId() {
        return machineId;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
