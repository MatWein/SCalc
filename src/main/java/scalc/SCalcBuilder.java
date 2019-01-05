package scalc;

import scalc.internal.ExpressionParser;
import scalc.internal.ParamExtractor;
import scalc.internal.converter.INumberConverter;
import scalc.internal.expr.Expression;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class SCalcBuilder<RETURN_TYPE> {
    private static Map<Class<?>, INumberConverter> staticConverters = new HashMap<>();
    private SCalcOptions<RETURN_TYPE> options = new SCalcOptions<>();

    private SCalcBuilder() {
    }

    public static SCalcBuilder<Double> doubleInstance() {
        return instanceFor(Double.class);
    }

    public static SCalcBuilder<BigDecimal> bigDecimalInstance() {
        return instanceFor(BigDecimal.class);
    }

    public static <RETURN_TYPE> SCalcBuilder<RETURN_TYPE> instanceFor(Class<RETURN_TYPE> returnType) {
        return new SCalcBuilder<>(returnType);
    }

    public static void registerStaticConverters(Map<Class<?>, INumberConverter> converters) {
        staticConverters.putAll(converters);
    }

    public static void registerStaticConverter(Class<?> type, INumberConverter converter) {
        staticConverters.put(type, converter);
    }

    private SCalcBuilder(Class<RETURN_TYPE> returnType) {
        this.options.setReturnType(returnType);
    }

    public SCalcBuilder<RETURN_TYPE> expression(String rawExpression) {
        this.options.setRawExpression(rawExpression);
        return this;
    }

    public SCalcBuilder<RETURN_TYPE> params(Map<String, Object> params) {
        this.options.getParams().putAll(params);
        return this;
    }

    public SCalcBuilder<RETURN_TYPE> params(Object... params) {
        this.options.setParamsAsArray(params);
        return this;
    }

    public SCalcBuilder<RETURN_TYPE> parameter(String name, Object value) {
        this.options.getParams().put(name, value);
        return this;
    }

    public SCalcBuilder<RETURN_TYPE> removeNullParameters(boolean removeNullParameters) {
        this.options.setRemoveNullParameters(removeNullParameters);
        return this;
    }

    public SCalcBuilder<RETURN_TYPE> resultScale(int scale) {
        return resultScale(scale, RoundingMode.HALF_UP);
    }

    public SCalcBuilder<RETURN_TYPE> resultScale(int scale, RoundingMode roundingMode) {
        this.options.setResultScale(scale);
        this.options.setResultRoundingMode(roundingMode);
        this.options.setResultMathContext(new MathContext(scale, roundingMode));
        return this;
    }

    public SCalcBuilder<RETURN_TYPE> calculationScale(int scale) {
        return calculationScale(scale, RoundingMode.HALF_UP);
    }

    public SCalcBuilder<RETURN_TYPE> calculationScale(int scale, RoundingMode roundingMode) {
        this.options.setCalculationScale(scale);
        this.options.setCalculationRoundingMode(roundingMode);
        this.options.setCalculationMathContext(new MathContext(scale, roundingMode));
        return this;
    }

    public SCalcBuilder<RETURN_TYPE> registerConverter(Class<?> type, Class<? extends INumberConverter> converterType) {
        try {
            this.options.getCustomConverters().put(type, converterType.getConstructor().newInstance());
        } catch (Throwable e) {
            throw new RuntimeException(String.format("Number converter has no default constructur: %s", converterType.getName()));
        }
        return this;
    }

    public SCalcBuilder<RETURN_TYPE> registerConverters(Map<Class<?>, INumberConverter> converters) {
        this.options.getCustomConverters().putAll(converters);
        return this;
    }

    public SCalcBuilder<RETURN_TYPE> registerConverter(Class<?> type, INumberConverter converter) {
        this.options.getCustomConverters().put(type, converter);
        return this;
    }

    public SCalc<RETURN_TYPE> build() {
        Expression expression = ExpressionParser.parse(options.getRawExpression());

        Map<Class<?>, INumberConverter> converters = new HashMap<>();
        converters.putAll(staticConverters);
        converters.putAll(options.getCustomConverters());

        Map<String, Number> params = ParamExtractor.calculateParams(
                expression,
                converters,
                options.getParams(),
                options.getParamsAsArray(),
                options.isRemoveNullParameters());

        return new SCalc<>(expression, params, converters, options);
    }
}
