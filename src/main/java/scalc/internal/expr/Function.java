package scalc.internal.expr;

public class Function extends Expression {
    private String name;
    private Expression expression;

    public Function(String rawExpression, String name, Expression expression) {
        super(rawExpression);
        this.name = name;
        this.expression = expression;
    }

    public String getName() {
        return name;
    }

    public Expression getExpression() {
        return expression;
    }
}
