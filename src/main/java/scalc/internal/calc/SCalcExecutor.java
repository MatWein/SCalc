package scalc.internal.calc;

import scalc.SCalcOptions;
import scalc.exceptions.CalculationException;
import scalc.interfaces.FunctionImpl;
import scalc.internal.SCalcLogger;
import scalc.internal.functions.Functions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SCalcExecutor {
    private final String expression;
    private final Map<String, FunctionImpl> customFunctions;
    private final SCalcOptions<?> options;

    private int pos = -1;
    private char currentChar;

    SCalcExecutor(String expression, SCalcOptions<?> options) {
        this(expression, options, new HashMap<>(0));
    }

    SCalcExecutor(String expression, SCalcOptions<?> options, Map<String, FunctionImpl> customFunctions) {
        this.options = options;
        this.expression = expression.trim();
        this.customFunctions = customFunctions;
    }

    BigDecimal parse() {
        SCalcLogger.debug(options, "Calculating expression: %s", expression);
        
        if (expression == null || expression.trim().length() == 0) {
            return new BigDecimal(0).setScale(options.getCalculationScale(), options.getCalculationRoundingMode());
        }

        try {
	        nextChar();
	        BigDecimal x = parseExpression();
	        if (pos < expression.length()) throw new CalculationException("Unexpected: " + currentChar);
	        return x;
        } catch (Throwable e) {
        	String message = String.format("Unexpected error on calculation of expression: %s", expression);
	        throw new CalculationException(message, e);
        }
    }

    private BigDecimal parseExpression() {
        BigDecimal x = parseTerm();
        for (; ; ) {
            if (eat('+')) {
                BigDecimal augend = parseTerm();
                BigDecimal result = x.add(augend);
                SCalcLogger.debug(options,
                        "Calculated term. Expression: '+'. Left: '%s'. Right: '%s'. Result: %s",
                        x, augend, result);
                x = result;
            }
            else if (eat('-')) {
                BigDecimal subtrahend = parseTerm();
                BigDecimal result = x.subtract(subtrahend);
                SCalcLogger.debug(options,
                        "Calculated term. Expression: '-'. Left: '%s'. Right: '%s'. Result: %s",
                        x, subtrahend, result);
                x = result;
            }
            else return x;
        }
    }

    private BigDecimal parseTerm() {
        BigDecimal x = parseFactor();
        for (; ; ) {
            if (eat('*')) {
                BigDecimal multiplicand = parseFactor();
                BigDecimal result = x.multiply(multiplicand);
                SCalcLogger.debug(options,
                        "Calculated term. Expression: '*'. Left: '%s'. Right: '%s'. Result: %s",
                        x, multiplicand, result);
                x = result;
            }
            else if (eat('/')) {
                BigDecimal divisor = parseFactor();
                BigDecimal result = x.divide(divisor, options.getCalculationScale(), options.getCalculationRoundingMode());
                SCalcLogger.debug(options,
                        "Calculated term. Expression: '/'. Left: '%s'. Right: '%s'. Result: %s",
                        x, divisor, result);
                x = result;
            }
            else return x;
        }
    }

    private BigDecimal parseFactor() {
        if (eat('+')) return parseFactor();
        if (eat('-')) return new BigDecimal(-1).multiply(parseFactor());

        BigDecimal x;
        int startPos = this.pos;

        if (eat('(')) {
            x = parseExpression();
            eat(')');
        } else if (calculateIsValidNumberChar()) {
            while (calculateIsValidNumberChar()) nextChar();
            x = new BigDecimal(expression.substring(startPos, this.pos)).setScale(options.getCalculationScale(), options.getCalculationRoundingMode());
        } else if (Functions.calculateIsValidFunctionChar(currentChar)) {
            while (Functions.calculateIsValidFunctionChar(currentChar)) nextChar();
            String func = expression.substring(startPos, this.pos);

            boolean isEmptyFunctionBody = expression.charAt(this.pos) == '(' && expression.charAt(this.pos + 1) == ')';
            List<BigDecimal> factors;
            if (isEmptyFunctionBody) {
                factors = new ArrayList<>();
                eat('(');
                eat(')');
            } else {
                eat('(');
                factors = parseExpressions();
                eat(')');
            }

            FunctionImpl funcImpl = Functions.FUNCTIONS.get(func);
            if (funcImpl == null) {
                funcImpl = customFunctions.get(func);
            }
	        if (funcImpl == null) {
		        funcImpl = options.getUserFunctions().get(func);
	        }
            if (funcImpl == null) {
                throw new CalculationException("Unknown function: " + func);
            }

            x = funcImpl.call(options, factors);
    
            SCalcLogger.debug(options,
                    "Call function '%s'. Params: '%s'. Result: %s",
                    funcImpl.getClass().getSimpleName(), factors, x);
        } else {
            throw new CalculationException("Unexpected: " + currentChar);
        }

        if (eat('^')) {
            BigDecimal input = parseFactor();
            BigDecimal result = calulatePow(x, input, options);
            SCalcLogger.debug(options,
                    "Calculated term. Expression: '^'. Left: '%s'. Right: '%s'. Result: %s",
                    x, input, result);
            x = result;
        }

        return x;
    }

    static BigDecimal calulatePow(BigDecimal value, BigDecimal power, SCalcOptions<?> options) {
        return BigDecimal.valueOf(Math.pow(value.doubleValue(), power.doubleValue())).setScale(options.getCalculationScale(), options.getCalculationRoundingMode());
    }

    private List<BigDecimal> parseExpressions() {
        List<BigDecimal> expressions = new ArrayList<>();

        do {
            expressions.add(parseExpression());
        } while (eat(','));

        return expressions;
    }

    private void nextChar() {
        currentChar = (++pos < expression.length()) ? expression.charAt(pos) : 0;
    }

    private boolean eat(char charToEat) {
        while (isValidWhitespace()) nextChar();
        if (currentChar == charToEat) {
            nextChar();
            return true;
        }

        return false;
    }

    private boolean calculateIsValidNumberChar() {
        return (currentChar >= '0' && currentChar <= '9') || currentChar == '.';
    }

    private boolean isValidWhitespace() {
        return currentChar == ' ' || currentChar == '\t' || currentChar == '\r' || currentChar == '\n';
    }
}
