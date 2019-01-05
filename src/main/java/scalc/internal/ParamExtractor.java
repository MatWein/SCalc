package scalc.internal;

import scalc.internal.converter.INumberConverter;
import scalc.internal.converter.ToNumberConverter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class ParamExtractor {
    public static Map<String, Number> calculateParams(
            Map<Class<?>, INumberConverter> converters,
            Map<String, Object> paramsAsMap,
            Object[] paramsAsArray,
            boolean removeNullParameters) {

        Map<String, Number> result = new LinkedHashMap<>();

        if (paramsAsMap != null) {
            for (Map.Entry<String, Object> entry : paramsAsMap.entrySet()) {
                result.put(entry.getKey(), ToNumberConverter.toNumber(entry.getValue(), converters));
            }
        }

        if (paramsAsArray != null && paramsAsArray.length > 0) {
            if (paramsAsArray[0] instanceof CharSequence) {
                result.putAll(extractParamsFromNameValuePairs(converters, paramsAsArray));
            } else {
                result.putAll(extractParamsWithRandomName(converters, paramsAsArray));
            }
        }

        if (removeNullParameters) {
            //noinspection StatementWithEmptyBody
            while (result.values().remove(null)) {}
        }

        return result;
    }

    private static Map<String, Number> extractParamsWithRandomName(Map<Class<?>, INumberConverter> converters, Object[] paramsAsArray) {
        Map<String, Number> result = new HashMap<>();

        for (Object param : paramsAsArray) {
            Number value = ToNumberConverter.toNumber(param, converters);
            result.put(UUID.randomUUID().toString(), value);
        }

        return result;
    }

    public static Map<String, Number> extractParamsFromNameValuePairs(Map<Class<?>, INumberConverter> converters, Object[] params) {
        if (params.length % 2 != 0) {
            throw new IllegalArgumentException("Params have to be in form: name,value,name2,value2,...");
        }

        Map<String, Number> result = new HashMap<>(params.length / 2);

        for (int i = 0; i < params.length; i += 2) {
            Object param1 = params[i];
            if (!(param1 instanceof String)) {
                throw new IllegalArgumentException(String.format("Invalid param value: '%s'. Has to be a string.", param1));
            }

            Object param2 = params[i + 1];

            String name = (String) param1;
            Number value = ToNumberConverter.toNumber(param2, converters);

            result.put(name, value);
        }

        return result;
    }
}
