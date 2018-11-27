package scalc.internal.expr;

public class Constant extends Expression implements INegatable {
    private boolean positive;
    private Number value;

    public Constant(boolean positive, Number value) {
        super(value.toString());
        this.positive = positive;
        this.value = value;
    }

    public boolean isPositive() {
        return positive;
    }

    public Number getValue() {
        return value;
    }
}
