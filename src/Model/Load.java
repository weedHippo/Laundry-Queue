package Model;

public class Load {
    //PRIMARY KEY
    private int loadId;
    //FOREIGN KEY
    private int cstIdFk;

    private double loadWeight;
    private ServiceStage loadStat;


    public Load(int loadId, int cstIdFk, double loadWeight, ServiceStage loadStat) {
        this.loadId = loadId;
        this.cstIdFk = cstIdFk;
        this.loadWeight = loadWeight;
        this.loadStat = loadStat;
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

    public void setCstIdFk(int cstIdFk) {
        this.cstIdFk = cstIdFk;
    }

    public void setLoadId(int loadId) {
        this.loadId = loadId;
    }



}
