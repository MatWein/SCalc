package scalc.internal.functions;

import scalc.SCalcOptions;
import scalc.exceptions.CalculationException;
import scalc.interfaces.FunctionImpl;

import java.math.BigDecimal;
import java.util.List;

public class CosFunction implements FunctionImpl {
    public static final CosFunction INSTANCE = new CosFunction();

    @Override
    public BigDecimal call(SCalcOptions<?> options, List<BigDecimal> functionParams) {
        if (functionParams.size() != 1) {
            throw new CalculationException("The cos function has to get exactly one parameter.");
        }

        BigDecimal firstParam = functionParams.get(0);
        return BigDecimal.valueOf(Math.cos(firstParam.doubleValue())).setScale(options.getCalculationScale(), options.getCalculationRoundingMode());
    }
}
