package scalc;

import scalc.interfaces.FunctionImpl;
import scalc.interfaces.INumberConverter;

import java.math.RoundingMode;
import java.util.Map;
import java.util.function.Consumer;

public final class SCalcOptions<RETURN_TYPE> {
	public static final int DEFAULT_SCALE = 10;
	
	private Class<RETURN_TYPE> returnType;
    private String expression;
    private Map<Class<?>, INumberConverter> converters;
    private Map<String, FunctionImpl> userFunctions;
    private int resultScale = DEFAULT_SCALE;
    private RoundingMode resultRoundingMode = RoundingMode.HALF_UP;
    private int calculationScale = DEFAULT_SCALE;
    private RoundingMode calculationRoundingMode = RoundingMode.HALF_UP;
    private boolean debug = false;
    private Consumer<String> debugLogger = System.out::println;
    
    SCalcOptions() { }
    
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

    public String getExpression() {
        return expression;
    }

    void setExpression(String expression) {
        this.expression = expression;
    }

    public Map<Class<?>, INumberConverter> getConverters() {
        return converters;
    }

    void setConverters(Map<Class<?>, INumberConverter> converters) {
        this.converters = converters;
    }
    
    public boolean isDebug() {
        return debug;
    }
    
    void setDebug(boolean debug) {
        this.debug = debug;
    }
    
    public Consumer<String> getDebugLogger() {
        return debugLogger;
    }
    
    void setDebugLogger(Consumer<String> debugLogger) {
        this.debugLogger = debugLogger;
    }
	
	public Map<String, FunctionImpl> getUserFunctions() {
		return userFunctions;
	}
	
	void setUserFunctions(Map<String, FunctionImpl> userFunctions) {
		this.userFunctions = userFunctions;
	}
}
