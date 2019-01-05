package scalc;

import org.junit.Assert;
import org.junit.Test;
import scalc.exceptions.CalculationException;
import scalc.test.model.Money;
import scalc.test.model.MoneyConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class SCalcTest {
    @Test
    public void calc_SimpleExpression_1() {
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
    public void calc_SimpleExpression_2() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("a + b - c")
                .parameter("a", 10)
                .parameter("b", 5)
                .parameter("c", 15)
                .build()
                .calc();

        Assert.assertEquals(0.0, result, 0);
    }

    @Test
    public void calc_BigDecimal() {
        BigDecimal result = SCalcBuilder.bigDecimalInstance()
                .expression("4 * 4 - var1")
                .parameter("var1", new BigDecimal(2))
                .build()
                .calc();

        Assert.assertEquals(14.0, result.doubleValue(), 0);
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
    public void calc_Money_Global() {
        SCalcBuilder.registerGlobalConverter(Money.class, MoneyConverter.class);

        Money result = SCalcBuilder.instanceFor(Money.class)
                .expression("var1 - var2")
                .params("var1", 10.9, "var2", 0.9)
                .build()
                .calc();

        Assert.assertEquals(10.0, result.getValue(), 0);
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
    public void calc_ParamName() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("u*Wurzel(u)")
                .parameter("u", 81)
                .build()
                .calc();

        Assert.assertEquals(9.0 * 81.0, result, 0);
    }

    @Test
    public void calc_ComplexMultiline() {
        Double result = SCalcBuilder.doubleInstance()
                .expression(
                    "f(x, y)=10 + (x * y) - 1;\r\n" +
                    "g(x) = wurzel(x);" +
                    "variable1 = 7; " +
                    "return f(2, 3) + g(4) - variable1;")
                .build()
                .calc();

        Assert.assertEquals(10.0, result, 0);
    }

    @Test(expected = CalculationException.class)
    public void calc_ComplexMultiline_NoReturn() {
        SCalcBuilder.doubleInstance()
                .expression(
                    "a=10;\r\n" +
                    "b=12;")
                .build()
                .calc();
    }

    @Test
    public void calc_ComplexMultiline2() {
        Double result = SCalcBuilder.doubleInstance()
                .expression(
                    "f(x, y)=10 + (x * y) - 1;\r\n" +
                    "g(x) = wurzel(x);" +
                    "variable1 = 7; " +
                    "return f(2, 3) + g(4) - variable1;")
                .build()
                .calc();

        Assert.assertEquals(10.0, result, 0);
    }

    @Test
    public void calc_MultiParam() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("Wurzel(4, 2)")
                .build()
                .calc();

        Assert.assertEquals(2.0, result, 0);
    }

    @Test
    public void calc_SumAllParams() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("Sum(ALL_PARAMS)")
                .params(10, 5, 2, 7)
                .build()
                .calc();

        Assert.assertEquals(24.0, result, 0);
    }

    @Test
    public void calc_SumAllParams_NoParams() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("Sum(ALL_PARAMS)")
                .build()
                .calc();

        Assert.assertEquals(0.0, result, 0);
    }

    @Test
    public void calc_GlobalConstants() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("PI * 2 - π")
                .resultScale(2)
                .build()
                .calc();

        Assert.assertEquals(3.14, result, 0);
    }

    @Test
    public void calc_CaseInsensitiveNames() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("pI * 2 - π + VAR1")
                .resultScale(2)
                .parameter("var1", 1)
                .build()
                .calc();

        Assert.assertEquals(4.14, result, 0);
    }

    @Test
    public void calc_FloatingPoint1() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("a+b")
                .parameter("a", 0.7)
                .parameter("b", 0.1)
                .build()
                .calc();

        Assert.assertEquals(0.8, result, 0);
        Assert.assertEquals(0.7999999999999999, 0.7 + 0.1, 0);

        result = SCalcBuilder.doubleInstance()
                .expression("0.9 - 0.1")
                .build()
                .calc();

        Assert.assertEquals(0.8, result, 0);
        Assert.assertEquals(0.8, 0.9 - 0.1, 0);
    }

    @Test
    public void calc_0E10() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("a+b")
                .parameter("a", new BigDecimal("0E-10"))
                .parameter("b", 5)
                .build()
                .calc();

        Assert.assertEquals(5.0, result, 0);
    }
}