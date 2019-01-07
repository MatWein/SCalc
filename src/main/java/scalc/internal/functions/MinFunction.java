package scalc.internal.functions;

import scalc.SCalcOptions;

import java.math.BigDecimal;
import java.util.List;

public class MinFunction implements FunctionImpl {
    public static final MinFunction INSTANCE = new MinFunction();

    @Override
    public BigDecimal call(SCalcOptions<?> options, List<BigDecimal> functionParams) {
        if (functionParams.isEmpty()) {
            return new BigDecimal(0).setScale(options.getCalculationScale(), options.getCalculationRoundingMode());
        }

        BigDecimal result = functionParams.get(0);
        for (BigDecimal param : functionParams) {
            if (param.compareTo(result) < 0) {
                result = param;
            }
        }

        return result;
    }
}
