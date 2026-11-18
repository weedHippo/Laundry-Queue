package Service.Managers;

import Model.Customer;
import Model.Load;
import Model.ServiceStage;
import Model.ServiceType;
import Service.DataManager.Repo.CustomerRepo;
import Service.DataManager.Repo.LaundryStatRepo;

import java.time.LocalDateTime;
import java.util.List;

public class LaundryAssignmentManager {
        private final LaundryStatRepo laundryRepo = new LaundryStatRepo();

    public void registerLaundry(int cstID, double loadWeight, ServiceType st) {

        CustomerRepo customerRepo = new CustomerRepo();
        Customer customer = Validator.requireExists(customerRepo.findById(cstID), "Customer");
        Validator.requirePositive(loadWeight, "Laundry Weight");
        Validator.requireExists(st, "Service Type");

        ServiceStage initialStage = switch (st) {

            case FULL_WASH, WASHING_ONLY ->
                    ServiceStage.QUEUED_WASHER;

            case DRYER_ONLY ->
                    ServiceStage.QUEUED_DRYER;
        };

        LocalDateTime receivedAt = LocalDateTime.now();


        Load load = new Load(0, customer.getCustomerId(), loadWeight, initialStage, receivedAt,st);

        laundryRepo.addEntry(load);
    }

    public void updateLoadWeight(int loadId, double newWeight){

        Validator.requirePositiveId(loadId, "Load ID");
        Load load = Validator.requireExists(laundryRepo.findById(loadId), "Load ID");
        Validator.requirePositive(newWeight,newWeight + " is too low, please use the recommended weight" );

        Load updLoad = new Load(load.getLoadId(),load.getCstIdFk(),newWeight,load.getLoadStat(),load.getTimestamp(), load.getServiceType());

        laundryRepo.updateEntry(updLoad);
    }

    public void updateLoadState(int loadId, ServiceStage updStage){
        Validator.requirePositiveId(loadId, "Load ID");
        Load load = Validator.requireExists(laundryRepo.findById(loadId), "Load ID");
        ServiceStage currentStage = load.getLoadStat();

        Validator.requireExists(updStage, "Service Stage");

        boolean validTransition = switch (currentStage) {

            case QUEUED_WASHER ->
                            updStage == ServiceStage.WASHING ||
                            updStage == ServiceStage.CANCELED;

            case QUEUED_DRYER ->
                            updStage == ServiceStage.DRYING ||
                            updStage == ServiceStage.CANCELED;

            case WASHING ->
                            updStage == ServiceStage.TRANSFER ||
                            updStage == ServiceStage.COMPLETED ||
                            updStage == ServiceStage.INTERRUPTION;

            case TRANSFER ->
                            updStage == ServiceStage.QUEUED_DRYER ||
                            updStage == ServiceStage.CANCELED;

            case DRYING ->
                            updStage == ServiceStage.COMPLETED ||
                            updStage == ServiceStage.INTERRUPTION;

            case INTERRUPTION ->
                            updStage == ServiceStage.WASHING ||
                            updStage == ServiceStage.DRYING ||
                            updStage == ServiceStage.QUEUED_WASHER ||
                            updStage == ServiceStage.QUEUED_DRYER ||
                            updStage == ServiceStage.CANCELED;

            case COMPLETED, CANCELED -> false;
        };

        Validator.requireState(validTransition, "Status Transition failed from " + currentStage + " to " + updStage);
        Load updLoad = new Load(load.getLoadId(),load.getCstIdFk(),load.getLoadWeight(),updStage,load.getTimestamp(),load.getServiceType());
        laundryRepo.updateEntryState(updLoad);

    }

    public List<Load> getAllLoadAss(){
        return laundryRepo.findAll();
    }
}
