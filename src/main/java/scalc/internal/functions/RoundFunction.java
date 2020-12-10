package scalc.internal.functions;

import scalc.SCalcOptions;
import scalc.exceptions.CalculationException;
import scalc.internal.constants.Constants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class RoundFunction implements FunctionImpl {
	public static final RoundFunction INSTANCE = new RoundFunction();
	
	private static final int DEFAULT_ROUND_SCALE = 2;
	private static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;
	
	@Override
	public BigDecimal call(SCalcOptions<?> options, List<BigDecimal> functionParams) {
		if (functionParams.size() == 1) {
			BigDecimal value = functionParams.get(0);
			return calc(value, DEFAULT_ROUND_SCALE, DEFAULT_ROUNDING_MODE);
		} else if (functionParams.size() == 2) {
			BigDecimal value = functionParams.get(0);
			int scale = functionParams.get(1).intValue();
			
			return calc(value, scale, DEFAULT_ROUNDING_MODE);
		} else if (functionParams.size() == 3) {
			BigDecimal value = functionParams.get(0);
			int scale = functionParams.get(1).intValue();
			int roundingModeConst = functionParams.get(2).intValue();
			RoundingMode roundingMode = mapRoundingMode(roundingModeConst);
			
			return calc(value, scale, roundingMode);
		}
		
		throw new CalculationException(String.format("Function '%s' has to have at least one argument and max. 3 arguments. " +
				"Format: round(value, [default=2] scale, [default=HALF_UP] roundingMode). Example: round(0.5, 1, HALF_DOWN)",
				getClass().getSimpleName()));
	}
	
	private RoundingMode mapRoundingMode(int roundingModeConst) {
		if (roundingModeConst == Constants.HALF_UP_CONST_VALUE) {
			return RoundingMode.HALF_UP;
		} else if (roundingModeConst == Constants.HALF_EVEN_CONST_VALUE) {
			return RoundingMode.HALF_EVEN;
		} else if (roundingModeConst == Constants.HALF_DOWN_CONST_VALUE) {
			return RoundingMode.HALF_DOWN;
		}
		
		throw new CalculationException(String.format("Invalid rounding type '%s'. " +
				"Possible values: HALF_UP (0), HALF_EVEN (1), HALF_DOWN (2)", roundingModeConst));
	}
	
	private BigDecimal calc(BigDecimal value, int scale, RoundingMode roundingMode) {
		return value.setScale(scale, roundingMode);
	}
}
