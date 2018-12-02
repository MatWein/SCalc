package scalc;

import org.junit.Assert;
import org.junit.Test;
import scalc.test.model.Money;
import scalc.test.model.MoneyConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class SCalcTest {
    @Test
    public void calc_ComplexExpression_1() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("a", 10);
        params.put("b", 2.1);

        Double result = SCalc.doubleInstance()
                .expression("a + b")
                .params(params)
                .calc();

        Assert.assertEquals(12.1, result, 0);
    }

    @Test
    public void calc_ComplexExpression_2() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("a", 10);
        params.put("b", 2);

        BigDecimal result = SCalc.bigDecimalInstance()
                .expression("a + b * √(16)")
                .params(params)
                .resultScale(1, RoundingMode.HALF_UP)
                .calc();

        Assert.assertEquals(new BigDecimal("18.0"), result);
    }

    @Test
    public void calc_Constant() {
        Double result = SCalc.doubleInstance()
                .expression("20.11")
                .calc();

        Assert.assertEquals(20.11, result, 0);
    }

    @Test
    public void calc_Variable() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("someFancyVar1", 10);

        Double result = SCalc.doubleInstance()
                .expression("  someFancyVar1  ")
                .params(params)
                .calc();

        Assert.assertEquals(10.0, result, 0);
    }

    @Test
    public void calc_Function() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("var1", 4);

        Double result = SCalc.doubleInstance()
                .expression("Wurzel(var1)")
                .params(params)
                .calc();

        Assert.assertEquals(2.0, result, 0);
    }
	
	@Test
	public void calc_Function_Pow() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("var1", 4);
		
		Double result = SCalc.doubleInstance()
				.expression("Wurzel(var1) ^ 3")
				.params(params)
				.calc();
		
		Assert.assertEquals(8.0, result, 0);
	}

    @Test
    public void calc_MultiplyAll() {
        Double result = SCalc.doubleInstance()
                .expression("*")
                .paramsInOrder(2, 3, null, 4)
                .calc();

        Assert.assertEquals(24.0, result, 0);
    }

    @Test
    public void calc_NotRemoveNullParameters() {
        Double result = SCalc.doubleInstance()
                .expression("*")
                .paramsInOrder(2, 3, null, 4)
                .removeNullParameters(false)
                .calc();

        Assert.assertEquals(0.0, result, 0);
    }

    @Test
    public void calc_PowAll() {
        Double result = SCalc.doubleInstance()
                .expression("^")
                .paramsInOrder(2, 3, 2)
                .calc();

        Assert.assertEquals(64.0, result, 0);
    }

    @Test
    public void calc_Performance() {
        long timeStart = System.currentTimeMillis();
        SCalc<Double> sCalc = SCalc.doubleInstance()
                .expression("a^b");
        long timeEnd = System.currentTimeMillis();

        System.out.println(String.format("Time needed for expression parsing: %sms.", timeEnd - timeStart));

        Double result = 0.0;
        timeStart = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            result = sCalc
                    .parameter("a", 10)
                    .parameter("b", 2)
                    .calc();
        }
        timeEnd = System.currentTimeMillis();

        Assert.assertEquals(100.0, result, 0);
        System.out.println(String.format("Time needed for calculation: %sms.", timeEnd - timeStart));
    }

    @Test
    public void calc_ParamNameValue() {
        Double result = SCalc.doubleInstance()
                .expression("-")
                .paramsFromNameValuePairs("a", 10, "b", 100, "c", 20)
                .calc();

        Assert.assertEquals(-110.0, result, 0);
    }

    @Test
    public void calc_Money() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("var1", 4);

        Money result = SCalc.instanceFor(Money.class)
                .expression("(√(16, 3) + 2) / (99.99 - 79.99 - 16)")
                .params(params)
                .registerConverter(Money.class, MoneyConverter.class)
                .calc();

        Assert.assertEquals(1.0, result.getValue(), 0);
    }

    @Test
    public void calc_Pow() {
        Double result = SCalc.doubleInstance()
                .expression("Wurzel(4 ^ 2)")
                .calc();

        Assert.assertEquals(4.0, result, 0);
    }
}