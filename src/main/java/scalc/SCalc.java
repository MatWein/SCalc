package scalc;

import scalc.internal.ExpressionParser;
import scalc.internal.ParamExtractor;
import scalc.internal.SimpleCalculator;
import scalc.internal.expr.Expression;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class SCalc<RETURN_TYPE extends Number> {
    private final Class<RETURN_TYPE> returnType;
    private Expression expression;
    private Map<String, Number> params = new HashMap<String, Number>();
    private int resultScale = 8;
    private RoundingMode resultRoundingMode = RoundingMode.HALF_UP;
    private MathContext resultMathContext = new MathContext(resultScale, resultRoundingMode);
    private int calculationScale = 8;
    private RoundingMode calculationRoundingMode = RoundingMode.HALF_UP;
    private MathContext calculationMathContext = new MathContext(calculationScale, calculationRoundingMode);

    public static SCalc<Double> doubleInstance() {
        return instanceFor(Double.class);
    }

    public static SCalc<BigDecimal> bigDecimalInstance() {
        return instanceFor(BigDecimal.class);
    }

    public static <RETURN_TYPE extends Number> SCalc<RETURN_TYPE> instanceFor(Class<RETURN_TYPE> returnType) {
        return new SCalc<RETURN_TYPE>(returnType);
    }

    private SCalc(Class<RETURN_TYPE> returnType) {
        this.returnType = returnType;
    }

    public SCalc<RETURN_TYPE> expression(String expression) {
        this.expression = ExpressionParser.parse(expression);
        return this;
    }

    public SCalc<RETURN_TYPE> params(Map<String, Number> params) {
        this.params = params;
        return this;
    }

    public SCalc<RETURN_TYPE> params(Object... params) {
        this.params = ParamExtractor.extractParamsFromNameValuePairs(expression, params);
        return this;
    }

    public SCalc<RETURN_TYPE> paramsInOrder(Number... params) {
        this.params = ParamExtractor.extractParamsInOrder(expression, params);
        return this;
    }

    public SCalc<RETURN_TYPE> resultScale(int scale) {
        return resultScale(scale, RoundingMode.HALF_UP);
    }

    public SCalc<RETURN_TYPE> resultScale(int scale, RoundingMode roundingMode) {
        this.resultScale = scale;
        this.resultRoundingMode = roundingMode;
        this.resultMathContext = new MathContext(scale, roundingMode);
        return this;
    }

    public SCalc<RETURN_TYPE> calculationScale(int scale) {
        return calculationScale(scale, RoundingMode.HALF_UP);
    }

    public SCalc<RETURN_TYPE> calculationScale(int scale, RoundingMode roundingMode) {
        this.calculationScale = scale;
        this.calculationRoundingMode = roundingMode;
        this.calculationMathContext = new MathContext(scale, roundingMode);
        return this;
    }

    public RETURN_TYPE calc() {
        if (expression == null) {
            throw new IllegalArgumentException("Expression cannot be empty.");
        }

        return SimpleCalculator.calc(this);
    }

    public Class<RETURN_TYPE> getReturnType() {
        return returnType;
    }

    public Expression getExpression() {
        return expression;
    }

    public Map<String, Number> getParams() {
        return params;
    }

    public int getResultScale() {
        return resultScale;
    }

    public RoundingMode getResultRoundingMode() {
        return resultRoundingMode;
    }

    public int getCalculationScale() {
        return calculationScale;
    }

    public RoundingMode getCalculationRoundingMode() {
        return calculationRoundingMode;
    }

    public MathContext getResultMathContext() {
        return resultMathContext;
    }

    public MathContext getCalculationMathContext() {
        return calculationMathContext;
    }
}
