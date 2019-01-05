package scalc.internal.expr.multiline;

import scalc.internal.expr.ComplexExpression;

public class ReturnExpression extends LineExpression {
    private ComplexExpression expression;

    public ReturnExpression(String rawExpression, ComplexExpression expression) {
        super(rawExpression);
        this.expression = expression;
    }

    public ComplexExpression getExpression() {
        return expression;
    }
}
