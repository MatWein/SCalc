package scalc.internal;

import scalc.SCalc;
import scalc.internal.converter.ToNumberConverter;
import scalc.internal.expr.Expression;
import scalc.internal.expr.ISubExpressions;
import scalc.internal.expr.Variable;

import java.util.*;

public class ParamExtractor {
    public static Map<String, Number> extractParamsInOrder(SCalc<?> sCalc, Expression expression, Object... params) {
        Map<String, Number> result = new HashMap<String, Number>();

        Queue<Object> paramsAsQueue = new LinkedList<Object>(Arrays.asList(params));
        iterateExpression(sCalc, result, expression, paramsAsQueue);

        for (Object remainingParam : paramsAsQueue) {
            result.put(UUID.randomUUID().toString().replace("-", ""), ToNumberConverter.toNumber(remainingParam, sCalc));
        }

        return result;
    }

    private static void iterateExpression(
            SCalc<?> sCalc,
            Map<String, Number> result,
            Expression expression,
            Queue<Object> paramsAsQueue) {

        if (expression instanceof Variable) {
            Object polledNumber = paramsAsQueue.poll();
            result.put(((Variable) expression).getName(), ToNumberConverter.toNumber(polledNumber, sCalc));
        } else if (expression instanceof ISubExpressions) {
            List<Expression> expressions = ((ISubExpressions) expression).getExpressions();
            for (Expression subExpression : expressions) {
                iterateExpression(sCalc, result, subExpression, paramsAsQueue);
            }
        }
    }

    public static Map<String, Number> extractParamsFromNameValuePairs(SCalc<?> sCalc, Object... params) {
        if (params == null || params.length == 0) {
            return new HashMap<String, Number>(0);
        }

        if (params.length % 2 != 0) {
            throw new IllegalArgumentException("Params have to be in form: name,value,name2,value2,...");
        }

        Map<String, Number> result = new HashMap<String, Number>(params.length / 2);

        for (int i = 0; i < params.length; i += 2) {
            Object param1 = params[i];
            if (!(param1 instanceof String)) {
                throw new IllegalArgumentException(String.format("Invalid param value: '%s'. Has to be a string.", param1));
            }

            Object param2 = params[i + 1];

            String name = (String) param1;
            Number value = ToNumberConverter.toNumber(param2, sCalc);

            result.put(name, value);
        }

        return result;
    }

    public static Map<String, Number> extractParamsFromMap(SCalc<?> sCalc, Map<String, Object> params) {
        Map<String, Number> result = new HashMap<String, Number>();

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            result.put(entry.getKey(), ToNumberConverter.toNumber(entry.getValue(), sCalc));
        }

        return result;
    }

    public static void postProcessParams(SCalc<?> sCalc) {
        if (sCalc.isRemoveNullParameters()) {
            while (sCalc.getParams().values().remove(null)) {}
        }
    }
}
