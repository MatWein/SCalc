package scalc.internal.converter;

import org.junit.Assert;
import org.junit.Test;
import scalc.exceptions.CalculationException;

import java.math.BigDecimal;

public class NumberTypeConverterTest {
    @Test
    public void convert_SameInstance() {
        Double number = 10.2;

        Double result = NumberTypeConverter.convert(number, Double.class);

        Assert.assertSame(number, result);
    }

    @Test
    public void convert_Null() {
        Double result = NumberTypeConverter.convert(null, Double.class);

        Assert.assertEquals(0.0, result, 0);
    }

    @Test
    public void convert() {
        BigDecimal result = NumberTypeConverter.convert(12.99, BigDecimal.class);

        Assert.assertEquals(new BigDecimal("12.99"), result);
    }

    @Test
    public void convert_Integer() {
        Integer result = NumberTypeConverter.convert(12L, Integer.class);

        Assert.assertEquals(12, result, 0);
    }

    @Test
    public void convert_Long() {
        Long result = NumberTypeConverter.convert(120.1, Long.class);

        Assert.assertEquals(120, result, 0);
    }

    @Test(expected = CalculationException.class)
    public void convert_UnsupportedType() {
        NumberTypeConverter.convert(120.1, Byte.class);
    }
}