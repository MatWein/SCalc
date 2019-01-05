package scalc.internal.functions;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

public class RootFunction implements FunctionImpl {
    public static final RootFunction INSTANCE = new RootFunction();

    public BigDecimal call(MathContext mathContext, List<BigDecimal> functionParams) {
        if (functionParams.size() == 1) {
	        BigDecimal value = functionParams.get(0);
	        BigDecimal root = new BigDecimal(2, mathContext);
	        return calc(mathContext, value, root);
        } else if (functionParams.size() == 2) {
	        BigDecimal value = functionParams.get(0);
	        BigDecimal root = functionParams.get(1);
	        return calc(mathContext, value, root);
        }
	
	    throw new IllegalArgumentException(String.format("Function '%s' has to have at least one argument and max. 2 arguments. Format: √(base, [default=2] power). Example: √(16) or √(16, 4)", getClass().getSimpleName()));
    }
	
	private BigDecimal calc(MathContext mathContext, BigDecimal value, BigDecimal root) {
		return new BigDecimal(String.valueOf(Math.pow(value.doubleValue(), 1.0 / root.doubleValue())), mathContext);
	}
}
