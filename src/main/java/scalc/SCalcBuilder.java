package scalc;

import scalc.exceptions.CalculationException;
import scalc.internal.converter.INumberConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Builder class for the SCalc calculator.
 */
public class SCalcBuilder<RETURN_TYPE> {
    private static final Map<Class<?>, INumberConverter> staticConverters = new HashMap<>();

    private final SCalcOptions<RETURN_TYPE> options = new SCalcOptions<>();
    private final Map<Class<?>, INumberConverter> customConverters = new HashMap<>();

    /**
     * Use this method to get a new builder. All calculation results will be returned as double.
     */
    public static SCalcBuilder<Double> doubleInstance() {
        return instanceFor(Double.class);
    }

    /**
     * Use this method to get a new builder. All calculation results will be returned as BigDecimal.
     */
    public static SCalcBuilder<BigDecimal> bigDecimalInstance() {
        return instanceFor(BigDecimal.class);
    }

    /**
     * Use this method to get a new builder. All calculation results will be returned in the given type, if the type is known or you have specified a custom converter for this type.
     * @param returnType Class<? extends Number> or custom type.
     */
    public static <RETURN_TYPE> SCalcBuilder<RETURN_TYPE> instanceFor(Class<RETURN_TYPE> returnType) {
        return new SCalcBuilder<>(returnType);
    }

    /**
     * Register multiple global type converters for calculation results and parameters.<br/>
     * Attention: This will affect ALL instances of SCalc!
     * @param converters Map of all converter instances to register. Already existing ones will be overridden.
     */
    public static void registerGlobalConverters(Map<Class<?>, INumberConverter> converters) {
        staticConverters.putAll(converters);
    }

    /**
     * Register a global type converter for calculation results and parameters.<br/>
     * Attention: This will affect ALL instances of SCalc!
     * @param type Number type of the given converter
     * @param converter Converter instance to register. Already existing ones will be overridden.
     */
    public static void registerGlobalConverter(Class<?> type, INumberConverter converter) {
        staticConverters.put(type, converter);
    }

    /**
     * Register a global type converter for calculation results and parameters.<br/>
     * Your converter class has to have a default constructor to use this method!<br/>
     * Attention: This will affect ALL instances of SCalc!
     * @param type Number type of the given converter
     * @param converter Converter class to register. Already existing ones will be overridden.
     */
    public static void registerGlobalConverter(Class<?> type, Class<? extends INumberConverter> converter) {
        try {
            staticConverters.put(type, converter.getConstructor().newInstance());
        } catch (Throwable e) {
            throw new CalculationException(String.format("Number converter has no default constructur: %s", converter.getName()));
        }
    }

    private SCalcBuilder(Class<RETURN_TYPE> returnType) {
        this.options.setReturnType(returnType);
    }

    /**
     * [REQUIRED] Expression to parse. For further details see documentation.
     * @param rawExpression Single operator expression, standard expression or definition expression
     */
    public SCalcBuilder<RETURN_TYPE> expression(String rawExpression) {
        this.options.setExpression(rawExpression);
        return this;
    }
    
    /**
     * [REQUIRED] Expression to parse. For further details see documentation.
     */
    public SCalcBuilder<RETURN_TYPE> sumExpression() {
        return expression(SCalcExpressions.SUM_EXPRESSION);
    }
    
    /**
     * [REQUIRED] Expression to parse. For further details see documentation.
     */
    public SCalcBuilder<RETURN_TYPE> subtractExpression() {
        return expression(SCalcExpressions.SUBTRACT_EXPRESSION);
    }
    
    /**
     * [REQUIRED] Expression to parse. For further details see documentation.
     */
    public SCalcBuilder<RETURN_TYPE> multiplyExpression() {
        return expression(SCalcExpressions.MULTIPLY_EXPRESSION);
    }
    
    /**
     * [REQUIRED] Expression to parse. For further details see documentation.
     */
    public SCalcBuilder<RETURN_TYPE> divideExpression() {
        return expression(SCalcExpressions.DIVIDE_EXPRESSION);
    }
    
    /**
     * [REQUIRED] Expression to parse. For further details see documentation.
     */
    public SCalcBuilder<RETURN_TYPE> powExpression() {
        return expression(SCalcExpressions.POW_EXPRESSION);
    }

