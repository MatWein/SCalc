package scalc.internal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import scalc.internal.converter.INumberConverter;

import java.util.HashMap;
import java.util.Map;

public class ParamExtractorTest {
    private Map<Class<?>, INumberConverter> converters;

    @Before
    public void setUp() {
        converters = new HashMap<>();
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