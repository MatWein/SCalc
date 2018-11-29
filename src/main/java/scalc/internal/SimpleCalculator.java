package scalc.internal;

import scalc.SCalc;
import scalc.internal.converter.NumberTypeConverter;
import scalc.internal.converter.ToNumberConverter;
import scalc.internal.expr.*;
import scalc.internal.functions.FunctionImpl;
import scalc.internal.functions.PowFunction;
import scalc.internal.functions.SqrtFunction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SimpleCalculator {
    private static final Map<String, FunctionImpl> FUNCTIONS = getPredefinedFunctions();

    private static Map<String, FunctionImpl> getPredefinedFunctions() {
        Map<String, FunctionImpl> functions = new TreeMap<String, FunctionImpl>(String.CASE_INSENSITIVE_ORDER);

        functions.put("√", PowFunction.INSTANCE);
        functions.put("wurzel", PowFunction.INSTANCE);
        functions.put("pow", PowFunction.INSTANCE);
        functions.put("sqrt", SqrtFunction.INSTANCE);
        
        return functions;
    }

    public static <RETURN_TYPE> RETURN_TYPE calc(SCalc<RETURN_TYPE> sCalc) {
        Expression expression = sCalc.getExpression();

        BigDecimal resolvedValue = resolveExpression(expression, sCalc);
        resolvedValue = resolvedValue.setScale(sCalc.getResultScale(), sCalc.getResultRoundingMode());

        return ToNumberConverter.toResultType(resolvedValue, sCalc);
    }

    private static BigDecimal resolveExpression(Expression expression, SCalc<?> sCalc) {
        BigDecimal rawValue = resolveRawExpression(expression, sCalc);

        if (expression instanceof INegatable) {
            if (!((INegatable) expression).isPositive()) {
                return rawValue.multiply(new BigDecimal(-1));
            }
        }

        return rawValue;
    }

    private static BigDecimal resolveRawExpression(Expression expression, SCalc<?> sCalc) {
        Map<String, Number> params = sCalc.getParams();

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
                BigDecimal value = resolveExpression(subExpression, sCalc);
                functionParams.add(value);
            }

            return callFunction(functionName, functionParams);
        } else if (expression instanceof ComplexExpression) {
            List<Expression> subExpressions = ((ComplexExpression) expression).getExpressions();

            processOperator(sCalc, subExpressions, Operator.MULTIPLICATION);
            processOperator(sCalc, subExpressions, Operator.DIVISION);
            processOperator(sCalc, subExpressions, Operator.SUBTRACTION);
            processOperator(sCalc, subExpressions, Operator.ADDITION);

            return (BigDecimal)(((Constant)subExpressions.get(0)).getValue());
        }

        throw new IllegalArgumentException(String.format("Cannot calculate value for expression: %s", expression));
    }

    private static void processOperator(SCalc<?> sCalc, List<Expression> subExpressions, Operator operator) {
        int operatorIndex;
        while ((operatorIndex = subExpressions.indexOf(operator)) != -1) {
            int firstOperandIndex = operatorIndex - 1;
            int secondOperandIndex = operatorIndex + 1;

            Expression expressionBefore = subExpressions.get(firstOperandIndex);
            Expression expressionAfter = subExpressions.get(secondOperandIndex);

            BigDecimal valueBefore = resolveExpression(expressionBefore, sCalc);
            BigDecimal valueAfter = resolveExpression(expressionAfter, sCalc);

            BigDecimal result = operator.getFunction().calc(sCalc, valueBefore, valueAfter);
            subExpressions.remove(firstOperandIndex);
            subExpressions.remove(firstOperandIndex);
            subExpressions.remove(firstOperandIndex);
            subExpressions.add(firstOperandIndex, new Constant(true, result));
        }
    }

    private static BigDecimal callFunction(String name, List<BigDecimal> functionParams) {
        FunctionImpl function = FUNCTIONS.get(name);
        if (function == null) {
            throw new IllegalArgumentException(String.format("Function with name '%s' not defined.", name));
        }

        return function.call(functionParams);
    }
}
