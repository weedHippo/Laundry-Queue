package Service.Managers;

import Model.*;
import Service.DataManager.Repo.CustomerRepo;
import Service.DataManager.Repo.LaundryStatRepo;
import Service.DataManager.Repo.MachineAsignmentRepo;
import Service.DataManager.Repo.MachineRepo;
import Service.Exception.UnavailableMachineException;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;

public class MachineAssignmentManager {
    public LaundryStatRepo laundryStatRepo = new LaundryStatRepo();
    public MachineRepo machineRepo = new MachineRepo();
    public MachineAsignmentRepo mar = new MachineAsignmentRepo();

    public MachineManager mm = new MachineManager();
    public LaundryAssignmentManager lam = new LaundryAssignmentManager();
    private final int TEMP_ID = 0;
    private final int DRYER_DURATION = 40;


    public void assignMachine(int loadId, int machineId, int durationMinutes) {
        // STEP 1: Validate input
        Validator.requirePositiveId(loadId, "Load ID is negative");
        Validator.requirePositiveId(machineId, "Machine ID is negative");
        Validator.requirePositive(durationMinutes, "Duration is too low");

        // STEP 2: Retrieve load and machine
        Load load = Validator.requireExists(laundryStatRepo.findById(loadId), "You haven't registered a laundry with the ID: " + loadId);
        Machine machine = Validator.requireExists(machineRepo.findById(machineId), "The Machine you somehow Assigned doesn't exist");

        // STEP 3: Check machine availability
        boolean isAvailable = machine.getMachineState() == MachineState.AVAILABLE;
        
        if(!isAvailable){
            throw new UnavailableMachineException("Machine Chosen is Not Available");
        }

        // STEP 4: Check machine capacity
        System.out.println("Machine ID: " + machine.getMachineId());
        System.out.println("Machine Capacity: " + machine.getMachineCapacity());
        System.out.println("Load Weight: " + load.getLoadWeight());

        if(machine.getMachineCapacity() < load.getLoadWeight()){
            throw new IllegalArgumentException("Weight is over the recommended washer/dryer capacity");
        }
        // STEP 5: Check machine type matches queue
        boolean chkStat;

        if (load.getLoadStat() == ServiceStage.INTERRUPTION) {
            // An interrupted load must continue on the same kind of machine.
            // The previous assignment is the source of truth for this phase.
            MachineType interruptedType = findInterruptedMachineType(load.getLoadId());
            chkStat = machine.getMachineType() == interruptedType;
        } else {
            chkStat = switch (load.getLoadStat()) {
                case QUEUED_WASHER ->
                        machine.getMachineType() == MachineType.WASHER;
                case QUEUED_DRYER, TRANSFER ->
                        machine.getMachineType() == MachineType.DRYER;
                default -> false;
            };
        }

        if(!chkStat){
            throw new IllegalArgumentException("Machine type does not match load queue status.");
        }else{
            machine.setMachineState(MachineState.IN_USE);
        }

        // STEP 6: Check neither is already assigned
        boolean loadAssigned = mar.hasActiveLoadAssignment(load.getLoadId());
        boolean machineAssigned = mar.hasActiveMachineAssignment(machine.getMachineId());
        Validator.requireState(!loadAssigned, "Load is already assigned!");
        Validator.requireState(!machineAssigned,"Machine is already occupied!");

        // STEP 7: Create assignment and update states
        MachineAssignment machineAssignment = new MachineAssignment(TEMP_ID,load.getLoadId(),machine.getMachineId(),null,durationMinutes,null);
        mar.addEntry(machineAssignment);
        mm.updateMachineState(machine.getMachineId(), MachineState.IN_USE);
    }


    public void startSession(int assignmentId) {
        MachineAssignment machineAssignment = Validator.requireExists(mar.findById(assignmentId), "Machine Assignment");
        Load load = Validator.requireExists(laundryStatRepo.findById(machineAssignment.getLoadId()), "Load");
        Machine machine = Validator.requireExists(machineRepo.findById(machineAssignment.getMachineId()),"Machine");
        Validator.requireState(machineAssignment.getStartedAt() == null && machineAssignment.getEndedAt() == null, "Session is already started!");
        Validator.requirePositive(machineAssignment.getDurationMinutes(), "The Duration is SOMEHOW NEGATIVE??! HOW??!!");


        if (machine.getMachineType() == MachineType.WASHER) {
            lam.updateLoadState(
                    machineAssignment.getLoadId(),
                    ServiceStage.WASHING
            );

        } else if (machine.getMachineType() == MachineType.DRYER) {
            lam.updateLoadState(
                    machineAssignment.getLoadId(),
                    ServiceStage.DRYING
            );
        }

        machineAssignment.setStartedAt(LocalDateTime.now());
        mar.updateStateStart(machineAssignment);
    }

