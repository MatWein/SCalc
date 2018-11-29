package scalc;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class SCalcTest {
    @Test
    public void calc_ComplexExpression_1() {
        Map<String, Number> params = new HashMap<String, Number>();
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
        Map<String, Number> params = new HashMap<String, Number>();
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
        Map<String, Number> params = new HashMap<String, Number>();
        params.put("someFancyVar1", 10);

        Double result = SCalc.doubleInstance()
                .expression("  someFancyVar1  ")
                .params(params)
                .calc();

        Assert.assertEquals(10.0, result, 0);
    }

    @Test
    public void calc_Function() {
        Map<String, Number> params = new HashMap<String, Number>();
        params.put("var1", 4);

        Double result = SCalc.doubleInstance()
                .expression("Wurzel(var1)")
                .params(params)
                .calc();

        Assert.assertEquals(2.0, result, 0);
    }
}