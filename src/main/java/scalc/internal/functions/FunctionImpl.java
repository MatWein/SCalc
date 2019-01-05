package scalc.internal.functions;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

public interface FunctionImpl {
    BigDecimal call(MathContext mathContext, List<BigDecimal> functionParams);
}
