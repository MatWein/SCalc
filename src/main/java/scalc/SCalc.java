package scalc;

import scalc.internal.SCalcController;
import scalc.internal.converter.INumberConverter;
import scalc.internal.converter.ToNumberConverter;
import scalc.internal.expr.Expression;

import java.util.Map;

public class SCalc<RETURN_TYPE> {
    private final Expression expression;
    private final Map<String, Number> params;
    private final Map<Class<?>, INumberConverter> converters;
    private final SCalcOptions<RETURN_TYPE> options;

    SCalc(Expression expression,
            Map<String, Number> params,
            Map<Class<?>, INumberConverter> converters,
            SCalcOptions<RETURN_TYPE> options) {

        if (expression == null) {
            throw new IllegalArgumentException("Expression cannot be empty. Please call the build() method before calculation.");
        }

        this.expression = expression;
        this.params = params;
        this.converters = converters;
        this.options = options;
    }

    public SCalc<RETURN_TYPE> parameter(String name, Object value) {
        if (options.isRemoveNullParameters() && value == null) {
            return this;
        }

        this.options.getParams().put(name, value);
        this.params.put(name, ToNumberConverter.toNumber(value, converters));
        return this;
    }

    public RETURN_TYPE calc() {
        return SCalcController.calc(this);
    }

    public Expression getExpression() {
        return expression;
    }

    public Map<String, Number> getParams() {
        return params;
    }

    public Map<Class<?>, INumberConverter> getConverters() {
        return converters;
    }

    public SCalcOptions<RETURN_TYPE> getOptions() {
        return options;
    }
}
