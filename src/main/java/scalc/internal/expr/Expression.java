package scalc.internal.expr;

public abstract class Expression {
    private String rawExpression;

    public Expression(String rawExpression) {
        this.rawExpression = rawExpression;
    }

    public String getRawExpression() {
        return rawExpression;
    }
}
