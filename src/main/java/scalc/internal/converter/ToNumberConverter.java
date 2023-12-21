package scalc.internal.converter;

import scalc.exceptions.CalculationException;
import scalc.interfaces.INumber;
import scalc.interfaces.INumberConverter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ToNumberConverter {
    public static <T> Number[] toNumbers(Object object, Function<T, Object> paramExtractor, Map<Class<?>, INumberConverter> converters) {
		if (object == null) {
			return new Number[] { 0.0 };
		}
		
		if (object instanceof Collection) {
			return ((Collection<T>) object).stream()
					.map(o -> toNumbers(o, paramExtractor, converters))
					.flatMap(Arrays::stream)
					.collect(Collectors.toList())
					.toArray(new Number[] {});
		} else if (object instanceof Object[]) {
			return Arrays.stream((Object[]) object)
					.map(o -> toNumbers(o, paramExtractor, converters))
					.flatMap(Arrays::stream)
					.collect(Collectors.toList())
					.toArray(new Number[] {});
		} else if (object instanceof int[]) {
			return Arrays.stream((int[]) object)
					.boxed()
					.map(o -> toNumbers(o, paramExtractor, converters))
					.flatMap(Arrays::stream)
					.collect(Collectors.toList())
					.toArray(new Number[] {});
		} else if (object instanceof long[]) {
			return Arrays.stream((long[]) object)
					.boxed()
					.map(o -> toNumbers(o, paramExtractor, converters))
					.flatMap(Arrays::stream)
					.collect(Collectors.toList())
					.toArray(new Number[] {});
		} else if (object instanceof double[]) {
			return Arrays.stream((double[]) object)
					.boxed()
					.map(o -> toNumbers(o, paramExtractor, converters))
					.flatMap(Arrays::stream)
					.collect(Collectors.toList())
					.toArray(new Number[] {});
		} else {
			return new Number[] { toNumber(paramExtractor.apply((T)object), converters) };
		}
    }
	
    public static Number toNumber(Object object, Map<Class<?>, INumberConverter> converters) {
        if (object == null) {
            return 0.0;
        }

        if (object instanceof Number) {
            return (Number)object;
        }
        if (object instanceof CharSequence) {
            String value = object.toString();
            try {
                return new BigDecimal(value);
            } catch (Throwable e) {
                throw new CalculationException(String.format("Cannot parse bigdecimal from string '%s'.", value), e);
            }
        }
	    if (object instanceof INumber) {
	    	return ((INumber)object).toBigDecimal();
	    }
        
        INumberConverter numberConverter = converters.get(object.getClass());
        if (numberConverter == null) {
            throw new CalculationException(String.format("Cannot find converter for '%s'.", object));
        }

        try {
            return numberConverter.toBigDecimal(object);
        } catch (Throwable e) {
            throw new CalculationException(String.format("Cannot parse number from '%s' using %s.", object, numberConverter), e);
        }
    }

    public static <RETURN_TYPE> RETURN_TYPE toResultType(
            BigDecimal value,
            Class<RETURN_TYPE> returnType,
            Map<Class<?>, INumberConverter> converters) {

        if (returnType.equals(BigDecimal.class)) {
            return (RETURN_TYPE)value;
        }

        if (Number.class.isAssignableFrom(returnType)
		        || double.class.equals(returnType)
		        || int.class.equals(returnType)
		        || short.class.equals(returnType)
		        || long.class.equals(returnType)
		        || float.class.equals(returnType)) {
        	
            return (RETURN_TYPE)NumberTypeConverter.convert(value, (Class)returnType);
        }
        
        INumberConverter numberConverter = converters.get(returnType);
        if (numberConverter == null) {
            throw new CalculationException(String.format("Cannot find converter for '%s'.", returnType.getName()));
        }

        return (RETURN_TYPE)numberConverter.fromBigDecimal(value);
    }
}
