package scalc.internal;

import scalc.internal.expr.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionParser {
    private static final String NAME_PATTERN = "[\\w\\d√]+";
    private static final String SIGN_PATTERN = "[\\-]?";

    private static final String CONSTANT_PATTERN = "(" + SIGN_PATTERN + ")(" + "([0-9]+)|([0-9]+[.]?[0-9]+)" + ")";
    private static final String VARIABLE_PATTERN = "(" + SIGN_PATTERN + ")(" + NAME_PATTERN + ")";

    private static final String OPERATOR_PATTERN = "((\\+)|(-)|(\\*)|(/)|(\\^))";
    private static final Pattern COMPILED_OPERATOR_PATTERN = Pattern.compile(OPERATOR_PATTERN);

    private static final String FUNCTION_PATTERN = "(" + SIGN_PATTERN + ")(" + NAME_PATTERN + ")" + "\\((.*)\\)";
    private static final Pattern COMPILED_FUNCTION_PATTERN = Pattern.compile(FUNCTION_PATTERN);

    public static Expression parse(String expression) {
        if (expression == null) {
            throw new IllegalArgumentException("Expression cannot be null.");
        }

        expression = expression.trim();
        if ("".equals(expression)) {
            throw new IllegalArgumentException("Expression cannot be blank.");
        }

        if (expression.matches(OPERATOR_PATTERN)) {
            if (Operator.MULTIPLICATION.getOperator().equals(expression)) {
                return Operator.MULTIPLICATION;
            } else if (Operator.DIVISION.getOperator().equals(expression)) {
                return Operator.DIVISION;
            } else if (Operator.ADDITION.getOperator().equals(expression)) {
                return Operator.ADDITION;
            } else if (Operator.SUBTRACTION.getOperator().equals(expression)) {
                return Operator.SUBTRACTION;
            } else if (Operator.POW.getOperator().equals(expression)) {
                return Operator.POW;
            } else {
                throw new IllegalArgumentException(String.format("Cannot find operator for expression '%s'.", expression));
            }
        }

        if (expression.matches(CONSTANT_PATTERN)) {
            boolean negative = expression.startsWith("-");
            if (negative) {
                return new Constant(false, new BigDecimal(expression.substring(1)));
            } else {
                return new Constant(true, new BigDecimal(expression));
            }
        }

        if (expression.matches(VARIABLE_PATTERN)) {
            boolean negative = expression.startsWith("-");
            if (negative) {
                return new Variable(false, expression.substring(1));
            } else {
                return new Variable(true, expression);
            }
        }

        Matcher functionMatcher = COMPILED_FUNCTION_PATTERN.matcher(expression);
        if (functionMatcher.find() && expression.matches(FUNCTION_PATTERN)) {
            boolean negative = expression.startsWith("-");
            String name = functionMatcher.group(2);
            String innerExpression = functionMatcher.group(3);

            List<Expression> subExpressions = new ArrayList<Expression>();
            if (!"".equals(innerExpression)) {
                String[] innerExpressions = innerExpression.split(",");

                for (String ie : innerExpressions) {
                    subExpressions.add(parse(ie));
                }
            }

            return new Function(!negative, expression, name, subExpressions);
        }

        return parseComplexExpressionWithoutBracket(expression);
    }

    private static ComplexExpression parseComplexExpressionWithoutBracket(String expression) {
        List<Expression> expressions = new ArrayList<Expression>();

        String[] subExpressions = expression.split(OPERATOR_PATTERN);
        for (String subExpression : subExpressions) {
            expressions.add(parse(subExpression));
        }

        int operatorStartIndex = 1;
        Matcher matcher = COMPILED_OPERATOR_PATTERN.matcher(expression);
        while (matcher.find()) {
            Operator operator = (Operator)parse(matcher.group(1));
            expressions.add(operatorStartIndex, operator);

            operatorStartIndex += 2;
        }

        return new ComplexExpression(expression, expressions);
    }
}
