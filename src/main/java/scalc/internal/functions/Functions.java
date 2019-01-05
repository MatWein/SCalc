package scalc.internal.functions;

import java.util.Map;
import java.util.TreeMap;

public class Functions {
    public static final Map<String, FunctionImpl> FUNCTIONS = getPredefinedFunctions();

    private static Map<String, FunctionImpl> getPredefinedFunctions() {
        Map<String, FunctionImpl> functions = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        functions.put("√", RootFunction.INSTANCE);
        functions.put("wurzel", RootFunction.INSTANCE);
        functions.put("sqrt", RootFunction.INSTANCE);

        return functions;
    }
}
