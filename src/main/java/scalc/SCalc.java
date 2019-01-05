package scalc;

import scalc.exceptions.CalculationException;
import scalc.internal.SCalcController;

public class SCalc<RETURN_TYPE> {
    private final SCalcOptions<RETURN_TYPE> options;

    SCalc(SCalcOptions<RETURN_TYPE> options) {
        this.options = options;
    }

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
