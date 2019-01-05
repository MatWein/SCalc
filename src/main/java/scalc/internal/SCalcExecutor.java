package scalc.internal;

import scalc.exceptions.CalculationException;
import scalc.internal.functions.FunctionImpl;
import scalc.internal.functions.Functions;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SCalcExecutor {
    private final String expression;
    private final Map<String, FunctionImpl> customFunctions;
    private final MathContext mathContext;

    private int pos = -1;
    private char currentChar;

    public SCalcExecutor(String expression, MathContext mathContext) {
        this(expression, mathContext, new HashMap<String, FunctionImpl>(0));
    }

    public SCalcExecutor(String expression, MathContext mathContext, Map<String, FunctionImpl> customFunctions) {
        this.mathContext = mathContext;
        this.expression = expression.trim();
        this.customFunctions = customFunctions;
    }

    BigDecimal parse() {
        if (expression == null || expression.trim().length() == 0) {
            return new BigDecimal(0, mathContext);
        }

        nextChar();
        BigDecimal x = parseExpression();
        if (pos < expression.length()) throw new CalculationException("Unexpected: " + currentChar);
        return x;
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
            else if (eat('/')) x = x.divide(parseFactor(), mathContext);
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
            x = new BigDecimal(expression.substring(startPos, this.pos), mathContext);
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
                factors = parseFactors();
                eat(')');
            }

            FunctionImpl funcImpl = Functions.FUNCTIONS.get(func);
            if (funcImpl == null) {
                funcImpl = customFunctions.get(func);
            }
            if (funcImpl == null) {
                throw new CalculationException("Unknown function: " + func);
            }

            x = funcImpl.call(mathContext, factors);
        } else {
            throw new CalculationException("Unexpected: " + currentChar);
        }

        if (eat('^')) x = calulatePow(x, parseFactor(), mathContext);

        return x;
    }

    public static BigDecimal calulatePow(BigDecimal value, BigDecimal power, MathContext mathContext) {
        return new BigDecimal(Math.pow(value.doubleValue(), power.doubleValue()), mathContext);
    }

    private List<BigDecimal> parseFactors() {
        List<BigDecimal> factors = new ArrayList<>();

        do {
            factors.add(parseFactor());
        } while (eat(','));

        return factors;
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