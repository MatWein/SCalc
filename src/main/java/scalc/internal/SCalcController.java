package scalc.internal;

import scalc.SCalc;
import scalc.SCalcOptions;
import scalc.internal.converter.NumberTypeConverter;
import scalc.internal.converter.ToNumberConverter;

import java.math.BigDecimal;
import java.util.Map.Entry;

public class SCalcController {
    public static <RETURN_TYPE> RETURN_TYPE calc(SCalc<RETURN_TYPE> sCalc) {
        SCalcOptions<RETURN_TYPE> options = sCalc.getOptions();
        BigDecimal resolvedValue = calculateResult(options);
        resolvedValue = resolvedValue.setScale(options.getResultScale(), options.getResultRoundingMode());

        return ToNumberConverter.toResultType(resolvedValue, options.getReturnType(), options.getConverters());
    }

    private static <RETURN_TYPE> BigDecimal calculateResult(SCalcOptions<RETURN_TYPE> options) {
        String expression = resolveExpression(options);

        SCalcExecutor<RETURN_TYPE> executor = new SCalcExecutor<>(options, expression);
        return executor.parse();
    }

    private static <RETURN_TYPE> String resolveExpression(SCalcOptions<RETURN_TYPE> options) {
        String expression = options.getExpression();
        for (Entry<String, Number> param : options.getParams().entrySet()) {
            String number = param.getValue() == null ? "0" : NumberTypeConverter.convert(param.getValue(), BigDecimal.class).toString();
            expression = expression.replaceAll("\\b" + param.getKey() + "\\b", number);
        }

        return expression;
    }
}
