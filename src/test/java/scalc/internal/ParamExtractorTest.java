package scalc.internal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import scalc.internal.converter.INumberConverter;
import scalc.internal.expr.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParamExtractorTest {
    private Map<Class<?>, INumberConverter> converters;

    @Before
    public void setUp() {
        converters = new HashMap<>();
    }

    @Test
    public void extractParamsInOrder_Constant() {
        Map<String, Number> result = ParamExtractor.extractParamsInOrder(converters, new Constant(true, 12), new Object[0]);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void extractParamsInOrder_Function() {
        Map<String, Number> result = ParamExtractor.extractParamsInOrder(
                converters,
                new Function(true, "test(12.0)", "test", new Constant(false, 12.0)),
                new Object[0]);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void extractParamsInOrder_Operator() {
        Map<String, Number> result = ParamExtractor.extractParamsInOrder(converters, Operator.MULTIPLICATION, new Object[0]);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void extractParamsInOrder_Variable() {
        Map<String, Number> result = ParamExtractor.extractParamsInOrder(converters, new Variable(true, "var1"), new Object[] { 100 });

        Assert.assertEquals(1, result.size());
        Assert.assertEquals(100, result.get("var1"));
    }

    @Test
    public void extractParamsInOrder_ComplexExpression() {
        List<Expression> subExpressions = new ArrayList<>();
        subExpressions.add(new Function(true, "√(-c)", "√", new Variable(false, "c")));

        List<Expression> expressions = new ArrayList<>();
        expressions.add(new Variable(true, "var1"));
        expressions.add(Operator.ADDITION);
        expressions.add(new Variable(true, "var2"));
        expressions.add(Operator.MULTIPLICATION);
        expressions.add(new ComplexExpression("√(-c)", subExpressions));

        ComplexExpression complexExpression = new ComplexExpression("var1 + var2 * (√(-c))", expressions);
        Map<String, Number> result = ParamExtractor.extractParamsInOrder(converters, complexExpression, new Object[] { 100, 10, 1 });

        Assert.assertEquals(3, result.size());
        Assert.assertEquals(100, result.get("var1"));
        Assert.assertEquals(10, result.get("var2"));
        Assert.assertEquals(1, result.get("c"));
    }

    @Test
    public void extractParamsFromNameValuePairs() {
        Map<String, Number> result = ParamExtractor.extractParamsFromNameValuePairs(converters, new Object[0]);
        Assert.assertTrue(result.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void extractParamsFromNameValuePairs_InvalidPairs() {
        ParamExtractor.extractParamsFromNameValuePairs(converters, new Object[] { "a" });
    }

    @Test
    public void extractParamsFromNameValuePairs_Result() {
        Map<String, Number> result = ParamExtractor.extractParamsFromNameValuePairs(converters, new Object[] { "a", 10.1, "b", 2 });

        Assert.assertEquals(2, result.size());
        Assert.assertEquals(10.1, result.get("a"));
        Assert.assertEquals(2, result.get("b"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void extractParamsFromNameValuePairs_InvalidPairsByTypeString() {
        ParamExtractor.extractParamsFromNameValuePairs(converters, new Object[] { "a", "10.1", "b", 2 });
    }

    @Test(expected = IllegalArgumentException.class)
    public void extractParamsFromNameValuePairs_InvalidPairsByTypeNumber() {
        ParamExtractor.extractParamsFromNameValuePairs(converters, new Object[] { 1, 1 });
    }
}