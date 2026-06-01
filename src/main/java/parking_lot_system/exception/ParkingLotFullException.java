package parking_lot_system.exception;

public class ParkingLotFullException extends RuntimeException {

    public ParkingLotFullException(String message) {
        super(message);
    }
}