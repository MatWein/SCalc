package scalc.internal;

import scalc.SCalc;
import scalc.internal.expr.Expression;

import java.math.BigDecimal;
import java.util.Map;

public class SimpleCalculator {
    public static <RETURN_TYPE extends Number> RETURN_TYPE calc(SCalc<RETURN_TYPE> calculatorParams) {
        Expression expression = calculatorParams.getExpression();
        Map<String, Number> params = calculatorParams.getParams();

        BigDecimal result = calc(expression, params);
        return NumberTypeConverter.convert(result, calculatorParams.getReturnType());
    }

    private static BigDecimal calc(Expression expression, Map<String, Number> params) {
        return null;
    }
}
