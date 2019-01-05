package scalc.internal;

import scalc.SCalc;
import scalc.SCalcOptions;
import scalc.exceptions.CalculationException;
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

        String resolvedExpression = resolveExpression(options.getCalculationMathContext(), expression, options.getParams());
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
                default: throw new CalculationException("Expression invalid.");
            }
        }

        return result;
    }

    private static BigDecimal parseDefinition(final SCalcOptions<?> options) {
        final Map<String, Number> customParams = new LinkedHashMap<>(options.getParams());
        final Map<String, FunctionImpl> customFunctions = new HashMap<>();

        String[] definitions = options.getExpression().split(";");
        for (String definition : definitions) {
            if (definition.trim().startsWith("return")) {
                String expression = definition.trim().substring("return".length());
                String resolvedExpression = resolveExpression(options.getCalculationMathContext(), expression, customParams);
                SCalcExecutor executor = new SCalcExecutor(resolvedExpression, options.getCalculationMathContext(), customFunctions);
                return executor.parse();
            } else {
                String[] assignment = definition.split("=");
                if (assignment.length != 2) {
                    throw new CalculationException("Definitions cannot have more than 2 parts.");
                } else {
                    final String variableOrFunctionName = assignment[0].trim();
                    final String expression = assignment[1].trim();

                    boolean isFunction = variableOrFunctionName.contains("(") && variableOrFunctionName.contains(")");
                    boolean isVariable = !isFunction;

                    if (isVariable) {
                        String resolvedExpression = resolveExpression(options.getCalculationMathContext(), expression, customParams);
                        SCalcExecutor executor = new SCalcExecutor(resolvedExpression, options.getCalculationMathContext(), customFunctions);
                        BigDecimal result = executor.parse();

                        customParams.put(variableOrFunctionName, result);
                    } else {
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
                }
            }
        }

        throw new CalculationException("No 'return ...' statement was found on the given expression.");
    }

    private static String resolveExpression(MathContext mathContext, String expression, Map<String, Number> params) {
        expression = replaceGivenParamsInExpression(expression, params);
        expression = replaceAllParamsConstant(expression, params);
        expression = replaceGlobalConstants(mathContext, expression);

        return expression;
    }

    private static String replaceGlobalConstants(MathContext mathContext, String expression) {
        String pi = new BigDecimal(Math.PI, mathContext).toString();
        expression = replaceWord(expression, "PI", pi);
        expression = replaceWord(expression, "π", pi);

        return expression;
    }

    private static String replaceAllParamsConstant(String expression, Map<String, Number> params) {
        List<String> allParamsAsString = new ArrayList<>();
        for (Number value : params.values()) {
            allParamsAsString.add(paramToString(value));
        }

        String paramString = allParamsAsString.toString();
        paramString = paramString.substring(1, paramString.length() - 1);

        expression = replaceWord(expression, "ALL_PARAMS", paramString);
        return expression;
    }

    private static String replaceGivenParamsInExpression(String expression, Map<String, Number> params) {
        for (Entry<String, Number> param : params.entrySet()) {
            String number = param.getValue() == null ? "0" : paramToString(param.getValue());
            expression = replaceWord(expression, param.getKey(), number);
        }
        return expression;
    }

    private static String paramToString(Number param) {
        return NumberTypeConverter.convert(param, BigDecimal.class).toString();
    }

    private static String replaceWord(String value, String wordToReplace, String newValue) {
        return value.replaceAll("(?i)\\b" + wordToReplace + "\\b", newValue);
    }
}
