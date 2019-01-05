package scalc;

import scalc.internal.converter.INumberConverter;

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Map;

public class SCalcOptions<RETURN_TYPE> {
    private Class<RETURN_TYPE> returnType;
    private String expression;

    private Map<String, Number> params;
    private Map<Class<?>, INumberConverter> converters;

    private int resultScale = 10;
    private RoundingMode resultRoundingMode = RoundingMode.HALF_UP;
    private MathContext resultMathContext = new MathContext(resultScale, resultRoundingMode);
    private int calculationScale = 10;
    private RoundingMode calculationRoundingMode = RoundingMode.HALF_UP;
    private MathContext calculationMathContext = new MathContext(calculationScale, calculationRoundingMode);
    private boolean removeNullParameters = true;

    public Class<RETURN_TYPE> getReturnType() {
        return returnType;
    }

    void setReturnType(Class<RETURN_TYPE> returnType) {
        this.returnType = returnType;
    }

    public int getResultScale() {
        return resultScale;
    }

    void setResultScale(int resultScale) {
        this.resultScale = resultScale;
    }

    public RoundingMode getResultRoundingMode() {
        return resultRoundingMode;
    }

    void setResultRoundingMode(RoundingMode resultRoundingMode) {
        this.resultRoundingMode = resultRoundingMode;
    }

    public MathContext getResultMathContext() {
        return resultMathContext;
    }

    void setResultMathContext(MathContext resultMathContext) {
        this.resultMathContext = resultMathContext;
    }

    public int getCalculationScale() {
        return calculationScale;
    }

    void setCalculationScale(int calculationScale) {
        this.calculationScale = calculationScale;
    }

    public RoundingMode getCalculationRoundingMode() {
        return calculationRoundingMode;
    }

    void setCalculationRoundingMode(RoundingMode calculationRoundingMode) {
        this.calculationRoundingMode = calculationRoundingMode;
    }

    public MathContext getCalculationMathContext() {
        return calculationMathContext;
    }

    void setCalculationMathContext(MathContext calculationMathContext) {
        this.calculationMathContext = calculationMathContext;
    }

    public boolean isRemoveNullParameters() {
        return removeNullParameters;
    }

    void setRemoveNullParameters(boolean removeNullParameters) {
        this.removeNullParameters = removeNullParameters;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Map<String, Number> getParams() {
        return params;
    }

    public void setParams(Map<String, Number> params) {
        this.params = params;
    }

    public Map<Class<?>, INumberConverter> getConverters() {
        return converters;
    }

    public void setConverters(Map<Class<?>, INumberConverter> converters) {
        this.converters = converters;
    }
}
