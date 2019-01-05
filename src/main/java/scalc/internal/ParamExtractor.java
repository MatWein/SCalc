package scalc.internal;

import scalc.internal.converter.INumberConverter;
import scalc.internal.converter.ToNumberConverter;
import scalc.internal.expr.Expression;
import scalc.internal.expr.ISubExpressions;
import scalc.internal.expr.Variable;

import java.util.*;

public class ParamExtractor {
    public static Map<String, Number> calculateParams(
            Expression expression,
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
                result.putAll(extractParamsInOrder(converters, expression, paramsAsArray));
            }
        }

        if (removeNullParameters) {
            while (result.values().remove(null)) {}
        }

        return result;
    }

    public static Map<String, Number> extractParamsInOrder(Map<Class<?>, INumberConverter> converters, Expression expression, Object[] params) {
        Map<String, Number> result = new LinkedHashMap<>();

        Queue<Object> paramsAsQueue = new LinkedList<>(Arrays.asList(params));
        iterateExpression(converters, result, expression, paramsAsQueue);

        for (Object remainingParam : paramsAsQueue) {
            result.put(UUID.randomUUID().toString().replace("-", ""), ToNumberConverter.toNumber(remainingParam, converters));
        }

        return result;
    }

    private static void iterateExpression(
            Map<Class<?>, INumberConverter> converters,
            Map<String, Number> result,
            Expression expression,
            Queue<Object> paramsAsQueue) {

        if (expression instanceof Variable) {
            Object polledNumber = paramsAsQueue.poll();
            result.put(((Variable) expression).getName(), ToNumberConverter.toNumber(polledNumber, converters));
        } else if (expression instanceof ISubExpressions) {
            List<? extends Expression> expressions = ((ISubExpressions) expression).getExpressions();
            for (Expression subExpression : expressions) {
                iterateExpression(converters, result, subExpression, paramsAsQueue);
            }
        }
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
