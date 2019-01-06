package scalc;

import org.junit.Assert;
import org.junit.Test;
import scalc.exceptions.CalculationException;
import scalc.internal.converter.INumberConverter;
import scalc.test.model.Money;
import scalc.test.model.MoneyConverter;
import scalc.test.model.TestDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SCalcTest {
    @Test
    public void calc_SimpleExpression_1() {
        Map<String, Object> params = new HashMap<>();
        params.put("a", 10);
        params.put("b", 2.1);

        Double result = SCalcBuilder.doubleInstance()
                .expression("a + b")
                .build()
                .params(params)
                .calc();

        Assert.assertEquals(12.1, result, 0);
    }

    @Test
    public void calc_SimpleExpression_2() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("a + b - c")
                .build()
                .parameter("a", 10)
                .parameter("b", 5)
                .parameter("c", 15)
                .calc();

        Assert.assertEquals(0.0, result, 0);
    }

    @Test
    public void calc_BigDecimal() {
        BigDecimal result = SCalcBuilder.bigDecimalInstance()
                .expression("4 * 4 - var1")
                .build()
                .parameter("var1", new BigDecimal(2))
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
                .resultScale(1, RoundingMode.HALF_UP)
                .build()
                .params(params)
                .calc();

        Assert.assertEquals(new BigDecimal("18.0"), result);
    }

    @Test
    public void calc_Constant() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("20.11")
                .buildAndCalc();

        Assert.assertEquals(20.11, result, 0);
    }

    @Test
    public void calc_Variable() {
        Map<String, Object> params = new HashMap<>();
        params.put("someFancyVar1", 10);

        Double result = SCalcBuilder.doubleInstance()
                .expression("  someFancyVar1  ")
                .build()
                .params(params)
                .calc();

        Assert.assertEquals(10.0, result, 0);
    }

    @Test
    public void calc_Function() {
        Map<String, Object> params = new HashMap<>();
        params.put("var1", 4);

        Double result = SCalcBuilder.doubleInstance()
                .expression("Wurzel(var1)")
                .build()
                .params(params)
                .calc();

        Assert.assertEquals(2.0, result, 0);
    }
	
	@Test
	public void calc_Function_Pow() {
		Map<String, Object> params = new HashMap<>();
		params.put("var1", 4);
		
		Double result = SCalcBuilder.doubleInstance()
				.expression("Wurzel(var1) ^ 3")
                .build()
				.params(params)
				.calc();
		
		Assert.assertEquals(8.0, result, 0);
	}

    @Test
    public void calc_MultiplyAll() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("*")
                .removeNullParameters(true)
                .build()
                .params(2, 3, null, 4)
                .calc();

        Assert.assertEquals(24.0, result, 0);
    }

    @Test
    public void calc_NotRemoveNullParameters() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("*")
                .removeNullParameters(false)
                .build()
                .params(2, 3, null, 4)
                .calc();

        Assert.assertEquals(0.0, result, 0);
    }

    @Test
    public void calc_AddAll() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("+")
                .build()
                .params(2, 3, 2)
                .calc();

        Assert.assertEquals(7.0, result, 0);
    }

    @Test
    public void calc_PowAll() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("^")
                .build()
                .params(2, 3, 2)
                .calc();

        Assert.assertEquals(64.0, result, 0);
    }

    @Test
    public void calc_ParamNameValue() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("-")
                .build()
                .params("a", 10, "b", 100, "c", 20)
                .calc();

        Assert.assertEquals(-110.0, result, 0);
    }

    @Test
    public void calc_Money() {
        Map<String, Object> params = new HashMap<>();
        params.put("var1", 4);

        Money result = SCalcBuilder.instanceFor(Money.class)
                .expression("(√(16, 4) + 2) / (99.99 - 79.99 - 16)")
                .registerConverter(Money.class, MoneyConverter.class)
                .build()
                .params(params)
                .calc();

        Assert.assertEquals(1.0, result.getValue(), 0);
    }

    @Test
    public void calc_Money_Global() {
        SCalcBuilder.registerGlobalConverter(Money.class, MoneyConverter.class);

        Money result = SCalcBuilder.instanceFor(Money.class)
                .expression("var1 - var2")
                .build()
                .params("var1", 10.9, "var2", 0.9)
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
                .build()
                .parameter("u", 81)
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
                .buildAndCalc();

        Assert.assertEquals(10.0, result, 0);
    }

    @Test(expected = CalculationException.class)
    public void calc_ComplexMultiline_NoReturn() {
        SCalcBuilder.doubleInstance()
                .expression(
                    "a=10;\r\n" +
                    "b=12;")
                .buildAndCalc();
    }

    @Test
    public void calc_ComplexMultiline2() {
        Double result = SCalcBuilder.doubleInstance()
                .expression(
                    "f(x, y)=10 + (x * y) - 1;\r\n" +
                    "g(x) = wurzel(x);" +
                    "variable1 = 7; " +
                    "return f(2, 3) + g(4) - variable1;")
                .buildAndCalc();

        Assert.assertEquals(10.0, result, 0);
    }

    @Test
    public void calc_MultiParam() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("Wurzel(4, 2)")
                .buildAndCalc();

        Assert.assertEquals(2.0, result, 0);
    }

    @Test
    public void calc_SumAllParams() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("Sum(ALL_PARAMS)")
                .build()
                .params(10, 5, 2, 7)
                .calc();

        Assert.assertEquals(24.0, result, 0);
    }

    @Test
    public void calc_SumAllParams_NoParams() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("Sum(ALL_PARAMS)")
                .buildAndCalc();

        Assert.assertEquals(0.0, result, 0);
    }

    @Test
    public void calc_GlobalConstants() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("PI * 2 - π")
                .resultScale(2)
                .buildAndCalc();

        Assert.assertEquals(3.14, result, 0);
    }

    @Test
    public void calc_CaseInsensitiveNames() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("pI * 2 - π + VAR1")
                .resultScale(2)
                .build()
                .parameter("var1", 1)
                .calc();

        Assert.assertEquals(4.14, result, 0);
    }

    @Test
    public void calc_FloatingPoint1() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("a+b")
                .build()
                .parameter("a", 0.7)
                .parameter("b", 0.1)
                .calc();

        Assert.assertEquals(0.8, result, 0);
        Assert.assertEquals(0.7999999999999999, 0.7 + 0.1, 0);

        result = SCalcBuilder.doubleInstance()
                .expression("0.9 - 0.1")
                .buildAndCalc();

        Assert.assertEquals(0.8, result, 0);
        Assert.assertEquals(0.8, 0.9 - 0.1, 0);
    }

    @Test
    public void calc_0E10() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("a+b")
                .build()
                .parameter("a", new BigDecimal("0E-10"))
                .parameter("b", 5)
                .calc();

        Assert.assertEquals(5.0, result, 0);
    }

    @Test
    public void calc_SumDtos() {
        Set<TestDto> dtos = new HashSet<>();
        dtos.add(new TestDto(10.0));
        dtos.add(new TestDto(5.1));
        dtos.add(null);
        dtos.add(new TestDto(2));

        INumberConverter<TestDto> numberConverter = new INumberConverter<TestDto>() {
            @Override
            public BigDecimal toBigDecimal(TestDto input) {
                if (input == null) {
                    return null;
                }
                return new BigDecimal(input.getValueToExtract()).setScale(2, RoundingMode.HALF_UP);
            }

            @Override
            public TestDto fromBigDecimal(BigDecimal input) {
                throw new RuntimeException("Not implemented");
            }
        };

        Double result = SCalcBuilder.doubleInstance()
                .expression("+")
                .registerConverter(TestDto.class, numberConverter)
                .build()
                .paramsAsCollection(dtos)
                .calc();

        Assert.assertEquals(17.1, result, 0);

        result = SCalcBuilder.doubleInstance()
                .expression("∑(ALL_PARAMS)")
                .registerConverter(TestDto.class, numberConverter)
                .build()
                .paramsAsCollection(dtos)
                .calc();

        Assert.assertEquals(17.1, result, 0);
    }

    @Test
    public void calc_PowChar() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("a² / b³")
                .build()
                .params("a", 3, "b", 2)
                .calc();

        Assert.assertEquals(1.125, result, 0);
    }

    @Test
    public void calc_Min() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("min(16, 4, 24, 1)")
                .buildAndCalc();

        Assert.assertEquals(1.0, result, 0);
    }

    @Test
    public void calc_Min2() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("min(16, -4, 24, 1)")
                .buildAndCalc();

        Assert.assertEquals(-4.0, result, 0);
    }

    @Test
    public void calc_Min3() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("-min(16, -4 / 2, 24, 1)")
                .buildAndCalc();

        Assert.assertEquals(2.0, result, 0);
    }

    @Test
    public void calc_Max() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("max(16, 4, 24, 1)")
                .buildAndCalc();

        Assert.assertEquals(24.0, result, 0);
    }

    @Test
    public void calc_Avg() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("avg(16, 4, 24, 1)")
                .buildAndCalc();

        Assert.assertEquals(11.25, result, 0);
    }

    @Test
    public void calc_Abs() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("abs(16)")
                .buildAndCalc();

        Assert.assertEquals(16.0, result, 0);
    }

    @Test
    public void calc_Abs2() {
        Double result = SCalcBuilder.doubleInstance()
                .expression("abs(-16)")
                .buildAndCalc();

        Assert.assertEquals(16.0, result, 0);
    }

    @Test
    public void calc_MultiLineCalculation() {
        Double result = SCalcBuilder.doubleInstance()
                .expression(
                    "umsatzProMonat = 50000;" +
                    "umsatzProJahr = umsatzProMonat * 12;" +
                    "kostenProMonat = 2000;" +
                    "kostenProJahr = kostenProMonat * 12;" +
                    "gewinnProJahr = umsatzProJahr - kostenProJahr;" +
                    "return gewinnProJahr;"
                )
                .buildAndCalc();

        Assert.assertEquals(576000.0, result, 0);
    }
}