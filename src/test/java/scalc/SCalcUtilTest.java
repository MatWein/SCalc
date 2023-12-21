package scalc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import scalc.test.model.Money;
import scalc.test.model.MoneyConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class SCalcUtilTest {
	@Test
	public void testRound() {
		double result = SCalcUtil.round(10.55, 1, RoundingMode.HALF_UP, double.class);
		Assertions.assertEquals(10.6, result, 0.0);
	}
	
	@Test
	public void testRound_MoreThanInternalScale() {
		double result = SCalcUtil.round(10.012345678912345, 14, RoundingMode.HALF_UP, double.class);
		Assertions.assertEquals(10.01234567891235, result, 0.0);
	}
	
	@Test
	public void testRound_Null() {
		BigDecimal result = SCalcUtil.round(null, 1, RoundingMode.HALF_UP, BigDecimal.class);
		Assertions.assertNull(result);
	}
	
	@Test
	public void testRound_SameReturnTypeAsInput_Null() {
		Double result = SCalcUtil.round(null, 2);
		Assertions.assertNull(result);
	}
	
	@Test
	public void testRound_SameReturnTypeAsInput_Double() {
		double result = SCalcUtil.round(10.5567, 2);
		Assertions.assertEquals(10.56, result, 0.0);
	}
	
	@Test
	public void testRound_SameReturnTypeAsInput_Money() {
		SCalcBuilder.registerGlobalConverter(Money.class, MoneyConverter.class);
		try {
			Money result = SCalcUtil.round(new Money(0.000012), 5);
			Assertions.assertEquals(0.00001, result.getValue(), 0.0);
		} finally {
			SCalcBuilder.removeGlobalConverter(Money.class);
		}
	}
	
	@Test
	public void testRound_DifferentReturnTypeAsInput() {
		BigDecimal result = SCalcUtil.round(-999.99, 2, BigDecimal.class);
		Assertions.assertEquals(BigDecimal.valueOf(-999.99), result);
	}
	
	@Test
	public void testSummarize() {
		double result = SCalcUtil.summarize(double.class, -10.0, 15.0, 60.1);
		Assertions.assertEquals(65.1, result, 0.0);
	}
	
	@Test
	public void testSummarize_ReturnZeroIfEmpty() {
		double result = SCalcUtil.summarize(double.class);
		Assertions.assertEquals(0.0, result, 0.0);
	}
	
	@Test
	public void testSummarize_Collection() {
		List<Double> numbers = new ArrayList<>();
		numbers.add(1.0);
		numbers.add(2.1);
		numbers.add(3.2);
		
		Double result = SCalcUtil.summarizeCollection(Double.class, numbers);
		Assertions.assertEquals(6.3, result, 0.0);
	}
	
	@Test
	public void testSummarize_Collection_Empty() {
		List<Double> numbers = new ArrayList<>();
		
		double result = SCalcUtil.summarizeCollection(double.class, numbers);
		Assertions.assertEquals(0.0, result, 0.0);
	}
	
	@Test
	public void testSummarize_Collection_WithParamExtractor() {
		List<Money> numbers = new ArrayList<>();
		numbers.add(new Money(1.0));
		numbers.add(new Money(2.1));
		numbers.add(new Money(3.2));
		
		Double result = SCalcUtil.summarizeCollection(Double.class, numbers, Money::getValue);
		Assertions.assertEquals(6.3, result, 0.0);
	}
	
	@Test
	public void testSummarize_Collection_Null() {
		List<Double> numbers = null;
		
		double result = SCalcUtil.summarize(double.class, numbers);
		Assertions.assertEquals(0.0, result, 0.0);
	}
	
	@Test
	public void testSummarize_DifferentInputTypes() {
		double result = SCalcUtil.summarize(double.class, 1.0, BigDecimal.valueOf(2.0), 7L);
		Assertions.assertEquals(10.0, result, 0.0);
	}
	
	@Test
	public void testSummarize_LimitToInternalScale() {
		double result = SCalcUtil.summarize(double.class, 1.0123456789123456);
		Assertions.assertEquals(1.0123456789, result, 0.0);
	}
}