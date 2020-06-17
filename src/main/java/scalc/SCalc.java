package scalc;

import scalc.exceptions.CalculationException;
import scalc.internal.ParamExtractor;
import scalc.internal.calc.SCalcController;
import scalc.internal.converter.ToNumberConverter;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The calculator class. Do not create the instance by yourself. Please use SCalcBuilder!
 */
public class SCalc<RETURN_TYPE> {
    private final SCalcOptions<RETURN_TYPE> options;
    private Map<String, Number> params = new LinkedHashMap<>();

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
    public SCalc<RETURN_TYPE> params(Map<String, Object> params) {
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
    public SCalc<RETURN_TYPE> params(Object... paramsAsArray) {
        if (paramsAsArray != null && paramsAsArray.length > 0) {
            if (paramsAsArray[0] instanceof CharSequence) {
                this.params.putAll(ParamExtractor.extractParamsFromNameValuePairs(options.getConverters(), paramsAsArray));
            } else {
                this.params.putAll(ParamExtractor.extractParamsWithRandomName(options.getConverters(), paramsAsArray));
            }
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
    public SCalc<RETURN_TYPE> paramsAsCollection(Collection<?> params) {
        return params(params.toArray(new Object[] {}));
    }

    /**
     * [OPTIONAL] Single parameter for calculation. Can be used multiple times to add more than one param.
     * Optional if the expression does not have any params.
     * @param name Name of the param
     * @param value Value of the param as java.lang.Number or custom type.
     */
    public SCalc<RETURN_TYPE> parameter(String name, Object value) {
        this.params.put(name, ToNumberConverter.toNumber(value, options.getConverters()));
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

    public Map<String, Number> getParams() {
        return params;
    }
}
