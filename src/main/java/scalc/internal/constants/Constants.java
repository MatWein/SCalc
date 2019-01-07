package scalc.internal.constants;

import scalc.SCalcOptions;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static Map<String, Number> getPredefinedConstants(SCalcOptions<?> options) {
        Map<String, Number> constants = new HashMap<>();

        BigDecimal pi = new BigDecimal(Math.PI).setScale(options.getCalculationScale(), options.getCalculationRoundingMode());
        constants.put("PI", pi);
        constants.put("π", pi);

        BigDecimal e = new BigDecimal(Math.E).setScale(options.getCalculationScale(), options.getCalculationRoundingMode());
        constants.put("E", e);

        return constants;
    }
}
