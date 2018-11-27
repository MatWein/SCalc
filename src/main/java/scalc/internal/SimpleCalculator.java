package scalc.internal;

import scalc.SCalc;
import scalc.internal.expr.*;
import scalc.internal.functions.FunctionImpl;
import scalc.internal.functions.PowFunction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleCalculator {
    private static final Map<String, FunctionImpl> FUNCTIONS = getPredefinedFunctions();

    private static Map<String, FunctionImpl> getPredefinedFunctions() {
        Map<String, FunctionImpl> functions = new HashMap<String, FunctionImpl>();

        PowFunction powFunction = new PowFunction();
        functions.put("√", powFunction);
        functions.put("wurzel", powFunction);
        functions.put("pow", powFunction);
        functions.put("sqrt", powFunction);
        
        return functions;
    }

    public static <RETURN_TYPE extends Number> RETURN_TYPE calc(SCalc<RETURN_TYPE> calculatorParams) {
        Expression expression = calculatorParams.getExpression();
        Map<String, Number> params = calculatorParams.getParams();

        BigDecimal resolvedValue = resolveExpression(expression, params);

        if (calculatorParams.getScale() != null && calculatorParams.getRoundingMode() != null) {
            resolvedValue = resolvedValue.setScale(calculatorParams.getScale(), calculatorParams.getRoundingMode());
        }

        return NumberTypeConverter.convert(resolvedValue, calculatorParams.getReturnType());
    }

    private static BigDecimal resolveExpression(Expression expression, Map<String, Number> params) {
        BigDecimal rawValue = resolveRawExpression(expression, params);

        if (expression instanceof INegatable) {
            if (!((INegatable) expression).isPositive()) {
                return rawValue.multiply(new BigDecimal(-1));
            }
        }

        return rawValue;
    }

    private static BigDecimal resolveRawExpression(Expression expression, Map<String, Number> params) {
        if (expression instanceof Constant) {
            return NumberTypeConverter.convert(((Constant) expression).getValue(), BigDecimal.class);
        } else if (expression instanceof Variable) {
            return NumberTypeConverter.convert(params.get(((Variable) expression).getName()), BigDecimal.class);
        } else if (expression instanceof Function) {
            Function expressionAsFunction = (Function) expression;
            String functionName = expressionAsFunction.getName();

            List<BigDecimal> functionParams = new ArrayList<BigDecimal>();
            List<Expression> expressions = expressionAsFunction.getExpressions();
            for (Expression subExpression : expressions) {
                BigDecimal value = resolveExpression(subExpression, params);
                functionParams.add(value);
            }

            BigDecimal functionValue = callFunction(functionName, functionParams);
            return NumberTypeConverter.convert(functionValue, BigDecimal.class);
        } else if (expression instanceof ComplexExpression) {

        }

        throw new IllegalArgumentException(String.format("Cannot calculate value for expression: %s", expression));
    }

    private static BigDecimal callFunction(String name, List<BigDecimal> functionParams) {
        FunctionImpl function = FUNCTIONS.get(name);
        if (function == null) {
            throw new IllegalArgumentException(String.format("Function with name '%s' not defined.", name));
        }

        return function.call(functionParams);
    }
}
