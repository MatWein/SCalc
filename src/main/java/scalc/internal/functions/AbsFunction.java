package scalc.internal.functions;

import scalc.exceptions.CalculationException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

public class AbsFunction implements FunctionImpl {
    public static final AbsFunction INSTANCE = new AbsFunction();

    @Override
    public BigDecimal call(MathContext mathContext, List<BigDecimal> functionParams) {
        if (functionParams.size() != 1) {
            throw new CalculationException("The abs function has to get exactly one parameter.");
        }

        BigDecimal firstParam = functionParams.get(0);
        if (firstParam.compareTo(new BigDecimal(0, mathContext)) < 0) {
            return firstParam.multiply(new BigDecimal(-1, mathContext));
        } else {
            return firstParam;
        }
    }
}
