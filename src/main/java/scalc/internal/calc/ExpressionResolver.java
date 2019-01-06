package scalc.internal.calc;

import scalc.internal.constants.Constants;
import scalc.internal.converter.NumberTypeConverter;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExpressionResolver {
    public static String resolveExpression(MathContext mathContext, String expression, Map<String, Number> params) {
        expression = replaceGivenParamsInExpression(expression, params);
        expression = replaceAllParamsConstant(expression, params);
        expression = replaceGlobalConstants(mathContext, expression);

        return expression;
    }

    private static String replaceGlobalConstants(MathContext mathContext, String expression) {
        Map<String, Number> constants = Constants.getPredefinedConstants(mathContext);

        for (Map.Entry<String, Number> entry : constants.entrySet()) {
            expression = replaceWord(expression, entry.getKey(), entry.getValue().toString());
        }

        expression = expression.replace("²", "^2");
        expression = expression.replace("³", "^3");

        return expression;
    }

    private static String replaceAllParamsConstant(String expression, Map<String, Number> params) {
        List<String> allParamsAsString = new ArrayList<>();
        for (Number value : params.values()) {
            allParamsAsString.add(paramToString(value));
        }

        String paramString = allParamsAsString.toString();
        paramString = paramString.substring(1, paramString.length() - 1);

        expression = replaceWord(expression, "ALL_PARAMS", paramString);
        return expression;
    }

    private static String replaceGivenParamsInExpression(String expression, Map<String, Number> params) {
        for (Map.Entry<String, Number> param : params.entrySet()) {
            String number = param.getValue() == null ? "0" : paramToString(param.getValue());
            expression = replaceWord(expression, param.getKey(), number);
        }
        return expression;
    }

    private static String paramToString(Number param) {
        return NumberTypeConverter.convert(param, BigDecimal.class).toPlainString();
    }

    private static String replaceWord(String value, String wordToReplace, String newValue) {
        return value.replaceAll("(?i)\\b" + wordToReplace + "\\b", newValue);
    }
}
