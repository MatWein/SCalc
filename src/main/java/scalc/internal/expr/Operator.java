package scalc.internal.expr;

import scalc.SCalc;

import java.math.BigDecimal;

public class Operator extends Expression {
    public static final Operator MULTIPLICATION = new Operator("*", new IOperatorFunction() {
        public BigDecimal calc(SCalc<?> sCalc, BigDecimal left, BigDecimal right) {
            return left.multiply(right, sCalc.getCalculationMathContext());
        }
    });

    public static final Operator DIVISION = new Operator("/", new IOperatorFunction() {
        public BigDecimal calc(SCalc<?> sCalc, BigDecimal left, BigDecimal right) {
            return left.divide(right, sCalc.getCalculationScale(), sCalc.getCalculationRoundingMode());
        }
    });

    public static final Operator SUBTRACTION = new Operator("-", new IOperatorFunction() {
        public BigDecimal calc(SCalc<?> sCalc, BigDecimal left, BigDecimal right) {
            return left.subtract(right, sCalc.getCalculationMathContext());
        }
    });
    public static final Operator ADDITION = new Operator("+", new IOperatorFunction() {
        public BigDecimal calc(SCalc<?> sCalc, BigDecimal left, BigDecimal right) {
            return left.add(right, sCalc.getCalculationMathContext());
        }
    });
	public static final Operator POW = new Operator("^", new IOperatorFunction() {
		public BigDecimal calc(SCalc<?> sCalc, BigDecimal left, BigDecimal right) {
			return left.pow(right.intValue(), sCalc.getCalculationMathContext());
		}
	});

    private final String operator;
    private final IOperatorFunction function;

    public Operator(String operator, IOperatorFunction function) {
        super(operator);
        this.operator = operator;
        this.function = function;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Operator operator1 = (Operator) o;

        return getOperator().equals(operator1.getOperator());
    }

    @Override
    public int hashCode() {
        return getOperator().hashCode();
    }

    public IOperatorFunction getFunction() {
        return function;
    }

    public interface IOperatorFunction {
        BigDecimal calc(SCalc<?> sCalc, BigDecimal left, BigDecimal right);
    }
}
