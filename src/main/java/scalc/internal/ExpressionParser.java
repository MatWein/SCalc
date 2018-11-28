package scalc.internal;

import scalc.internal.expr.Expression;

public class ExpressionParser {
    public static Expression parse(String expression) {
        if (expression == null) {
            throw new IllegalArgumentException("Expression cannot be null.");
        }

        expression = expression.trim();
        if ("".equals(expression)) {
            throw new IllegalArgumentException("Expression cannot be blank.");
        }



        return null;
    }
}
