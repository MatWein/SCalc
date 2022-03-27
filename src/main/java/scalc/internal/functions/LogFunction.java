package scalc.internal.functions;

import scalc.SCalcOptions;
import scalc.exceptions.CalculationException;
import scalc.interfaces.FunctionImpl;

import java.math.BigDecimal;
import java.util.List;

public class LogFunction implements FunctionImpl {
    public static final LogFunction INSTANCE = new LogFunction();

    @Override
    public BigDecimal call(SCalcOptions<?> options, List<BigDecimal> functionParams) {
        if (functionParams.size() != 1) {
            throw new CalculationException("The log function has to get exactly one parameter.");
        }

        BigDecimal firstParam = functionParams.get(0);
        return BigDecimal.valueOf(Math.log10(firstParam.doubleValue())).setScale(options.getCalculationScale(), options.getCalculationRoundingMode());
    }
}
