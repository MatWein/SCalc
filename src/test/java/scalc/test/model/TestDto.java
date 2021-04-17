package scalc.test.model;

public class TestDto {
    private Double valueToExtract;

    public TestDto(Double valueToExtract) {
        this.valueToExtract = valueToExtract;
    }

    public Double getValueToExtract() {
        return valueToExtract;
    }

    public void setValueToExtract(Double valueToExtract) {
        this.valueToExtract = valueToExtract;
    }
}
