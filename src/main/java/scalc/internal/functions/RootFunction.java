package scalc.internal.functions;

import scalc.SCalcOptions;
import scalc.exceptions.CalculationException;
import scalc.interfaces.FunctionImpl;

import java.math.BigDecimal;
import java.util.List;

public class RootFunction implements FunctionImpl {
    public static final RootFunction INSTANCE = new RootFunction();
	
	@Override
    public BigDecimal call(SCalcOptions<?> options, List<BigDecimal> functionParams) {
        if (functionParams.size() == 1) {
	        BigDecimal value = functionParams.get(0);
	        BigDecimal root = new BigDecimal(2).setScale(options.getCalculationScale(), options.getCalculationRoundingMode());
	        return calc(options, value, root);
        } else if (functionParams.size() == 2) {
	        BigDecimal value = functionParams.get(0);
	        BigDecimal root = functionParams.get(1);
	        return calc(options, value, root);
        }
	
	    throw new CalculationException(String.format("Function '%s' has to have at least one argument and max. 2 arguments. " +
			    "Format: √(base, [default=2] power). Example: √(16) or √(16, 4)", getClass().getSimpleName()));
    }
	
	private BigDecimal calc(SCalcOptions<?> options, BigDecimal value, BigDecimal root) {
		return new BigDecimal(String.valueOf(Math.pow(value.doubleValue(), 1.0 / root.doubleValue())))
				.setScale(options.getCalculationScale(), options.getCalculationRoundingMode());
	}
}
