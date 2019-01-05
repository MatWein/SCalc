package scalc.test.model;

public class TestDto {
    private double valueToExtract;

    public TestDto(double valueToExtract) {
        this.valueToExtract = valueToExtract;
    }

    public double getValueToExtract() {
        return valueToExtract;
    }

    public void setValueToExtract(double valueToExtract) {
        this.valueToExtract = valueToExtract;
    }
}
