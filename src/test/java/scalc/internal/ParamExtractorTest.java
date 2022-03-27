package scalc.internal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import scalc.exceptions.CalculationException;
import scalc.interfaces.INumberConverter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ParamExtractorTest {
    private Map<Class<?>, INumberConverter> converters;

    @Before
    public void setUp() {
        converters = new HashMap<>();
    }

    @Test
    public void extractParamsFromNameValuePairs() {
        Map<String, Number> result = ParamExtractor.extractParamsFromNameValuePairs(Function.identity(), converters, new Object[0]);
        Assert.assertTrue(result.isEmpty());
    }

    @Test(expected = CalculationException.class)
    public void extractParamsFromNameValuePairs_InvalidPairs() {
        ParamExtractor.extractParamsFromNameValuePairs(Function.identity(), converters, new Object[] { "a" });
    }

    @Test
    public void extractParamsFromNameValuePairs_Result() {
        Map<String, Number> result = ParamExtractor.extractParamsFromNameValuePairs(Function.identity(), converters, new Object[] { "a", 10.1, "b", 2 });

        Assert.assertEquals(2, result.size());
        Assert.assertEquals(10.1, result.get("a"));
        Assert.assertEquals(2, result.get("b"));
    }

    @Test(expected = CalculationException.class)
    public void extractParamsFromNameValuePairs_InvalidPairsByTypeString() {
        ParamExtractor.extractParamsFromNameValuePairs(Function.identity(), converters, new Object[] { "a", "abc", "b", 2 });
    }
    
    @Test
    public void extractParamsFromNameValuePairs_WithString() {
        Map<String, Number> result = ParamExtractor.extractParamsFromNameValuePairs(Function.identity(), converters, new Object[]{"a", "10.2", "b", 2});
    
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(new BigDecimal("10.2"), result.get("a"));
        Assert.assertEquals(2, result.get("b"));
    }

    @Test(expected = CalculationException.class)
    public void extractParamsFromNameValuePairs_InvalidPairsByTypeNumber() {
        ParamExtractor.extractParamsFromNameValuePairs(Function.identity(), converters, new Object[] { 1, 1 });
    }
}