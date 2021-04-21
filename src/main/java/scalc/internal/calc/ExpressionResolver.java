package scalc.internal.calc;

import scalc.SCalcExpressions;
import scalc.SCalcOptions;
import scalc.internal.constants.Constants;
import scalc.internal.converter.NumberTypeConverter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class ExpressionResolver {
    public static String resolveExpression(SCalcOptions<?> options, String expression, Map<String, Number> params) {
        expression = replaceGivenParamsInExpression(expression, params);
        expression = replaceAllParamsConstant(expression, params);
        expression = replaceGlobalConstants(options, expression);

        return expression;
    }

    private static String replaceGlobalConstants(SCalcOptions<?> options, String expression) {
        Map<String, Number> constants = Constants.getPredefinedConstants(options);

        for (Entry<String, Number> entry : constants.entrySet()) {
            expression = replaceWord(expression, entry.getKey(), entry.getValue().toString());
        }

        expression = expression.replace("²", "^2");
        expression = expression.replace("³", "^3");

        return expression;
    }

    private static String replaceAllParamsConstant(String expression, Map<String, Number> params) {
        String paramString = params.values().stream()
                .map(ExpressionResolver::paramToString)
                .collect(Collectors.joining(","));

        return replaceWord(expression, SCalcExpressions.ALL_PARAMS, paramString);
    }

    private static String replaceGivenParamsInExpression(String expression, Map<String, Number> params) {
        for (Entry<String, Number> param : params.entrySet()) {
            String number = param.getValue() == null ? "0" : paramToString(param.getValue());
            expression = replaceWord(expression, param.getKey(), number);
        }
        return expression;
    }

    private static String paramToString(Number param) {
        return NumberTypeConverter.convert(param, BigDecimal.class).toPlainString();
    }

    private static String replaceWord(String value, String wordToReplace, String newValue) {
        return value.replaceAll("(?i)\\b" + wordToReplace + "\\b(?!\\()", newValue);
    }
}
