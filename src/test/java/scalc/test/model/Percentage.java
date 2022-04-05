package scalc.test.model;

import scalc.interfaces.INumber;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Percentage implements INumber {
	private final BigDecimal value;
	
	public Percentage() {
		this(0.0);
	}
	
	public Percentage(double value) {
		this(BigDecimal.valueOf(value));
	}
	
	public Percentage(BigDecimal value) {
		this.value = value.setScale(4, RoundingMode.HALF_UP);
	}
	
	@Override
	public BigDecimal toBigDecimal() {
		return value;
	}
	
	public BigDecimal getValue() {
		return value;
	}
}
