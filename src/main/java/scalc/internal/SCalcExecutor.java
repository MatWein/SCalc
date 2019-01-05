package scalc.internal;

import scalc.SCalcOptions;
import scalc.internal.converter.NumberTypeConverter;
import scalc.internal.functions.FunctionImpl;
import scalc.internal.functions.Functions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SCalcExecutor<RETURN_TYPE> {
    private final SCalcOptions<RETURN_TYPE> options;
    private final String expression;

    private int pos = -1;
    private char currentChar;

    public SCalcExecutor(SCalcOptions<RETURN_TYPE> options, String expression) {
        this.options = options;
        this.expression = expression;
    }

    BigDecimal parse() {
        if (expression == null || expression.trim().length() == 0) {
            return new BigDecimal(0, options.getCalculationMathContext());
        }

        if (expression.equals("+") || expression.equals("-") || expression.equals("*") || expression.equals("/") || expression.equals("^")) {
            return calculateSingleOperatorExpression();
        }
        
        nextChar();
        BigDecimal x = parseExpression();
        if (pos < expression.length()) throw new RuntimeException("Unexpected: " + currentChar);
        return x;
    }

    private BigDecimal calculateSingleOperatorExpression() {
        if (options.getParams() == null || options.getParams().isEmpty()) {
            return new BigDecimal(0, options.getCalculationMathContext());
        }

        List<Number> paramsInOrder = new ArrayList<>(options.getParams().values());

        BigDecimal result = NumberTypeConverter.convert(paramsInOrder.get(0), BigDecimal.class);
        for (int i = 1; i < paramsInOrder.size(); i++) {
            BigDecimal value = NumberTypeConverter.convert(paramsInOrder.get(i), BigDecimal.class);

            switch (expression) {
                case "+": result = result.add(value); break;
                case "-": result = result.subtract(value); break;
                case "*": result = result.multiply(value); break;
                case "/": result = result.divide(value, options.getCalculationMathContext()); break;
                case "^": result = calulatePow(result, value); break;
                default: throw new RuntimeException("Expression invalid.");
            }
        }

        return result;
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
            else if (eat('/')) x = x.divide(parseFactor(), options.getCalculationMathContext());
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
            x = new BigDecimal(expression.substring(startPos, this.pos), options.getCalculationMathContext());
        } else if (Functions.calculateIsValidFunctionChar(currentChar)) {
            while (Functions.calculateIsValidFunctionChar(currentChar)) nextChar();
            String func = expression.substring(startPos, this.pos);
            List<BigDecimal> factors = parseFactors();
            eat(')');

            FunctionImpl funcImpl = Functions.FUNCTIONS.get(func);
            if (funcImpl == null) {
                throw new RuntimeException("Unknown function: " + func);
            } else {
                x = funcImpl.call(options, factors);
            }
        } else {
            throw new RuntimeException("Unexpected: " + currentChar);
        }

        if (eat('^')) x = calulatePow(x, parseFactor());

        return x;
    }

    private BigDecimal calulatePow(BigDecimal value, BigDecimal power) {
        return new BigDecimal(Math.pow(value.doubleValue(), power.doubleValue()), options.getCalculationMathContext());
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
