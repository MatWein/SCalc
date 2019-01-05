package scalc;

import scalc.internal.SCalcController;

public class SCalc<RETURN_TYPE> {
    private final SCalcOptions<RETURN_TYPE> options;

    SCalc(SCalcOptions<RETURN_TYPE> options) {
        this.options = options;
    }

    public RETURN_TYPE calc() {
        return SCalcController.calc(this);
    }

    public SCalcOptions<RETURN_TYPE> getOptions() {
        return options;
    }
}
