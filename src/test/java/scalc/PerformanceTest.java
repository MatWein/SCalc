package scalc;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PerformanceTest {
    private SCalc<Long> sCalc;

    @Before
    public void setUp() {
        sCalc = SCalcBuilder.instanceFor(Long.class)
                .expression("f(a, b)=√(a² - (b² / 2)); return f(a, b);")
                .resultScale(64)
                .calculationScale(64)
                .build();
    }

    @Test
    public void testPerformance() {
        long start = System.currentTimeMillis();

        for (long i = 0; i < 100000; i++) {
            Long result = sCalc
                .parameter("a", i)
                .parameter("b", i)
                .calc();
            System.out.println(String.format("%s >> %s", i, result));

            long pow = i * i;
            Assert.assertEquals((long)(Math.sqrt(pow - (pow / 2.0))), result, 0);
        }

        long end = System.currentTimeMillis();
        long timeNeeded = end - start;
        System.out.println(String.format("Time needed for calculation: %sms", timeNeeded));
    }
}
