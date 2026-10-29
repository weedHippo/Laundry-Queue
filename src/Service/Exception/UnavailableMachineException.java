package Service.Exception;

public class UnavailableMachineException extends RuntimeException {
    public UnavailableMachineException(String message) {
        super(message);
    }
}
