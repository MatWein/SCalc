package scalc.internal;

import scalc.exceptions.CalculationException;
import scalc.interfaces.INumberConverter;
import scalc.internal.converter.ToNumberConverter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class ParamExtractor {
    private static final String DEFAULT_PARAM_NAME = "param";
    
    public static <T> Map<String, Number> extractParams(
            Function<T, Object> paramExtractor,
            Map<Class<?>, INumberConverter> converters,
            T[] paramsAsArray,
            int startNumber) {
        
        if (paramsAsArray != null && paramsAsArray.length > 0) {
            if (paramsAsArray[0] instanceof CharSequence) {
                return ParamExtractor.extractParamsFromNameValuePairs(paramExtractor, converters, paramsAsArray);
            } else {
                return ParamExtractor.extractParamsWithDefaultName(paramExtractor, converters, paramsAsArray, startNumber);
            }
        }
        
        return new HashMap<>();
    }
    
    static <T> Map<String, Number> extractParamsWithDefaultName(
            Function<T, Object> paramExtractor,
            Map<Class<?>, INumberConverter> converters,
            T[] params,
            int startNumber) {
        
        Map<String, Number> result = new LinkedHashMap<>();
        int counter = startNumber;
        
        for (T param : params) {
            Object extractedParam = param == null ? null : paramExtractor.apply(param);
            Number value = ToNumberConverter.toNumber(extractedParam, converters);
            result.put(DEFAULT_PARAM_NAME + counter++, value);
        }

        return result;
    }
    
    static <T> Map<String, Number> extractParamsFromNameValuePairs(
            Function<T, Object> paramExtractor,
            Map<Class<?>, INumberConverter> converters,
            T[] params) {
        
        if (params.length % 2 != 0) {
            throw new CalculationException("Params have to be in form: name,value,name2,value2,...");
        }

        Map<String, Number> result = new LinkedHashMap<>(params.length / 2);

        for (int i = 0; i < params.length; i += 2) {
            Object param1 = params[i];
            if (!(param1 instanceof String)) {
                throw new CalculationException(String.format("Invalid param value: '%s'. Has to be a string.", param1));
            }

            T param2 = params[i + 1];
            Object extractedParam2 = param2 == null ? null : paramExtractor.apply(param2);
    
            String name = (String) param1;
            Number value = ToNumberConverter.toNumber(extractedParam2, converters);

            result.put(name, value);
        }

        return result;
    }
}
