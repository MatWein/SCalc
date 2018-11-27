package scalc.internal.expr;

import java.util.List;

public class ComplexExpression extends Expression {
    private List<Expression> expressions;

    public ComplexExpression(String rawExpression, List<Expression> expressions) {
        super(rawExpression);
        this.expressions = expressions;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }
}
