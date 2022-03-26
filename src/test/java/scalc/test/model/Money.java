package scalc.test.model;

public class Money {
    private double value;
	
	public Money() {
	}
	
	public Money(double value) {
		this.value = value;
	}
	
	public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
