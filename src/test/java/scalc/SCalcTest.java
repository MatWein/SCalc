package scalc;

import org.junit.Assert;
import org.junit.Test;
import scalc.exceptions.CalculationException;
import scalc.interfaces.INumberConverter;
import scalc.test.model.Money;
import scalc.test.model.MoneyConverter;
import scalc.test.model.Percentage;
import scalc.test.model.TestDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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
                .multiplyExpression()
                .build()
                .params(2, 3, null, 4)
                .calc();

        Assert.assertEquals(0.0, result, 0);
    }

    @Test
    public void calc_NotRemoveNullParameters() {
        Double result = SCalcBuilder.doubleInstance()
                .multiplyExpression()
                .build()
                .params(2, 3, null, 4)
                .calc();

        Assert.assertEquals(0.0, result, 0);
    }

    @Test
    public void calc_AddAll() {
        Double result = SCalcBuilder.doubleInstance()
                .sumExpression()
                .build()
                .params(2, 3, 2)
                .calc();

        Assert.assertEquals(7.0, result, 0);
    }

    @Test
    public void calc_PowAll() {
        Double result = SCalcBuilder.doubleInstance()
                .powExpression()
                .build()
                .params(2, 3, 2)
                .calc();

        Assert.assertEquals(64.0, result, 0);
    }
	
	@Test
	public void calc_DivideAll() {
		Double result = SCalcBuilder.doubleInstance()
				.divideExpression()
				.build()
				.params(20, 5, 2)
				.calc();
		
		Assert.assertEquals(2.0, result, 0);
	}

    @Test
    public void calc_ParamNameValue() {
        Double result = SCalcBuilder.doubleInstance()
                .subtractExpression()
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
                .expression("Sum(ALL_PARAMS) * 2")
                .build()
                .params(10, 5, 2, 7)
                .calc();

        Assert.assertEquals(48.0, result, 0);
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
        dtos.add(new TestDto(2.0));

        INumberConverter<TestDto> numberConverter = new INumberConverter<TestDto>() {
            @Override
            public BigDecimal toBigDecimal(TestDto input) {
                if (input == null) {
                    return null;
                }
                return BigDecimal.valueOf(input.getValueToExtract()).setScale(2, RoundingMode.HALF_UP);
            }

            @Override
            public TestDto fromBigDecimal(BigDecimal input) {
                throw new RuntimeException("Not implemented");
            }
        };

        Double result = SCalcBuilder.doubleInstance()
                .sumExpression()
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
	
	@Test
	public void calc_Scale() {
		Double result = SCalcBuilder.doubleInstance()
				.expression("root(11) * 100000")
				.calculationScale(4)
				.buildAndCalc();
		
		Assert.assertEquals(331660.0, result, 0);
		System.err.println(Math.sqrt(11));
	}
	
	@Test(expected = CalculationException.class)
	public void calc_DivisionByZero() {
    	try {
		    SCalcBuilder.bigDecimalInstance()
				    .expression("(var1 / var2) + 77")
				    .build()
				    .parameter("var1", 100.0)
				    .parameter("var2", 0.0)
				    .calc();
	    } catch (Throwable e) {
		    e.printStackTrace();
    		throw e;
	    }
	}
	
	@Test
	public void testNullParameter() {
		double result = SCalcBuilder.doubleInstance()
				.expression("AVG(anleihen, investmentfonds, aktien, hedgefonds, optionsscheine, zertifikate, beteiligungen)")
				.resultScale(2, RoundingMode.HALF_UP)
				.build()
				.parameter("anleihen", 1.0)
				.parameter("investmentfonds", 2.0)
				.parameter("aktien", 3.0)
				.parameter("hedgefonds", null)
				.parameter("optionsscheine", 1.0)
				.parameter("zertifikate", 2.0)
				.parameter("beteiligungen", 3.0)
				.calc();
		
		Assert.assertEquals(12.0 / 7.0, result, 0.01);
	}
	
	@Test
	public void testNullParameter_FirstPlace() {
		int result = SCalcBuilder.doubleInstance()
				.expression("AVG(anleihen, investmentfonds, aktien, hedgefonds, optionsscheine, zertifikate, beteiligungen)")
				.resultScale(0, RoundingMode.HALF_UP)
				.build()
				.parameter("anleihen", null)
				.parameter("investmentfonds", null)
				.parameter("aktien", null)
				.parameter("hedgefonds", null)
				.parameter("optionsscheine", null)
				.parameter("zertifikate", null)
				.parameter("beteiligungen", null)
				.calc()
				.intValue();
		
		Assert.assertEquals(0, result);
	}
	
	@Test
	public void testNullParameter_Round_OneParam() {
		double result = SCalcBuilder.doubleInstance()
				.expression("round(0.5)")
				.buildAndCalc();
		
		Assert.assertEquals(0.5, result, 0);
	}
	
	@Test
	public void testNullParameter_Round_TwoParams() {
		double result = SCalcBuilder.doubleInstance()
				.expression("round(0.5, 0)")
				.buildAndCalc();
		
		Assert.assertEquals(1.0, result, 0);
	}
	
	@Test
	public void testNullParameter_Round_ThreeParams() {
		double result = SCalcBuilder.doubleInstance()
				.expression("round(0.5, 0, HALF_DOWN)")
				.buildAndCalc();
		
		Assert.assertEquals(0.0, result, 0);
	}
	
	@Test
	public void testNullParameter_Round_ThreeParams2() {
		double result = SCalcBuilder.doubleInstance()
				.expression("round(0.999999999, 4, HALF_UP)")
				.buildAndCalc();
		
		Assert.assertEquals(1.0, result, 0);
	}
	
	@Test
	public void testParamsAsList() {
		double result = SCalcBuilder.doubleInstance()
				.expression("param0 + param1 - param2 + param3")
				.build()
				.params(10.0, 20.0, 5.0)
				.params(1)
				.calc();
		
		Assert.assertEquals(26.0, result, 0);
	}
	
	@Test
	public void testParametersWithExtractor() {
		Set<TestDto> dtos = new HashSet<>();
		dtos.add(new TestDto(100.0));
		dtos.add(new TestDto(0.01));
		dtos.add(null);
		dtos.add(new TestDto(200.0));
		dtos.add(new TestDto(null));
		
		double result = SCalcBuilder.doubleInstance()
				.sumExpression()
				.build()
				.paramsAsCollection(TestDto::getValueToExtract, dtos)
				.params(TestDto::getValueToExtract, dtos.toArray(new TestDto[] {}))
				.calc();
		
		Assert.assertEquals(600.02, result, 0);
	}
	
	@Test
	public void testSinFunction() {
		double result = SCalcBuilder.doubleInstance()
				.expression("sin(6)")
				.buildAndCalc();
		
		Assert.assertEquals(-0.27941549819, result, 0.00001);
	}
	
	@Test
	public void testCosFunction() {
		double result = SCalcBuilder.doubleInstance()
				.expression("cos(6)")
				.buildAndCalc();
		
		Assert.assertEquals(0.96017028665, result, 0.00001);
	}
	
	@Test
	public void testTanFunction() {
		double result = SCalcBuilder.doubleInstance()
				.expression("tan(6)")
				.buildAndCalc();
		
		Assert.assertEquals(-0.29100619138, result, 0.00001);
	}
	
	@Test
	public void testLnFunction() {
		double result = SCalcBuilder.doubleInstance()
				.expression("ln(6)")
				.buildAndCalc();
		
		Assert.assertEquals(1.79175946923, result, 0.00001);
	}
	
	@Test
	public void testLogFunction() {
		double result = SCalcBuilder.doubleInstance()
				.expression("log(6)")
				.buildAndCalc();
		
		Assert.assertEquals(0.77815125038, result, 0.00001);
	}
	
	@Test
	public void testAllParamsConstant() {
		List<TestDto> dtos = new ArrayList<>();
		dtos.add(new TestDto(10.0));
		dtos.add(new TestDto(20.0));
		dtos.add(new TestDto(30.0));
		dtos.add(new TestDto(40.0));
		
		double result = SCalcBuilder.doubleInstance()
				.expression("summe_alle = sum(ALL_PARAMS); faktor(x) = param0 * param2 * x; return summe_alle / faktor(3);")
				.debug(true)
				.debugLogger(System.out::println)
				.build()
				.paramsAsCollection(TestDto::getValueToExtract, dtos)
				.calc();
		
		Assert.assertEquals(0.11111111111, result, 0.00001);
	}
	
	@Test
	public void testConstantNameAsFunctionName() {
		double result = SCalcBuilder.doubleInstance()
				.expression("E(x)=E * x; return E(2);")
				.debug(true)
				.buildAndCalc();
		
		Assert.assertEquals(Math.E * 2, result, 0.0001);
	}
	
	@Test
	public void testConstantNameInFunctionName() {
		double result = SCalcBuilder.doubleInstance()
				.expression("funcE(x)=E * x; return funcE(2);")
				.debug(true)
				.buildAndCalc();
		
		Assert.assertEquals(Math.E * 2, result, 0.0001);
	}
	
	@Test
	public void testConstantNameInParamName() {
		double result = SCalcBuilder.doubleInstance()
				.expression("paramE * 2")
				.debug(true)
				.build()
				.parameter("paramE", 2)
				.calc();
		
		Assert.assertEquals(4.0, result, 0);
	}
	
	@Test(expected = CalculationException.class)
	public void testInvalidAssignment() {
		SCalcBuilder.doubleInstance()
				.expression("=2")
				.buildAndCalc();
	}
	
	@Test
	public void testEmptyExpression() {
		double result = SCalcBuilder.doubleInstance()
				.expression("")
				.buildAndCalc();
		
		Assert.assertEquals(0.0, result, 0);
	}
	
	@Test(expected = CalculationException.class)
	public void testUnknownFunction() {
		SCalcBuilder.doubleInstance()
				.expression("abc(2)")
				.buildAndCalc();
	}
	
	@Test
	public void testOnlyNumberInput() {
		double result = SCalcBuilder.doubleInstance()
				.expression("5.5")
				.buildAndCalc();
		
		Assert.assertEquals(5.5, result, 0);
	}
	
	@Test
	public void testSpaces() {
		Assert.assertEquals(4.0, SCalcBuilder.doubleInstance().expression("   12        -  8   ").buildAndCalc(), 0);
		Assert.assertEquals(133.0, SCalcBuilder.doubleInstance().expression("142        -9   ").buildAndCalc(), 0);
	}
	
	@Test
	public void testParenthesis() {
		Assert.assertEquals(5.0, SCalcBuilder.doubleInstance().expression("(((((5)))))").buildAndCalc(), 0);
		Assert.assertEquals(30.0, SCalcBuilder.doubleInstance().expression("(( ((2)) + 4))*((5))").buildAndCalc(), 0);
	}
	
	@Test
	public void testUnbalancedParenthesis() {
		Assert.assertEquals(6.0, SCalcBuilder.doubleInstance().expression("((2)) * ((3").buildAndCalc(), 0);
		Assert.assertEquals(6.0, SCalcBuilder.doubleInstance().expression("((2) * ((3").buildAndCalc(), 0);
		Assert.assertEquals(24.0, SCalcBuilder.doubleInstance().expression("6 * ( 2 + 2").buildAndCalc(), 0);
	}
	
	@Test(expected = CalculationException.class)
	public void testUnbalancedParenthesisError() {
		SCalcBuilder.doubleInstance()
				.expression("6 * ) 2 + 2")
				.buildAndCalc();
	}
	
	@Test
	public void testComplexFormula() {
		int a = 6;
		double b = 4.32;
		short c = (short) 24.15;
		
		double result = SCalcBuilder.doubleInstance()
				.expression("(((9-a/2)*2-b)/2-a-1)/(2+c/(2+4))")
				.calculationScale(16)
				.resultScale(16)
				.build()
				.params("a", a, "b", b, "c", c)
				.calc();
		
		Assert.assertEquals((((9 - a / 2.0) * 2 - b) / 2 - a - 1) / (2 + c / (2.0 + 4.0)), result, 0);
	}
	
	@Test
	public void testRoundingZero() {
		double result = SCalcBuilder.doubleInstance()
				.expression("round(a, 2) + round(b, 3) + round(c, 4)")
				.build()
				.parameter("a", "0.002")
				.parameter("b", "0.0002")
				.parameter("c", "0.00002")
				.calc();
		
		Assert.assertEquals(0.0, result, 0);
	}
	
	@Test
	public void testRounding() {
		BigDecimal result = SCalcBuilder.bigDecimalInstance()
				.expression("round(a, 2) + round(b, 3) + round(c, 5)")
				.resultScale(6)
				.build()
				.parameter("a", "0.002")
				.parameter("b", "0.0002")
				.parameter("c", "0.00002")
				.calc();
		
		Assert.assertEquals("0.000020", result.toPlainString());
	}
	
	@Test
	public void testRounding_CalculationScaleForSumCalculation() {
		BigDecimal result = SCalcBuilder.bigDecimalInstance()
				.expression("round(a, 2) + round(b, 3) + round(c, 5)")
				.resultScale(6)
				.calculationScale(3)
				.build()
				.parameter("a", "0.002")
				.parameter("b", "0.0002")
				.parameter("c", "0.00002")
				.calc();
		
		Assert.assertEquals("0.000000", result.toPlainString());
	}
	
	@Test
	public void testRounding_AtomicInteger() {
		AtomicInteger result = SCalcBuilder.instanceFor(AtomicInteger.class)
				.sumExpression()
				.build()
				.params(new AtomicInteger(5))
				.params(new AtomicInteger(6))
				.calc();
		
		Assert.assertEquals(11, result.get());
	}
	
	@Test
	public void testRounding_AtomicLong() {
		AtomicLong result = SCalcBuilder.instanceFor(AtomicLong.class)
				.sumExpression()
				.build()
				.params(new AtomicLong(-5))
				.params(new AtomicLong(6))
				.calc();
		
		Assert.assertEquals(1L, result.get());
	}
	
	@Test
	public void testConverting_INumber() {
		double result = SCalcBuilder.doubleInstance()
				.subtractExpression()
				.build()
				.params(new Percentage(0.0001))
				.params(new Percentage(0.0006))
				.calc();
		
		Assert.assertEquals(-0.0005, result, 0.0);
	}
	
	@Test(expected = CalculationException.class)
	public void testConverting_INumber_NotAsReturnType() {
		SCalcBuilder.instanceFor(Percentage.class)
				.subtractExpression()
				.build()
				.params(new Percentage(0.0001))
				.params(new Percentage(0.0006))
				.calc();
	}
}