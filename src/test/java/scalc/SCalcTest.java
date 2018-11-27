package scalc;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SCalcTest {
    @Test
    public void calc() {
        Map<String, Number> params = new HashMap<String, Number>();
        params.put("a", 10);
        params.put("b", 2.1);

        Double result = SCalc.doubleInstance()
                .expression("a + b")
                .params(params)
                .calc();

        Assert.assertEquals(12.1, result, 0);
    }
}