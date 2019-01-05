package scalc.exceptions;

public class CalculationException extends RuntimeException {
    public CalculationException() {
    }

    public CalculationException(String message) {
        super(message);
    }

    public CalculationException(Throwable cause) {
        super(cause);
    }

    public CalculationException(String message, Throwable cause) {
        super(message, cause);
    }
}
