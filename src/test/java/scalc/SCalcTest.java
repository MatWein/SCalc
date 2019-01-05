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
        Map<String, Object> params = new HashMap<>();
        params.put("a", 10);
        params.put("b", 2.1);

        Double result = SCalcBuilder.doubleInstance()
                .expression("a + b")
                .params(params)
                .build()
                .calc();

        Assert.assertEquals(12.1, result, 0);
    }

    @Test
    public void calc_ComplexExpression_2() {
        Map<String, Object> params = new HashMap<>();
        params.put("a", 10);
        params.put("b", 2);

        BigDecimal result = SCalcBuilder.bigDecimalInstance()
                .expression("a + b * √(16)")
                .params(params)
                .resultScale(1, RoundingMode.HALF_UP)
                .build()
                .calc();

        Assert.assertEquals(new BigDecimal("18.0"), result);
    }

    @Test
    public void calc_Constant() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("20.11")
                .build()
                .calc();

        Assert.assertEquals(20.11, result, 0);
    }

    @Test
    public void calc_Variable() {
        Map<String, Object> params = new HashMap<>();
        params.put("someFancyVar1", 10);

        Double result = SCalcBuilder.doubleInstance()
                .expression("  someFancyVar1  ")
                .params(params)
                .build()
                .calc();

        Assert.assertEquals(10.0, result, 0);
    }

    @Test
    public void calc_Function() {
        Map<String, Object> params = new HashMap<>();
        params.put("var1", 4);

        Double result = SCalcBuilder.doubleInstance()
                .expression("Wurzel(var1)")
                .params(params)
                .build()
                .calc();

        Assert.assertEquals(2.0, result, 0);
    }
	
	@Test
	public void calc_Function_Pow() {
		Map<String, Object> params = new HashMap<>();
		params.put("var1", 4);
		
		Double result = SCalcBuilder.doubleInstance()
				.expression("Wurzel(var1) ^ 3")
				.params(params)
                .build()
				.calc();
		
		Assert.assertEquals(8.0, result, 0);
	}

    @Test
    public void calc_MultiplyAll() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("*")
                .params(2, 3, null, 4)
                .removeNullParameters(true)
                .build()
                .calc();

        Assert.assertEquals(24.0, result, 0);
    }

    @Test
    public void calc_NotRemoveNullParameters() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("*")
                .params(2, 3, null, 4)
                .removeNullParameters(false)
                .build()
                .calc();

        Assert.assertEquals(0.0, result, 0);
    }

    @Test
    public void calc_AddAll() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("+")
                .params(2, 3, 2)
                .build()
                .calc();

        Assert.assertEquals(7.0, result, 0);
    }

    @Test
    public void calc_PowAll() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("^")
                .params(2, 3, 2)
                .build()
                .calc();

        Assert.assertEquals(64.0, result, 0);
    }

    @Test
    public void calc_ParamNameValue() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("-")
                .params("a", 10, "b", 100, "c", 20)
                .build()
                .calc();

        Assert.assertEquals(-110.0, result, 0);
    }

    @Test
    public void calc_Money() {
        Map<String, Object> params = new HashMap<>();
        params.put("var1", 4);

        Money result = SCalcBuilder.instanceFor(Money.class)
                .expression("(√(16, 4) + 2) / (99.99 - 79.99 - 16)")
                .params(params)
                .registerConverter(Money.class, MoneyConverter.class)
                .build()
                .calc();

        Assert.assertEquals(1.0, result.getValue(), 0);
    }

    @Test
    public void calc_Pow() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("Wurzel(4 ^ 2)")
                .build()
                .calc();

        Assert.assertEquals(4.0, result, 0);
    }

    @Test
    public void calc_ComplexMultiline() {
        Double result = SCalcBuilder.doubleInstance()
                .expression(
                    "f(x, y)=10 + (x * y) - 1;" +
                    "g(x) = wurzel(x);" +
                    "return f(2, 3) + g(4);")
                .build()
                .calc();

        Assert.assertEquals(17.0, result, 0);
    }

    @Test
    public void calc_MultiParam() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("Wurzel(4, 2)")
                .build()
                .calc();

        Assert.assertEquals(2.0, result, 0);
    }
}