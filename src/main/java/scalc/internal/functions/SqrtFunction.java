package scalc.internal.functions;

import java.math.BigDecimal;
import java.util.List;

public class SqrtFunction implements FunctionImpl {
    public static final SqrtFunction INSTANCE = new SqrtFunction();

    public BigDecimal call(List<BigDecimal> functionParams) {
        if (functionParams.size() != 1) {
            throw new IllegalArgumentException(String.format("Function '%s' has to have exactly one argument.", getClass().getSimpleName()));
        } else {
            return PowFunction.INSTANCE.call(functionParams);
        }
    }
}
