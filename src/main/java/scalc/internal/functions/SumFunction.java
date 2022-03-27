package scalc.internal.functions;

import scalc.SCalcOptions;
import scalc.interfaces.FunctionImpl;
import scalc.internal.converter.NumberTypeConverter;

import java.math.BigDecimal;
import java.util.List;

public class SumFunction implements FunctionImpl {
    public static final SumFunction INSTANCE = new SumFunction();

    @Override
    public BigDecimal call(SCalcOptions<?> options, List<BigDecimal> functionParams) {
        if (functionParams == null || functionParams.isEmpty()) {
            return new BigDecimal(0).setScale(options.getCalculationScale(), options.getCalculationRoundingMode());
        }

        BigDecimal result = NumberTypeConverter.convert(functionParams.get(0), BigDecimal.class);
        for (int i = 1; i < functionParams.size(); i++) {
            BigDecimal value = NumberTypeConverter.convert(functionParams.get(i), BigDecimal.class);
            result = result.add(value);
        }

        return result;
    }
}
