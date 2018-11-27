package scalc.internal;

import scalc.internal.expr.Expression;
import scalc.internal.expr.ISubExpressions;
import scalc.internal.expr.Variable;

import java.util.*;

public class ParamExtractor {
    public static Map<String, Number> extractParamsInOrder(Expression expression, Number... params) {
        Map<String, Number> result = new HashMap<String, Number>();

        Queue<Number> paramsAsQueue = new LinkedList<Number>(Arrays.asList(params));
        iterateExpression(result, expression, paramsAsQueue);

        return result;
    }

    private static void iterateExpression(
            Map<String, Number> result,
            Expression expression,
            Queue<Number> paramsAsQueue) {

        if (expression instanceof Variable) {
            Number polledNumber = paramsAsQueue.poll();
            result.put(((Variable) expression).getName(), polledNumber);
        } else if (expression instanceof ISubExpressions) {
            List<Expression> expressions = ((ISubExpressions) expression).getExpressions();
            for (Expression subExpression : expressions) {
                iterateExpression(result, subExpression, paramsAsQueue);
            }
        }
    }

    public static Map<String, Number> extractParamsFromNameValuePairs(Object... params) {
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
            if (!(param2 instanceof Number)) {
                throw new IllegalArgumentException(String.format("Invalid param value: '%s'. Has to be a number.", param2));
            }

            String name = (String) param1;
            Number value = (Number) param2;

            result.put(name, value);
        }

        return result;
    }
}