    /**
     * [OPTIONAL] Specifies the scale of the result. This scale will only be used for rounding the result number. Internal calculation can use another scale!<br/>
     * Default: 10
     * @param scale Scale to use for the result and only for the result
     */
    public SCalcBuilder<RETURN_TYPE> resultScale(int scale) {
        return resultScale(scale, RoundingMode.HALF_UP);
    }

    /**
     * [OPTIONAL] Specifies the scale of the result. This scale will only be used for rounding the result number. Internal calculation can use another scale!<br/>
     * Default: 10, HALP_UP
     * @param scale Scale to use for the result and only for the result
     * @param roundingMode Rounding mode to use for the result
     */
    public SCalcBuilder<RETURN_TYPE> resultScale(int scale, RoundingMode roundingMode) {
        this.options.setResultScale(scale);
        this.options.setResultRoundingMode(roundingMode);
        return this;
    }
    
    /**
     * [OPTIONAL] Specifies debug mode.<br/>
     * Default: false
     * @param debug If true, calculation steps are logged to debug logger or if null to system out.
     */
    public SCalcBuilder<RETURN_TYPE> debug(boolean debug) {
        this.options.setDebug(debug);
        return this;
    }
    
    /**
     * [OPTIONAL] Specifies debug logger.<br/>
     * Default: null (system out)
     * @param debugLogger If debug mode is set to true, this consumer is used to print log statements.
     *                    Usually this will be only a delegate to the logger of slf4j or other custom loggers.
     */
    public SCalcBuilder<RETURN_TYPE> debugLogger(Consumer<String> debugLogger) {
        this.options.setDebugLogger(debugLogger);
        return this;
    }

    /**
     * [OPTIONAL] Specifies the scale used for the calculation. This scale will not be used for rounding the result number!<br/>
     * Default: 10
     * @param scale Scale to use for the calculation and only for the calculation
     */
    public SCalcBuilder<RETURN_TYPE> calculationScale(int scale) {
        return calculationScale(scale, RoundingMode.HALF_UP);
    }

    /**
     * [OPTIONAL] Specifies the scale used for the calculation. This scale will not be used for rounding the result number!<br/>
     * Default: 10
     * @param scale Scale to use for the calculation and only for the calculation
     * @param roundingMode Rounding mode to use for the calculation
     */
    public SCalcBuilder<RETURN_TYPE> calculationScale(int scale, RoundingMode roundingMode) {
        this.options.setCalculationScale(scale);
        this.options.setCalculationRoundingMode(roundingMode);
        return this;
    }

    /**
     * Register a local type converter for calculation results and parameters.<br/>
     * Your converter class has to have a default constructor to use this method!<br/>
     * This will only affect the current instance of SCalc.
     * @param type Number type of the given converter
     * @param converterType Converter class to register. Already existing ones will be overridden.
     */
    public SCalcBuilder<RETURN_TYPE> registerConverter(Class<?> type, Class<? extends INumberConverter> converterType) {
        try {
            this.customConverters.put(type, converterType.getConstructor().newInstance());
        } catch (Throwable e) {
            throw new CalculationException(String.format("Number converter has no default constructur: %s", converterType.getName()));
        }
        return this;
    }

    /**
     * Register multiple local type converters for calculation results and parameters.<br/>
     * This will only affect the current instance of SCalc.
     * @param converters Map with converter instances
     */
    public SCalcBuilder<RETURN_TYPE> registerConverters(Map<Class<?>, INumberConverter> converters) {
        this.customConverters.putAll(converters);
        return this;
    }

    /**
     * Register a local type converter for calculation results and parameters.<br/>
     * This will only affect the current instance of SCalc.
     * @param type Number type of the given converter
     * @param converter Converter instance to register. Already existing ones will be overridden.
     */
    public SCalcBuilder<RETURN_TYPE> registerConverter(Class<?> type, INumberConverter converter) {
        this.customConverters.put(type, converter);
        return this;
    }

    /**
     * After setting all options for the builder, you can call this method to create a new instance of SCalc.
     * @return A new calculator with the given options.
     */
    public SCalc<RETURN_TYPE> build() {
        Map<Class<?>, INumberConverter> converters = new HashMap<>();
        converters.putAll(staticConverters);
        converters.putAll(customConverters);
        options.setConverters(converters);

        return new SCalc<>(options);
    }

    /**
     * Convenience method to run build and calc in one step if only one calculation is required.<br/>
     * By using this method it is no more possible to specify custom parameters!
     * @return The result of the calculation
     */
    public RETURN_TYPE buildAndCalc() {
        return build().calc();
    }
}
