package scalc.internal.expr;

import java.util.Collections;
import java.util.List;

public class Function extends Expression implements INegatable, ISubExpressions {
    private boolean positive;
    private String name;
    private List<Expression> expressions;

    public Function(boolean positive, String rawExpression, String name, Expression expression) {
        this(positive, rawExpression, name, Collections.singletonList(expression));
    }

    public Function(boolean positive, String rawExpression, String name, List<Expression> expressions) {
        super(rawExpression);
        this.positive = positive;
        this.name = name;
        this.expressions = expressions;
    }

    public boolean isPositive() {
        return positive;
    }

    public String getName() {
        return name;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }
}
