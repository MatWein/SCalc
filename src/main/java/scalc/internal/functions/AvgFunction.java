package scalc.internal.functions;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

public class AvgFunction implements FunctionImpl {
    public static final AvgFunction INSTANCE = new AvgFunction();

    @Override
    public BigDecimal call(MathContext mathContext, List<BigDecimal> functionParams) {
        if (functionParams.isEmpty()) {
            return new BigDecimal(0, mathContext);
        }

        BigDecimal sum = SumFunction.INSTANCE.call(mathContext, functionParams);
        return sum.divide(new BigDecimal(functionParams.size(), mathContext), mathContext);
    }
}
