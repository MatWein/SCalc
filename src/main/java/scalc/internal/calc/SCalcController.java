package scalc.internal.calc;

import scalc.SCalc;
import scalc.SCalcOptions;
import scalc.internal.converter.ToNumberConverter;

import java.math.BigDecimal;
import java.util.Map;

import static scalc.internal.calc.DefinitionExpressionController.parseDefinitionExpression;
import static scalc.internal.calc.SingleOperatorExpressionController.parseSingleOperatorExpression;
import static scalc.internal.calc.StandardExpressionController.parseStandardExpression;

public class SCalcController {
    public static <RETURN_TYPE> RETURN_TYPE calc(SCalc<RETURN_TYPE> sCalc) {
        SCalcOptions<RETURN_TYPE> options = sCalc.getOptions();

        BigDecimal resolvedValue = calculateResult(sCalc);
        resolvedValue = resolvedValue.setScale(options.getResultScale(), options.getResultRoundingMode());

        return ToNumberConverter.toResultType(resolvedValue, options.getReturnType(), options.getConverters());
    }

    private static <RETURN_TYPE> BigDecimal calculateResult(SCalc<RETURN_TYPE> sCalc) {
        SCalcOptions<RETURN_TYPE> options = sCalc.getOptions();
        Map<String, Number> params = sCalc.getParams();
        String expression = options.getExpression();

        if (expression.equals("+") || expression.equals("-") || expression.equals("*") || expression.equals("/") || expression.equals("^")) {
            return parseSingleOperatorExpression(expression, options.getCalculationMathContext(), params);
        } else if (expression.contains(";")) {
            return parseDefinitionExpression(sCalc);
        } else {
            return parseStandardExpression(options, params, expression);
        }
    }
}
