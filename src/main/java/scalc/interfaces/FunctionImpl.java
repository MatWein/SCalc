package scalc.interfaces;

import scalc.SCalcOptions;

import java.math.BigDecimal;
import java.util.List;

public interface FunctionImpl {
    BigDecimal call(SCalcOptions<?> options, List<BigDecimal> functionParams);
}
