package scalc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SCalcBuilderTest {
	@Test
	public void testDoubleInstance() {
		double result = SCalcBuilder.doubleInstance()
				.expression("10.2 - 0.3")
				.buildAndCalc();
		
		Assertions.assertEquals(9.9, result, 0.0);
	}
	
	@Test
	public void testFloatInstance() {
		float result = SCalcBuilder.floatInstance()
				.expression("10.2 - 0.3")
				.buildAndCalc();
		
		Assertions.assertEquals(9.9F, result, 0.0F);
	}
	
	@Test
	public void testIntegerInstance() {
		int result = SCalcBuilder.integerInstance()
				.expression("10.2 - 0.3")
				.buildAndCalc();
		
		Assertions.assertEquals(9, result);
	}
	
	@Test
	public void testShortInstance() {
		short result = SCalcBuilder.shortInstance()
				.expression("10.2 - 0.3")
				.buildAndCalc();
		
		Assertions.assertEquals(9, result);
	}
	
	@Test
	public void testLongInstance() {
		long result = SCalcBuilder.longInstance()
				.expression("10.2 - 0.3")
				.buildAndCalc();
		
		Assertions.assertEquals(9L, result);
	}
	
	@Test
	public void testBigDecimalInstance() {
		BigDecimal result = SCalcBuilder.bigDecimalInstance()
				.expression("10.2 - 0.3")
				.buildAndCalc();
		
		Assertions.assertEquals(new BigDecimal("9.9").setScale(SCalcOptions.DEFAULT_SCALE, RoundingMode.HALF_UP), result);
	}
}