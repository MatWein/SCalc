package scalc.internal.expr.multiline;

import scalc.internal.expr.Expression;

public abstract class LineExpression extends Expression {
    public LineExpression(String rawExpression) {
        super(rawExpression);
    }
}
