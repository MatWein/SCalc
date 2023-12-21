package scalc.internal.calc;

import scalc.SCalcOptions;
import scalc.exceptions.CalculationException;
import scalc.interfaces.SCalcExpressions;
import scalc.internal.SCalcLogger;
import scalc.internal.converter.NumberTypeConverter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SingleOperatorExpressionController {
    public static BigDecimal parseSingleOperatorExpression(
            String expression,
            SCalcOptions<?> options,
            Map<String, Number[]> params) {
        
        if (params == null || params.isEmpty()) {
            return new BigDecimal(0).setScale(options.getCalculationScale(), options.getCalculationRoundingMode());
        }

        List<BigDecimal> paramsInOrder = params.values().stream()
		        .flatMap(Arrays::stream)
		        .map(b -> NumberTypeConverter.convert(b, BigDecimal.class))
		        .collect(Collectors.toList());
	    
	    if (paramsInOrder.isEmpty()) {
		    return new BigDecimal(0).setScale(options.getCalculationScale(), options.getCalculationRoundingMode());
	    }
		
        BigDecimal result = paramsInOrder.get(0);
        for (int i = 1; i < paramsInOrder.size(); i++) {
            BigDecimal value = paramsInOrder.get(i);

            switch (expression) {
                case SCalcExpressions.SUM_EXPRESSION: result = result.add(value); break;
                case SCalcExpressions.SUBTRACT_EXPRESSION: result = result.subtract(value); break;
                case SCalcExpressions.MULTIPLY_EXPRESSION: result = result.multiply(value); break;
                case SCalcExpressions.DIVIDE_EXPRESSION: result = result.divide(value, options.getCalculationScale(), options.getCalculationRoundingMode()); break;
                case SCalcExpressions.POW_EXPRESSION: result = SCalcExecutor.calulatePow(result, value, options); break;
                default: throw new CalculationException("Expression invalid.");
            }
        }
    
        SCalcLogger.debug(options,
                "Calculated single operator expression. Expression: '%s'. Params: '%s'. Result: %s",
                expression, params, result);

        return result;
    }
}
