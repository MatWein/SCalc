package scalc;

import scalc.internal.ExpressionParser;
import scalc.internal.ParamExtractor;
import scalc.internal.SimpleCalculator;
import scalc.internal.expr.Expression;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class SCalc<RETURN_TYPE extends Number> {
    private final Class<RETURN_TYPE> returnType;
    private Expression expression;
    private Map<String, Number> params;
    private Integer scale;
    private RoundingMode roundingMode;

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

    public SCalc<RETURN_TYPE> scale(int scale) {
        return scale(scale, RoundingMode.HALF_UP);
    }

    public SCalc<RETURN_TYPE> scale(int scale, RoundingMode roundingMode) {
        this.scale = scale;
        this.roundingMode = roundingMode;
        return this;
    }

    public RETURN_TYPE calc() {
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

    public Integer getScale() {
        return scale;
    }

    public RoundingMode getRoundingMode() {
        return roundingMode;
    }
}
