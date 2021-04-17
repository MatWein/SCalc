package scalc.internal.calc;

import scalc.SCalc;
import scalc.SCalcExpressions;
import scalc.SCalcOptions;
import scalc.internal.converter.ToNumberConverter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static scalc.internal.calc.DefinitionExpressionController.parseDefinitionExpression;
import static scalc.internal.calc.SingleOperatorExpressionController.parseSingleOperatorExpression;
import static scalc.internal.calc.StandardExpressionController.parseStandardExpression;

public class SCalcController {
    private static final List<String> SINGLE_OPERATOR_EXPRESSIONS = Arrays.asList(
            SCalcExpressions.SUM_EXPRESSION,
            SCalcExpressions.SUBTRACT_EXPRESSION,
            SCalcExpressions.MULTIPLY_EXPRESSION,
            SCalcExpressions.DIVIDE_EXPRESSION,
            SCalcExpressions.POW_EXPRESSION
    );
    
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

        if (SINGLE_OPERATOR_EXPRESSIONS.contains(expression)) {
            return parseSingleOperatorExpression(expression, options, params);
        } else if (expression.contains(";")) {
            return parseDefinitionExpression(sCalc);
        } else {
            return parseStandardExpression(options, params, expression);
        }
    }
}
