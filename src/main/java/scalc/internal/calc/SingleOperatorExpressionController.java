package scalc.internal.calc;

import scalc.SCalcOptions;
import scalc.exceptions.CalculationException;
import scalc.internal.converter.NumberTypeConverter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SingleOperatorExpressionController {
    public static BigDecimal parseSingleOperatorExpression(String expression, SCalcOptions<?> options, Map<String, Number> params) {
        if (params == null || params.isEmpty()) {
            return new BigDecimal(0).setScale(options.getCalculationScale(), options.getCalculationRoundingMode());
        }

        List<Number> paramsInOrder = new ArrayList<>(params.values());

        BigDecimal result = NumberTypeConverter.convert(paramsInOrder.get(0), BigDecimal.class);
        for (int i = 1; i < paramsInOrder.size(); i++) {
            BigDecimal value = NumberTypeConverter.convert(paramsInOrder.get(i), BigDecimal.class);

            switch (expression) {
                case "+": result = result.add(value); break;
                case "-": result = result.subtract(value); break;
                case "*": result = result.multiply(value); break;
                case "/": result = result.divide(value, options.getCalculationScale(), options.getCalculationRoundingMode()); break;
                case "^": result = SCalcExecutor.calulatePow(result, value, options); break;
                default: throw new CalculationException("Expression invalid.");
            }
        }

        return result;
    }
}
