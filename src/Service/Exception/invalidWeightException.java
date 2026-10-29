package Service.Exception;

//Invalid as in the weight of the laundry exceeds its assigned weight
public class invalidWeightException extends RuntimeException {
    public invalidWeightException(String message) {
        super(message);
    }
}
