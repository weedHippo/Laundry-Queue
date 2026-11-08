package Model;

import java.time.LocalDateTime;

public class LaundryStat {
    private int loadId;
    private int customerId;
    private String queueStat;
    private LocalDateTime receiveAt;

    public LaundryStat(LocalDateTime receiveAt, String queueStat, int customerId, int loadId) {
        this.receiveAt = receiveAt;
        this.queueStat = queueStat;
        this.customerId = customerId;
        this.loadId = loadId;
    }

    public LocalDateTime getReceiveAt() {
        return receiveAt;
    }

    public String getQueueStat() {
        return queueStat;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getLoadId() {
        return loadId;
    }
}

