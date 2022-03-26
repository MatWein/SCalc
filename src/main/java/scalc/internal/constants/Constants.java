package scalc.internal.constants;

import scalc.SCalcOptions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class Constants {
	public static final int HALF_UP_CONST_VALUE = 0;
	public static final int HALF_EVEN_CONST_VALUE = 1;
	public static final int HALF_DOWN_CONST_VALUE = 2;
	
	public static Map<String, Number> getPredefinedConstants(SCalcOptions<?> options) {
		Map<String, Number> constants = new HashMap<>();
		
		BigDecimal pi = new BigDecimal(Math.PI).setScale(options.getCalculationScale(), options.getCalculationRoundingMode());
		constants.put("PI", pi);
		constants.put("π", pi);
		
		BigDecimal e = new BigDecimal(Math.E).setScale(options.getCalculationScale(), options.getCalculationRoundingMode());
		constants.put("E", e);
		
		// internal constants
		constants.put(RoundingMode.HALF_UP.name(), HALF_UP_CONST_VALUE);
		constants.put(RoundingMode.HALF_EVEN.name(), HALF_EVEN_CONST_VALUE);
		constants.put(RoundingMode.HALF_DOWN.name(), HALF_DOWN_CONST_VALUE);
		
		return constants;
	}
}
