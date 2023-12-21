package scalc.internal.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import scalc.exceptions.CalculationException;

import java.math.BigDecimal;

public class NumberTypeConverterTest {
    @Test
    public void convert_SameInstance() {
        Double number = 10.2;

        Double result = NumberTypeConverter.convert(number, Double.class);

        Assertions.assertSame(number, result);
    }

    @Test
    public void convert_Null() {
        Double result = NumberTypeConverter.convert(null, Double.class);

        Assertions.assertEquals(0.0, result, 0);
    }

    @Test
    public void convert() {
        BigDecimal result = NumberTypeConverter.convert(12.99, BigDecimal.class);

        Assertions.assertEquals(new BigDecimal("12.99"), result);
    }

    @Test
    public void convert_Integer() {
        Integer result = NumberTypeConverter.convert(12L, Integer.class);

        Assertions.assertEquals(12, result, 0);
    }

    @Test
    public void convert_Long() {
        Long result = NumberTypeConverter.convert(120.1, Long.class);

        Assertions.assertEquals(120, result, 0);
    }

    @Test
    public void convert_UnsupportedType() {
        Assertions.assertThrows(CalculationException.class, () -> NumberTypeConverter.convert(120.1, Byte.class));
    }
}