package scalc.internal.functions;

import scalc.SCalcOptions;
import scalc.exceptions.CalculationException;

import java.math.BigDecimal;
import java.util.List;

public class TanFunction implements FunctionImpl {
    public static final TanFunction INSTANCE = new TanFunction();

    @Override
    public BigDecimal call(SCalcOptions<?> options, List<BigDecimal> functionParams) {
        if (functionParams.size() != 1) {
            throw new CalculationException("The tan function has to get exactly one parameter.");
        }

        BigDecimal firstParam = functionParams.get(0);
        return BigDecimal.valueOf(Math.tan(firstParam.doubleValue())).setScale(options.getCalculationScale(), options.getCalculationRoundingMode());
    }
}
