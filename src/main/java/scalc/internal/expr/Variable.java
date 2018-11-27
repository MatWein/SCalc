package scalc.internal.expr;

public class Variable extends Expression implements INegatable {
    private boolean positive;
    private String name;

    public Variable(boolean positive, String name) {
        super(name);
        this.positive = positive;
        this.name = name;
    }

    public boolean isPositive() {
        return positive;
    }

    public String getName() {
        return name;
    }
}
