package scalc;

import scalc.exceptions.CalculationException;
import scalc.internal.calc.SCalcController;
import scalc.internal.converter.ToNumberConverter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * The calculator class. Do not create the instance by yourself. Please use SCalcBuilder!
 */
public final class SCalc<RETURN_TYPE> {
	private static final String DEFAULT_PARAM_NAME = "param";
	
    private final SCalcOptions<RETURN_TYPE> options;
    private final Map<String, Number[]> params = new LinkedHashMap<>();
	private final AtomicInteger paramCounter = new AtomicInteger(0);

    SCalc(SCalcOptions<RETURN_TYPE> options) {
        this.options = options;
    }

    /**
     * Starts the calculation and returns the result in the previously given return type.
     * @return Calculation result as double, BigDecimal or whatever you have specified in the builder.
     * @throws CalculationException If any problems occur, the exception will be wrapped ad CalculationException.
     */
    public RETURN_TYPE calc() throws CalculationException {
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
    public SCalc<RETURN_TYPE> parameter(Map<String, Object> params) {
		if (params == null || params.isEmpty()) {
			return this;
		}
		
        for (Map.Entry<String, Object> entry : params.entrySet()) {
			this.parameter(entry.getKey(), entry.getValue());
        }
		
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
    public SCalc<RETURN_TYPE> parameter(Object... params) {
	    return parameter(Function.identity(), params);
    }
	
	/**
	 * [OPTIONAL] Parameters in form of: <br/>
	 * - "name1", 10, "name2", 5, ...<br/>
	 * or<br/>
	 * - 10, 20, 30, ...<br/>
	 * Optional if the expression does not have any params.
	 * @param paramExtractor Function to extract nested properties of the given params
	 * @param params Params for calculation
	 */
    public <T> SCalc<RETURN_TYPE> parameter(Function<T, Object> paramExtractor, Object... params) {
	    if (params != null && params.length > 0 && params[0] instanceof CharSequence && params.length % 2 == 0) {
		    for (int i = 0; i < params.length; i += 2) {
			    if (!(params[i] instanceof String)) {
				    throw new CalculationException(String.format("Invalid param value: '%s'. Has to be a string.", params[i]));
			    }
			    
			    this.parameter((String)params[i], paramExtractor, params[i + 1]);
		    }
	    } else {
		    this.parameter(DEFAULT_PARAM_NAME + paramCounter.getAndIncrement(), paramExtractor, params);
	    }
		
		return this;
    }
	
    private SCalc<RETURN_TYPE> parameter(String name, Object... values) {
		return parameter(name, Function.identity(), values);
    }
	
	private <T> SCalc<RETURN_TYPE> parameter(String name, Function<T, Object> paramExtractor, Object... values) {
		if (values == null || values.length == 0) {
			return this;
		}
		
		this.params.put(name, ToNumberConverter.toNumbers(values, paramExtractor, options.getConverters()));
		
        return this;
    }

    /**
     * Resets the calculator state and removes all its parameters.<br/>
     * This method is called automatically after each successful calculation.
     */
    public SCalc<RETURN_TYPE> reset() {
        this.params.clear();
        return this;
    }

    public SCalcOptions<RETURN_TYPE> getOptions() {
        return options;
    }

    public Map<String, Number[]> getParams() {
        return params;
    }
}
