package scalc.internal.calc;

import scalc.SCalcOptions;
import scalc.exceptions.CalculationException;
import scalc.internal.functions.FunctionImpl;
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

    public SCalcExecutor(String expression, SCalcOptions<?> options) {
        this(expression, options, new HashMap<String, FunctionImpl>(0));
    }

    public SCalcExecutor(String expression, SCalcOptions<?> options, Map<String, FunctionImpl> customFunctions) {
        this.options = options;
        this.expression = expression.trim();
        this.customFunctions = customFunctions;
    }

    BigDecimal parse() {
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
            if (eat('+')) x = x.add(parseTerm());
            else if (eat('-')) x = x.subtract(parseTerm());
            else return x;
        }
    }

    private BigDecimal parseTerm() {
        BigDecimal x = parseFactor();
        for (; ; ) {
            if (eat('*')) x = x.multiply(parseFactor());
            else if (eat('/')) x = x.divide(parseFactor(), options.getCalculationScale(), options.getCalculationRoundingMode());
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
                throw new CalculationException("Unknown function: " + func);
            }

            x = funcImpl.call(options, factors);
        } else {
            throw new CalculationException("Unexpected: " + currentChar);
        }

        if (eat('^')) x = calulatePow(x, parseFactor(), options);

        return x;
    }

    public static BigDecimal calulatePow(BigDecimal value, BigDecimal power, SCalcOptions<?> options) {
        return new BigDecimal(Math.pow(value.doubleValue(), power.doubleValue())).setScale(options.getCalculationScale(), options.getCalculationRoundingMode());
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
