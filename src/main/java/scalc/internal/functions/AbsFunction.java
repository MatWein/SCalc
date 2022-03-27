package scalc.internal.functions;

import scalc.SCalcOptions;
import scalc.exceptions.CalculationException;
import scalc.interfaces.FunctionImpl;

import java.math.BigDecimal;
import java.util.List;

public class AbsFunction implements FunctionImpl {
    public static final AbsFunction INSTANCE = new AbsFunction();

    @Override
    public BigDecimal call(SCalcOptions<?> options, List<BigDecimal> functionParams) {
        if (functionParams.size() != 1) {
            throw new CalculationException("The abs function has to get exactly one parameter.");
        }

        BigDecimal firstParam = functionParams.get(0);
        if (firstParam.compareTo(new BigDecimal(0).setScale(options.getCalculationScale(), options.getCalculationRoundingMode())) < 0) {
            return firstParam.multiply(new BigDecimal(-1).setScale(options.getCalculationScale(), options.getCalculationRoundingMode()));
        } else {
            return firstParam;
        }
    }
}
