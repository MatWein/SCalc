package scalc.internal;

import org.junit.Assert;
import org.junit.Test;
import scalc.internal.expr.Constant;
import scalc.internal.expr.Expression;
import scalc.internal.expr.Function;
import scalc.internal.expr.Variable;

import java.math.BigDecimal;

public class ExpressionParserTest {
    @Test
    public void parseConstant_Double() {
        Expression expression = ExpressionParser.parse("22.1");

        Assert.assertTrue(expression instanceof Constant);
        Constant constant = (Constant) expression;

        Assert.assertTrue(constant.isPositive());
        Assert.assertEquals(new BigDecimal("22.1"), constant.getValue());
    }

    @Test
    public void parseConstant_Double_Trim() {
        Expression expression = ExpressionParser.parse("   -0.0001  ");

        Assert.assertTrue(expression instanceof Constant);
        Constant constant = (Constant) expression;

        Assert.assertFalse(constant.isPositive());
        Assert.assertEquals(new BigDecimal("0.0001"), constant.getValue());
    }

    @Test
    public void parseConstant_Integer() {
        Expression expression = ExpressionParser.parse("100");

        Assert.assertTrue(expression instanceof Constant);
        Constant constant = (Constant) expression;

        Assert.assertTrue(constant.isPositive());
        Assert.assertEquals(new BigDecimal("100"), constant.getValue());
    }

    @Test
    public void parseConstant_Integer_Negative() {
        Expression expression = ExpressionParser.parse("-100");

        Assert.assertTrue(expression instanceof Constant);
        Constant constant = (Constant) expression;

        Assert.assertFalse(constant.isPositive());
        Assert.assertEquals(new BigDecimal("100"), constant.getValue());
    }

    @Test
    public void parseVariable() {
        Expression expression = ExpressionParser.parse("some_var");

        Assert.assertTrue(expression instanceof Variable);
        Variable variable = (Variable) expression;

        Assert.assertTrue(variable.isPositive());
        Assert.assertEquals("some_var", variable.getName());
    }

    @Test
    public void parseVariable_Negative() {
        Expression expression = ExpressionParser.parse("-some_var");

        Assert.assertTrue(expression instanceof Variable);
        Variable variable = (Variable) expression;

        Assert.assertFalse(variable.isPositive());
        Assert.assertEquals("some_var", variable.getName());
    }

    @Test
    public void parseFunction() {
        Expression expression = ExpressionParser.parse("some_func()");

        Assert.assertTrue(expression instanceof Function);
        Function function = (Function) expression;

        Assert.assertTrue(function.isPositive());
        Assert.assertEquals("some_func", function.getName());
        Assert.assertEquals(0, function.getExpressions().size());
    }

    @Test
    public void parseFunction_OneParam_Negative() {
        Expression expression = ExpressionParser.parse("-some_func(20)");

        Assert.assertTrue(expression instanceof Function);
        Function function = (Function) expression;

        Assert.assertFalse(function.isPositive());
        Assert.assertEquals("some_func", function.getName());
        Assert.assertEquals(1, function.getExpressions().size());
        Assert.assertTrue(function.getExpressions().get(0) instanceof Constant);
    }

    @Test
    public void parseFunction_TwoParams() {
        Expression expression = ExpressionParser.parse("√(4, 2)");

        Assert.assertTrue(expression instanceof Function);
        Function function = (Function) expression;

        Assert.assertFalse(function.isPositive());
        Assert.assertEquals("√", function.getName());
        Assert.assertEquals(2, function.getExpressions().size());
        Assert.assertTrue(function.getExpressions().get(0) instanceof Constant);
        Assert.assertTrue(function.getExpressions().get(1) instanceof Constant);
    }

    @Test
    public void parseFunction_FunctionInFunctionParams() {
        Expression expression = ExpressionParser.parse("√(√(16))");

        Assert.assertTrue(expression instanceof Function);
        Function function = (Function) expression;

        Assert.assertFalse(function.isPositive());
        Assert.assertEquals("√", function.getName());
        Assert.assertEquals(1, function.getExpressions().size());
        Assert.assertTrue(function.getExpressions().get(0) instanceof Function);
    }
}