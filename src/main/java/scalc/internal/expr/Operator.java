package scalc.internal.expr;

public class Operator extends Expression {
    private String operator;

    public Operator(String operator) {
        super(operator);
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }
}
