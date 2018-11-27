package scalc.internal.functions;

import java.math.BigDecimal;
import java.util.List;

public interface FunctionImpl {
    BigDecimal call(List<BigDecimal> functionParams);
}
