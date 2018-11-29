package scalc.test.model;

import scalc.internal.converter.INumberConverter;

import java.math.BigDecimal;

public class MoneyConverter implements INumberConverter<Money> {
    public BigDecimal toBigDecimal(Money input) {
        if (input == null) {
            return null;
        }

        return new BigDecimal(input.getValue());
    }

    public Money fromBigDecimal(BigDecimal input) {
        Money money = new Money();
        money.setValue(input.doubleValue());
        return money;
    }
}
