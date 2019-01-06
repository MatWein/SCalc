package scalc.internal.calc;

import scalc.SCalc;
import scalc.SCalcOptions;
import scalc.exceptions.CalculationException;
import scalc.internal.functions.FunctionImpl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static scalc.internal.calc.ExpressionResolver.resolveExpression;

public class DefinitionExpressionController {
    public static BigDecimal parseDefinitionExpression(SCalc<?> sCalc) {
        SCalcOptions<?> options = sCalc.getOptions();
        Map<String, Number> customParams = new LinkedHashMap<>(sCalc.getParams());
        Map<String, FunctionImpl> customFunctions = new HashMap<>();

        String[] definitions = options.getExpression().split(";");
        for (String definition : definitions) {
            if (definition.trim().startsWith("return")) {
                return calculateReturnStatement(options, customParams, customFunctions, definition);
            } else {
                assignVariableOrFunction(options, customParams, customFunctions, definition);
            }
        }

        throw new CalculationException("No 'return ...' statement was found on the given expression.");
    }

    private static BigDecimal calculateReturnStatement(
            SCalcOptions<?> options,
            Map<String, Number> customParams,
            Map<String, FunctionImpl> customFunctions,
            String definition) {

        String expression = definition.trim().substring("return".length());
        String resolvedExpression = resolveExpression(options.getCalculationMathContext(), expression, customParams);
        SCalcExecutor executor = new SCalcExecutor(resolvedExpression, options.getCalculationMathContext(), customFunctions);
        return executor.parse();
    }

    private static void assignVariableOrFunction(
            SCalcOptions<?> options,
            Map<String, Number> customParams,
            Map<String, FunctionImpl> customFunctions,
            String definition) {

        String[] assignment = definition.split("=");
        if (assignment.length != 2) {
            throw new CalculationException("Definitions cannot have more than 2 parts.");
        } else {
            String variableOrFunctionName = assignment[0].trim();
            String expression = assignment[1].trim();

            boolean isFunction = variableOrFunctionName.contains("(") && variableOrFunctionName.contains(")");
            boolean isVariable = !isFunction;

            if (isVariable) {
                assignVariable(options, customParams, customFunctions, variableOrFunctionName, expression);
            } else {
                assignFunction(options, customParams, customFunctions, variableOrFunctionName, expression);
            }
        }
    }

    private static void assignFunction(
            final SCalcOptions<?> options,
            final Map<String, Number> customParams,
            final Map<String, FunctionImpl> customFunctions,
            final String variableOrFunctionName,
            final String expression) {

        Matcher matcher = Pattern.compile("(.*?)\\((.*?)\\)").matcher(variableOrFunctionName);
        if (!matcher.find()) {
            throw new CalculationException("Function definition invalid: " + variableOrFunctionName);
        }

        final String name = matcher.group(1);
        final String[] parameterNames = matcher.group(2).split(",");

        customFunctions.put(name, new FunctionImpl() {
            @Override
            public BigDecimal call(MathContext mathContext, List<BigDecimal> functionParams) {
                Map<String, Number> functionParamsAsMap = new LinkedHashMap<>(customParams);
                for (int i = 0; i < functionParams.size(); i++) {
                    String paramName = parameterNames[i].trim();
                    BigDecimal paramValue = functionParams.get(i);

                    functionParamsAsMap.put(paramName, paramValue);
                }

                String resolvedExpression = resolveExpression(mathContext, expression, functionParamsAsMap);
                SCalcExecutor executor = new SCalcExecutor(resolvedExpression, options.getCalculationMathContext(), customFunctions);
                return executor.parse();
            }
        });
    }

    private static void assignVariable(
            SCalcOptions<?> options,
            Map<String, Number> customParams,
            Map<String, FunctionImpl> customFunctions,
            String variableOrFunctionName,
            String expression) {

        String resolvedExpression = resolveExpression(options.getCalculationMathContext(), expression, customParams);
        SCalcExecutor executor = new SCalcExecutor(resolvedExpression, options.getCalculationMathContext(), customFunctions);
        BigDecimal result = executor.parse();

        customParams.put(variableOrFunctionName, result);
    }
}
