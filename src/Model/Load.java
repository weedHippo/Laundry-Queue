
package Model;

import java.time.LocalDateTime;

public class Load {
    private int loadId;
    private int cstIdFk;

    private double loadWeight;
    private ServiceStage loadStat;

    private LocalDateTime timestamp;

    public Load(int loadId, int cstIdFk,
                double loadWeight,
                ServiceStage loadStat,
                LocalDateTime timestamp) {

        this.loadId = loadId;
        this.cstIdFk = cstIdFk;
        this.loadWeight = loadWeight;
        this.loadStat = loadStat;
        this.timestamp = timestamp;
    }

    public int getLoadId() {
        return loadId;
    }

    public int getCstIdFk() {
        return cstIdFk;
    }

    public double getLoadWeight() {
        return loadWeight;
    }

    public ServiceStage getLoadStat() {
        return loadStat;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setCstIdFk(int cstIdFk) {
        this.cstIdFk = cstIdFk;
    }

    public void setLoadId(int loadId) {
        this.loadId = loadId;
    }
}
