package scalc.internal.functions;

import scalc.internal.converter.NumberTypeConverter;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

public class SumFunction implements FunctionImpl {
    public static final SumFunction INSTANCE = new SumFunction();

    @Override
    public BigDecimal call(MathContext mathContext, List<BigDecimal> functionParams) {
        if (functionParams == null || functionParams.isEmpty()) {
            return new BigDecimal(0, mathContext);
        }

        BigDecimal result = NumberTypeConverter.convert(functionParams.get(0), BigDecimal.class);
        for (int i = 1; i < functionParams.size(); i++) {
            BigDecimal value = NumberTypeConverter.convert(functionParams.get(i), BigDecimal.class);
            result = result.add(value);
        }

        return result;
    }
}