    public long getRemainingSeconds(int assignmentId) {
        MachineAssignment ma = Validator.requireExists(mar.findById(assignmentId), "Machine Assignment");
        Validator.requireState(ma.getStartedAt() != null, "Session hasn't started!");
        LocalDateTime expectedEnd = ma.getStartedAt().plusMinutes(ma.getDurationMinutes());
        long remaining = Duration.between(LocalDateTime.now(), expectedEnd).getSeconds();


        return Math.max(0, remaining);
    }

    public void completeSession(int assignmentId) {

        // STEP 1: Retrieve records
        MachineAssignment ma = Validator.requireExists(mar.findById(assignmentId), "Machine Assignment");

        Load load = Validator.requireExists(laundryStatRepo.findById(ma.getLoadId()), "Load");

        Machine machine = Validator.requireExists(machineRepo.findById(ma.getMachineId()), "Machine");

        // STEP 2: Validate session
        Validator.requireState(ma.getStartedAt() != null && ma.getEndedAt() == null, "Session is not active!");

        Validator.requireState(getRemainingSeconds(assignmentId) == 0, "Session is still running!");

        // STEP 3: Record completion
        ma.setEndedAt(LocalDateTime.now());
        mar.updateStateEnd(ma);

        // STEP 4: Release machine if necessary
        if (machine.getMachineState() != MachineState.AVAILABLE) {
            mm.updateMachineState(machine.getMachineId(), MachineState.AVAILABLE);
        }

        // STEP 5: Update load status
        if (machine.getMachineType() == MachineType.WASHER) {

            if (load.getServiceType() == ServiceType.FULL_WASH) {
                lam.updateLoadState(ma.getLoadId(), ServiceStage.TRANSFER);
            } else {
                lam.updateLoadState(ma.getLoadId(), ServiceStage.COMPLETED);
            }

        } else if (machine.getMachineType() == MachineType.DRYER) {

            lam.updateLoadState(ma.getLoadId(), ServiceStage.COMPLETED  );
        }
    }


    public void transferLoad(int loadId, int dryerId) {

        Load load = Validator.requireExists(laundryStatRepo.findById(loadId), "Load");
        Machine dryer = Validator.requireExists(machineRepo.findById(dryerId), "Dryer");
        Validator.requireState(dryer.getMachineState() == MachineState.AVAILABLE, "Dryer is unavailable!");
        Validator.requireState(dryer.getMachineCapacity() >= load.getLoadWeight(), "Load exceeds dryer capacity!");
        Validator.requireState(!mar.hasActiveMachineAssignment(dryerId), "Dryer already has an active assignment!");

        Validator.requireState(load.getLoadStat() == ServiceStage.TRANSFER, "Session isn't finished yet");
        Validator.requireState(dryer.getMachineType() == MachineType.DRYER,"The Machine You Chose is not a Dryer");
        Validator.requireState(load.getServiceType() == ServiceType.FULL_WASH, "Invalid Service Type");


        assignMachine(loadId, dryerId, DRYER_DURATION);
        lam.updateLoadState(loadId, ServiceStage.QUEUED_DRYER);
    }

    public void interruptSession(int assignmentId) {

        MachineAssignment ma = Validator.requireExists(mar.findById(assignmentId), "Machine Assignment");
        Load load = Validator.requireExists(laundryStatRepo.findById(ma.getLoadId()), "Load");
        Machine machine = Validator.requireExists(machineRepo.findById(ma.getMachineId()), "Machine");
        Validator.requireState(ma.getStartedAt() != null && ma.getEndedAt() == null, "Cannot interrupt an inactive session!");

        ma.setEndedAt(LocalDateTime.now());
        mar.updateStateEnd(ma);
        mm.updateMachineState(machine.getMachineId(), MachineState.OUT_OF_SERVICE);
        lam.updateLoadState(load.getLoadId(),ServiceStage.INTERRUPTION);
    }


