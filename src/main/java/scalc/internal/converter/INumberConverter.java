package scalc.internal.converter;

import java.math.BigDecimal;

public interface INumberConverter<T> {
    BigDecimal toBigDecimal(T input);
    T fromBigDecimal(BigDecimal input);
}
