package scalc;

import scalc.internal.ExpressionParser;
import scalc.internal.ParamExtractor;
import scalc.internal.SimpleCalculator;
import scalc.internal.converter.INumberConverter;
import scalc.internal.converter.ToNumberConverter;
import scalc.internal.expr.Expression;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SCalc<RETURN_TYPE> {
    private static Map<Class<?>, INumberConverter> staticConverters = new HashMap<Class<?>, INumberConverter>();

    private final Class<RETURN_TYPE> returnType;

    private Expression expression;
    private Map<String, Number> params = new LinkedHashMap<String, Number>();
    private int resultScale = 10;
    private RoundingMode resultRoundingMode = RoundingMode.HALF_UP;
    private MathContext resultMathContext = new MathContext(resultScale, resultRoundingMode);
    private int calculationScale = 10;
    private RoundingMode calculationRoundingMode = RoundingMode.HALF_UP;
    private MathContext calculationMathContext = new MathContext(calculationScale, calculationRoundingMode);
    private Map<Class<?>, INumberConverter> converters = new HashMap<Class<?>, INumberConverter>(staticConverters);
    private boolean removeNullParameters = true;

    public static SCalc<Double> doubleInstance() {
        return instanceFor(Double.class);
    }

    public static SCalc<BigDecimal> bigDecimalInstance() {
        return instanceFor(BigDecimal.class);
    }

    public static <RETURN_TYPE> SCalc<RETURN_TYPE> instanceFor(Class<RETURN_TYPE> returnType) {
        return new SCalc<RETURN_TYPE>(returnType);
    }

    public static void registerStaticConverters(Map<Class<?>, INumberConverter> converters) {
        staticConverters.putAll(converters);
    }

    public static void registerStaticConverter(Class<?> type, INumberConverter converter) {
        staticConverters.put(type, converter);
    }

    private SCalc(Class<RETURN_TYPE> returnType) {
        this.returnType = returnType;
    }

    public SCalc<RETURN_TYPE> expression(String expression) {
        this.expression = ExpressionParser.parse(expression);
        return this;
    }

    public SCalc<RETURN_TYPE> params(Map<String, Object> params) {
        this.params = ParamExtractor.extractParamsFromMap(this, params);
        return this;
    }

    public SCalc<RETURN_TYPE> paramsFromNameValuePairs(Object... params) {
        this.params = ParamExtractor.extractParamsFromNameValuePairs(this, params);
        return this;
    }

    public SCalc<RETURN_TYPE> paramsInOrder(Object... params) {
        this.params = ParamExtractor.extractParamsInOrder(this, expression, params);
        return this;
    }

    public SCalc<RETURN_TYPE> parameter(String name, Object value) {
        this.params.put(name, ToNumberConverter.toNumber(value, this));
        return this;
    }

    public SCalc<RETURN_TYPE> removeNullParameters(boolean removeNullParameters) {
        this.removeNullParameters = removeNullParameters;
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

    public SCalc<RETURN_TYPE> registerConverter(Class<?> type, Class<? extends INumberConverter> converterType) {
        try {
            this.converters.put(type, converterType.getConstructor().newInstance());
        } catch (Throwable e) {
            throw new RuntimeException(String.format("Number converter has no default constructur: %s", converterType.getName()));
        }
        return this;
    }

    public SCalc<RETURN_TYPE> registerConverters(Map<Class<?>, INumberConverter> converters) {
        this.converters.putAll(converters);
        return this;
    }

    public SCalc<RETURN_TYPE> registerConverter(Class<?> type, INumberConverter converter) {
        this.converters.put(type, converter);
        return this;
    }

    public RETURN_TYPE calc() {
        if (expression == null) {
            throw new IllegalArgumentException("Expression cannot be empty.");
        }

        if (removeNullParameters) {
            while (params.values().remove(null)) {}
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

    public Map<Class<?>, INumberConverter> getConverters() {
        return converters;
    }

    public boolean isRemoveNullParameters() {
        return removeNullParameters;
    }
}
