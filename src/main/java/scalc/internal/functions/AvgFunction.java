package scalc.internal.functions;

import scalc.SCalcOptions;

import java.math.BigDecimal;
import java.util.List;

public class AvgFunction implements FunctionImpl {
    public static final AvgFunction INSTANCE = new AvgFunction();

    @Override
    public BigDecimal call(SCalcOptions<?> options, List<BigDecimal> functionParams) {
        if (functionParams.isEmpty()) {
            return new BigDecimal(0).setScale(options.getCalculationScale(), options.getCalculationRoundingMode());
        }

        BigDecimal sum = SumFunction.INSTANCE.call(options, functionParams);
	    BigDecimal size = new BigDecimal(functionParams.size()).setScale(options.getCalculationScale(), options.getCalculationRoundingMode());
	    return sum.divide(size, options.getCalculationScale(), options.getCalculationRoundingMode());
    }
}
