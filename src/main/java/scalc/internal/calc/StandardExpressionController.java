package scalc.internal.calc;

import scalc.SCalcOptions;

import java.math.BigDecimal;
import java.util.Map;

import static scalc.internal.calc.ExpressionResolver.resolveExpression;

public class StandardExpressionController {
    public static <RETURN_TYPE> BigDecimal parseStandardExpression(SCalcOptions<RETURN_TYPE> options, Map<String, Number> params, String expression) {
        String resolvedExpression = resolveExpression(options, expression, params);
        SCalcExecutor executor = new SCalcExecutor(resolvedExpression, options);
        return executor.parse();
    }
}