    public void reassignLoad(int loadId, int newMachineId, int assignmentId, MachineType interruptedType) {
        Load load = Validator.requireExists(laundryStatRepo.findById(loadId), "Load");
        Machine machine = Validator.requireExists(machineRepo.findById(newMachineId), "Replacement Machine");
        MachineAssignment machineAssignment = Validator.requireExists(mar.findById(assignmentId), "Assigned machine");
        Machine originalMachine = Validator.requireExists(machineRepo.findById(machineAssignment.getMachineId()), "Original machine");

        // STEP 1: Validate interruption
        Validator.requireState(load.getLoadStat() == ServiceStage.INTERRUPTION, "Load is not     interrupted!");
        Validator.requireState(machineAssignment.getLoadId() == loadId, "Assignment does not belong to this load!");

        // STEP 2: Verify previous assignment is closed
        Validator.requireState(machineAssignment.getEndedAt() != null, "Previous assignment is not closed!");
        Validator.requireState(machineAssignment.getStartedAt() != null, "Assignment was never started!");
        Validator.requireState(!mar.hasActiveLoadAssignment(loadId), "Load still has an active assignment!");

        // STEP 3: Validate replacement machine
        Validator.requireState(machine.getMachineState() == MachineState.AVAILABLE, "Replacement machine is unavailable!");
        Validator.requireState(machine.getMachineCapacity() >= load.getLoadWeight(), "Load exceeds machine capacity!");
        Validator.requireState(!mar.hasActiveMachineAssignment(newMachineId), "Replacement machine is occupied!");
        Validator.requireState(originalMachine.getMachineType() == interruptedType, "Original machine type does not match the interrupted phase!");
        Validator.requireState(machine.getMachineType() == originalMachine.getMachineType(), "Replacement machine type is incompatible with the interrupted phase!");

        // STEP 4: Calculate remaining duration
        LocalDateTime start = machineAssignment.getStartedAt();
        LocalDateTime end = machineAssignment.getEndedAt();

        long elapsedSeconds = Duration.between(start, end).getSeconds();
        long remainingSeconds = Math.max(0,
                machineAssignment.getDurationMinutes() * 60L - elapsedSeconds);
        long remainingTime = (long) Math.ceil(remainingSeconds / 60.0);
        Validator.requireState(remainingTime > 0, "No remaining duration to reassign!");

        // STEP 5: Create replacement assignment
        assignMachine(loadId, newMachineId, (int) remainingTime);
    }


    public void cancelAssignment(int assignmentId) {

        MachineAssignment ma = Validator.requireExists(mar.findById(assignmentId), "Machine Assignment");
        Load load = Validator.requireExists(laundryStatRepo.findById(ma.getLoadId()), "Load");
        Machine machine = Validator.requireExists(machineRepo.findById(ma.getMachineId()), "Machine");

        // An interrupted session is already closed, but may still be canceled.
        if (load.getLoadStat() == ServiceStage.INTERRUPTION
                && ma.getStartedAt() != null && ma.getEndedAt() != null) {
            lam.updateLoadState(load.getLoadId(), ServiceStage.CANCELED);
            mar.deleteEntry(assignmentId);
            return;
        }

        // A normal queued assignment can only be canceled before it starts.
        Validator.requireState(ma.getStartedAt() == null, "Cannot cancel a started session!");
        Validator.requireState(ma.getEndedAt() == null, "Assignment is already closed!");
        Validator.requireState(load.getLoadStat() != ServiceStage.COMPLETED, "Cannot cancel a completed load!");

        // STEP 2: Restore queue status
        switch (machine.getMachineType()) {
            case WASHER ->
                    load.setLoadStat(ServiceStage.QUEUED_WASHER);

            case DRYER ->
                    load.setLoadStat(ServiceStage.QUEUED_DRYER);
        }

        // STEP 3: Release machine
        machine.setMachineState(MachineState.AVAILABLE);

        // STEP 4: Persist changes
        mar.deleteEntry(assignmentId);

        laundryStatRepo.updateEntry(load);
        machineRepo.updateEntry(machine);
    }

    private MachineType findInterruptedMachineType(int loadId) {
        MachineType result = null;
        for (MachineAssignment assignment : mar.findAll()) {
            if (assignment.getLoadId() == loadId
                    && assignment.getStartedAt() != null
                    && assignment.getEndedAt() != null) {
                Machine machine = machineRepo.findById(assignment.getMachineId());
                if (machine != null) result = machine.getMachineType();
            }
        }
        return Validator.requireExists(result, "Interrupted machine type");
    }
}
