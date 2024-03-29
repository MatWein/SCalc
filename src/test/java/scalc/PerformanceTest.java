package scalc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PerformanceTest {
	private static final int ITERATIONS = 100000;
	
	private SCalc<Long> sCalc;

    @BeforeEach
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
        long result = 0L;

        for (long i = 0; i < ITERATIONS; i++) {
            result = sCalc
                .parameter("a", i)
                .parameter("b", i)
                .calc();
        }

        long end = System.currentTimeMillis();
        long timeNeeded = end - start;
        System.out.printf("Time needed for calculation: %sms%n", timeNeeded);
	
	    long i = ITERATIONS - 1;
	    long pow = i * i;
	    Assertions.assertEquals((long)(Math.sqrt(pow - (pow / 2.0))), result, 0);
    }
}
