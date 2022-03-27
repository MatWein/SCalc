package scalc;

import scalc.exceptions.CalculationException;
import scalc.internal.ParamExtractor;
import scalc.internal.calc.SCalcController;
import scalc.internal.converter.ToNumberConverter;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * The calculator class. Do not create the instance by yourself. Please use SCalcBuilder!
 */
public final class SCalc<RETURN_TYPE> {
    private final SCalcOptions<RETURN_TYPE> options;
    private final Map<String, Number> params = new LinkedHashMap<>();

    SCalc(SCalcOptions<RETURN_TYPE> options) {
        this.options = options;
    }

    /**
     * Starts the calculation and returns the result in the previously given return type.
     * @return Calculation result as double, BigDecimal or whatever you have specified in the builder.
     * @throws CalculationException If any problems occur, the exception will be wrapped ad CalculationException.
     */
    public final RETURN_TYPE calc() throws CalculationException {
        try {
            RETURN_TYPE result = SCalcController.calc(this);
            this.reset();
            return result;
        } catch (Throwable e) {
        	String message = String.format("Unexpected error on calculation of expression: %s using params: %s", options.getExpression(), params);
            throw new CalculationException(message, e);
        }
    }

    /**
     * [OPTIONAL] Map of named parameters to use for calculation. Optional if the expression does not have any params.
     * @param params Params for calculation
     */
    public final SCalc<RETURN_TYPE> params(Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            this.params.put(entry.getKey(), ToNumberConverter.toNumber(entry.getValue(), options.getConverters()));
        }
        return this;
    }

    /**
     * [OPTIONAL] Parameters in form of: <br/>
     * - "name1", 10, "name2", 5, ...<br/>
     * or<br/>
     * - 10, 20, 30, ...<br/>
     * Optional if the expression does not have any params.
     * @param paramsAsArray Params for calculation
     */
    public final SCalc<RETURN_TYPE> params(Object... paramsAsArray) {
        this.params.putAll(ParamExtractor.extractParams(Function.identity(), options.getConverters(), paramsAsArray, this.params.size()));
        return this;
    }
    
    /**
     * [OPTIONAL] Parameters in form of: <br/>
     * - "name1", 10, "name2", 5, ...<br/>
     * or<br/>
     * - 10, 20, 30, ...<br/>
     * Optional if the expression does not have any params.
     * @param paramExtractor Function to extract nested properties of the fiven params
     * @param paramsAsArray Params for calculation
     */
    @SafeVarargs
    public final <T> SCalc<RETURN_TYPE> params(Function<T, Object> paramExtractor, T... paramsAsArray) {
        this.params.putAll(ParamExtractor.extractParams(paramExtractor, options.getConverters(), paramsAsArray, this.params.size()));
        return this;
    }

    /**
     * [OPTIONAL] Parameters in form of: <br/>
     * - "name1", 10, "name2", 5, ...<br/>
     * or<br/>
     * - 10, 20, 30, ...<br/>
     * Optional if the expression does not have any params.
     * @param params Params for calculation
     */
    public final SCalc<RETURN_TYPE> paramsAsCollection(Collection<?> params) {
    	if (params == null || params.isEmpty()) {
    		return this;
	    }
    	
        return params(params.toArray(new Object[] {}));
    }
    
    /**
     * [OPTIONAL] Parameters in form of: <br/>
     * - "name1", 10, "name2", 5, ...<br/>
     * or<br/>
     * - 10, 20, 30, ...<br/>
     * Optional if the expression does not have any params.
     * @param paramExtractor Function to extract nested properties of the fiven params
     * @param params Params for calculation
     */
    public final <T> SCalc<RETURN_TYPE> paramsAsCollection(Function<T, Object> paramExtractor, Collection<T> params) {
        return params(paramExtractor, (T[])params.toArray(new Object[] {}));
    }

    /**
     * [OPTIONAL] Single parameter for calculation. Can be used multiple times to add more than one param.
     * Optional if the expression does not have any params.
     * @param name Name of the param
     * @param value Value of the param as java.lang.Number or custom type.
     */
    public final SCalc<RETURN_TYPE> parameter(String name, Object value) {
        this.params.put(name, ToNumberConverter.toNumber(value, options.getConverters()));
        return this;
    }

    /**
     * Resets the calculator state and removes all its parameters.<br/>
     * This method is called automatically after each successful calculation.
     */
    public final SCalc<RETURN_TYPE> reset() {
        this.params.clear();
        return this;
    }

    public SCalcOptions<RETURN_TYPE> getOptions() {
        return options;
    }

    public Map<String, Number> getParams() {
        return params;
    }
}
