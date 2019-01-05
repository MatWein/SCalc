package scalc.internal.functions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Functions {
    public static final Map<String, FunctionImpl> FUNCTIONS = getPredefinedFunctions();
    public static final List<Character> FUNCTION_NAME_VALID_CHARS = Arrays.asList('√', '∑', 'ä', 'Ä', 'ö', 'Ö', 'ü', 'Ü', 'ß', '_');

    private static Map<String, FunctionImpl> getPredefinedFunctions() {
        Map<String, FunctionImpl> functions = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        functions.put("√", RootFunction.INSTANCE);
        functions.put("root", RootFunction.INSTANCE);
        functions.put("wurzel", RootFunction.INSTANCE);

        functions.put("∑", SumFunction.INSTANCE);
        functions.put("sum", SumFunction.INSTANCE);
        functions.put("summe", SumFunction.INSTANCE);

        functions.put("avg", AvgFunction.INSTANCE);
        functions.put("durchschnitt", AvgFunction.INSTANCE);

        functions.put("max", MaxFunction.INSTANCE);
        functions.put("min", MinFunction.INSTANCE);
        functions.put("abs", AbsFunction.INSTANCE);

        return functions;
    }

    public static boolean calculateIsValidFunctionChar(char c) {
        return (c >= 'A' && c <= 'Z')
                || (c >= 'a' && c <= 'z')
                || FUNCTION_NAME_VALID_CHARS.contains(c);
    }
}
