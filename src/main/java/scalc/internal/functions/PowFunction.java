package scalc.internal.functions;

import java.math.BigDecimal;
import java.util.List;

public class PowFunction implements FunctionImpl {
    public static final PowFunction INSTANCE = new PowFunction();

    public BigDecimal call(List<BigDecimal> functionParams) {
        if (functionParams.size() == 0) {
            throw new IllegalArgumentException(String.format("Function '%s' has to have at least one argument.", getClass().getSimpleName()));
        } else if (functionParams.size() > 2) {
            throw new IllegalArgumentException(String.format("Function '%s' cannot have more than 2 arguments.", getClass().getSimpleName()));
        } else {
            double firstParam = functionParams.get(0).doubleValue();
            if (functionParams.size() == 1) {
                return new BigDecimal(Math.sqrt(firstParam));
            } else {
                double secondParam = functionParams.get(1).doubleValue();
                return new BigDecimal(Math.pow(firstParam, secondParam));
            }
        }
    }
}
