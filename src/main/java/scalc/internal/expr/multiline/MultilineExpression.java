package scalc.internal.expr.multiline;

import scalc.internal.expr.Expression;
import scalc.internal.expr.ISubExpressions;

import java.util.List;

public class MultilineExpression extends Expression implements ISubExpressions {
    private List<LineExpression> expressions;

    public MultilineExpression(String rawExpression, List<LineExpression> expressions) {
        super(rawExpression);
        this.expressions = expressions;
    }

    public List<LineExpression> getExpressions() {
        return expressions;
    }
}
