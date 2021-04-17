package scalc.internal;

import scalc.exceptions.CalculationException;
import scalc.internal.converter.INumberConverter;
import scalc.internal.converter.ToNumberConverter;

import java.util.LinkedHashMap;
import java.util.Map;

public class ParamExtractor {
    private static final String DEFAULT_PARAM_NAME = "param";
    
    public static Map<String, Number> extractParamsWithRandomName(Map<Class<?>, INumberConverter> converters, Object[] paramsAsArray, int startNumber) {
        Map<String, Number> result = new LinkedHashMap<>();
        int counter = startNumber;
        
        for (Object param : paramsAsArray) {
            Number value = ToNumberConverter.toNumber(param, converters);
            result.put(DEFAULT_PARAM_NAME + counter++, value);
        }

        return result;
    }

    public static Map<String, Number> extractParamsFromNameValuePairs(Map<Class<?>, INumberConverter> converters, Object[] params) {
        if (params.length % 2 != 0) {
            throw new CalculationException("Params have to be in form: name,value,name2,value2,...");
        }

        Map<String, Number> result = new LinkedHashMap<>(params.length / 2);

        for (int i = 0; i < params.length; i += 2) {
            Object param1 = params[i];
            if (!(param1 instanceof String)) {
                throw new CalculationException(String.format("Invalid param value: '%s'. Has to be a string.", param1));
            }

            Object param2 = params[i + 1];

            String name = (String) param1;
            Number value = ToNumberConverter.toNumber(param2, converters);

            result.put(name, value);
        }

        return result;
    }
}
