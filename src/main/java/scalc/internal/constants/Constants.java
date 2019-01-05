package scalc.internal.constants;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static Map<String, Number> getPredefinedConstants(MathContext mathContext) {
        Map<String, Number> constants = new HashMap<>();

        BigDecimal pi = new BigDecimal(Math.PI, mathContext);
        constants.put("PI", pi);
        constants.put("π", pi);

        BigDecimal e = new BigDecimal(Math.E, mathContext);
        constants.put("E", e);

        return constants;
    }
}
