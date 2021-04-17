package scalc.internal.functions;

import scalc.SCalcOptions;
import scalc.exceptions.CalculationException;

import java.math.BigDecimal;
import java.util.List;

public class SinFunction implements FunctionImpl {
    public static final SinFunction INSTANCE = new SinFunction();

    @Override
    public BigDecimal call(SCalcOptions<?> options, List<BigDecimal> functionParams) {
        if (functionParams.size() != 1) {
            throw new CalculationException("The sin function has to get exactly one parameter.");
        }

        BigDecimal firstParam = functionParams.get(0);
        return BigDecimal.valueOf(Math.sin(firstParam.doubleValue())).setScale(options.getCalculationScale(), options.getCalculationRoundingMode());
    }
}
