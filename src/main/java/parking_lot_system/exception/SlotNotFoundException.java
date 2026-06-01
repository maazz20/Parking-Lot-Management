package parking_lot_system.exception;

public class SlotNotFoundException extends RuntimeException {

    public SlotNotFoundException(String message) {
        super(message);
    }
}