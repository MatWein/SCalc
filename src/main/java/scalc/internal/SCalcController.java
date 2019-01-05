package scalc.internal;

import scalc.SCalc;
import scalc.SCalcOptions;
import scalc.internal.converter.NumberTypeConverter;
import scalc.internal.converter.ToNumberConverter;
import scalc.internal.functions.FunctionImpl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SCalcController {
    public static <RETURN_TYPE> RETURN_TYPE calc(SCalc<RETURN_TYPE> sCalc) {
        SCalcOptions<RETURN_TYPE> options = sCalc.getOptions();
        BigDecimal resolvedValue = calculateResult(options);
        resolvedValue = resolvedValue.setScale(options.getResultScale(), options.getResultRoundingMode());

        return ToNumberConverter.toResultType(resolvedValue, options.getReturnType(), options.getConverters());
    }

    private static <RETURN_TYPE> BigDecimal calculateResult(SCalcOptions<RETURN_TYPE> options) {
        String expression = options.getExpression();
        if (expression.equals("+") || expression.equals("-") || expression.equals("*") || expression.equals("/") || expression.equals("^")) {
            return parseSingleOperatorExpression(expression, options.getCalculationMathContext(), options.getParams());
        }

        if (expression.contains(";")) {
            return parseDefinition(options);
        }

        String resolvedExpression = resolveExpression(expression, options.getParams());
        SCalcExecutor executor = new SCalcExecutor(resolvedExpression, options.getCalculationMathContext());
        return executor.parse();
    }

    private static BigDecimal parseSingleOperatorExpression(String expression, MathContext mathContext, Map<String, Number> params) {
        if (params == null || params.isEmpty()) {
            return new BigDecimal(0, mathContext);
        }

        List<Number> paramsInOrder = new ArrayList<>(params.values());

        BigDecimal result = NumberTypeConverter.convert(paramsInOrder.get(0), BigDecimal.class);
        for (int i = 1; i < paramsInOrder.size(); i++) {
            BigDecimal value = NumberTypeConverter.convert(paramsInOrder.get(i), BigDecimal.class);

            switch (expression) {
                case "+": result = result.add(value); break;
                case "-": result = result.subtract(value); break;
                case "*": result = result.multiply(value); break;
                case "/": result = result.divide(value, mathContext); break;
                case "^": result = SCalcExecutor.calulatePow(result, value, mathContext); break;
                default: throw new RuntimeException("Expression invalid.");
            }
        }

        return result;
    }

    private static BigDecimal parseDefinition(final SCalcOptions<?> options) {
        final Map<String, Number> customParams = new LinkedHashMap<>(options.getParams());
        final Map<String, FunctionImpl> customFunctions = new HashMap<>();

        String[] definitions = options.getExpression().split(";");
        for (String definition : definitions) {
            if (definition.startsWith("return")) {
                String expression = definition.substring("return".length());
                String resolvedExpression = resolveExpression(expression, customParams);
                SCalcExecutor executor = new SCalcExecutor(resolvedExpression, options.getCalculationMathContext(), customFunctions);
                return executor.parse();
            } else {
                String[] assignment = definition.split("=");
                if (assignment.length != 2) {
                    throw new RuntimeException("Definitions cannot have more than 2 parts.");
                } else {
                    final String variableOrFunctionName = assignment[0].trim();
                    final String expression = assignment[1].trim();

                    boolean isFunction = variableOrFunctionName.contains("(") && variableOrFunctionName.contains(")");
                    boolean isVariable = !isFunction;

                    if (isVariable) {
                        String resolvedExpression = resolveExpression(expression, customParams);
                        SCalcExecutor executor = new SCalcExecutor(resolvedExpression, options.getCalculationMathContext(), customFunctions);
                        BigDecimal result = executor.parse();

                        customParams.put(variableOrFunctionName, result);
                    } else {
                        Matcher matcher = Pattern.compile("(.*?)\\((.*?)\\)").matcher(variableOrFunctionName);
                        if (!matcher.find()) {
                            throw new RuntimeException("Function definition invalid: " + variableOrFunctionName);
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

                                String resolvedExpression = resolveExpression(expression, functionParamsAsMap);
                                SCalcExecutor executor = new SCalcExecutor(resolvedExpression, options.getCalculationMathContext(), customFunctions);
                                return executor.parse();
                            }
                        });
                    }
                }
            }
        }

        throw new RuntimeException("No 'return ...' statement was found on the given expression.");
    }

    private static String resolveExpression(String expression, Map<String, Number> params) {
        for (Entry<String, Number> param : params.entrySet()) {
            String number = param.getValue() == null ? "0" : NumberTypeConverter.convert(param.getValue(), BigDecimal.class).toString();
            expression = expression.replaceAll("\\b" + param.getKey() + "\\b", number);
        }

        return expression;
    }
}
