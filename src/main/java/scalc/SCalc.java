package scalc;

import scalc.exceptions.CalculationException;
import scalc.internal.SCalcController;

/**
 * The calculator class. Do not create the instance by yourself. Please use SCalcBuilder!
 */
public class SCalc<RETURN_TYPE> {
    private final SCalcOptions<RETURN_TYPE> options;

    SCalc(SCalcOptions<RETURN_TYPE> options) {
        this.options = options;
    }

    /**
     * Starts the calculation and returns the result in the previously given return type.
     * @return Calculation result as double, BigDecimal or whatever you have specified in the builder.
     * @throws CalculationException If any problems occur, the exception will be wrapped ad CalculationException.
     */
    public RETURN_TYPE calc() throws CalculationException {
        try {
            return SCalcController.calc(this);
        } catch (CalculationException e) {
            throw e;
        } catch (Throwable e) {
            throw new CalculationException("Unexpected error on calculation.", e);
        }
    }

    public SCalcOptions<RETURN_TYPE> getOptions() {
        return options;
    }
}
