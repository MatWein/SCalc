package scalc.internal.functions;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

public class MinFunction implements FunctionImpl {
    public static final MinFunction INSTANCE = new MinFunction();

    @Override
    public BigDecimal call(MathContext mathContext, List<BigDecimal> functionParams) {
        if (functionParams.isEmpty()) {
            return new BigDecimal(0, mathContext);
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
