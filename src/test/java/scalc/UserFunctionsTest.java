package scalc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import scalc.exceptions.CalculationException;
import scalc.interfaces.FunctionImpl;

import java.math.BigDecimal;

public class UserFunctionsTest {
	@Test
	public void testUserFunction() {
		FunctionImpl function = (options, functionParams) -> functionParams.get(0).multiply(new BigDecimal(-1).setScale(
				options.getCalculationScale(),
				options.getCalculationRoundingMode())
		);
		
		int result = SCalcBuilder.integerInstance()
				.expression("negate(2) + 1")
				.registerUserFunction("negate", function)
				.buildAndCalc();
		
		Assertions.assertEquals(-1, result);
	}
	
	@Test
	public void testUserFunction_Global() {
		SCalcBuilder.registerGlobalUserFunction("percent", (options, functionParams) -> functionParams.get(0)
				.multiply(new BigDecimal(100))
				.divide(functionParams.get(1), options.getCalculationScale(), options.getCalculationRoundingMode())
				.setScale(options.getCalculationScale(), options.getCalculationRoundingMode()));
		
		try {
			int result = SCalcBuilder.integerInstance()
					.expression("percent(12, 200)")
					.buildAndCalc();
			
			Assertions.assertEquals(6, result);
		} finally {
			SCalcBuilder.removeGlobalUserFunction("percent");
		}
	}
	
	@Test
	public void testUserFunction_InvalidFunctionName() {
		Assertions.assertThrows(CalculationException.class, () -> SCalcBuilder.integerInstance()
				.expression("return0(2) + 1")
				.registerUserFunction("return0", (options, functionParams) -> new BigDecimal(0))
				.buildAndCalc());
	}
}
