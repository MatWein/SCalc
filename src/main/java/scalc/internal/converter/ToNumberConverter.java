package scalc.internal.converter;

import java.math.BigDecimal;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ToNumberConverter {
    public static Number toNumber(Object object, Map<Class<?>, INumberConverter> converters) {
        if (object == null) {
            return null;
        }

        if (object instanceof Number) {
            return (Number)object;
        }

        INumberConverter numberConverter = converters.get(object.getClass());
        if (numberConverter == null) {
            throw new IllegalArgumentException(String.format("Cannot find converter for '%s'.", object));
        }

        return numberConverter.toBigDecimal(object);
    }

    public static <RETURN_TYPE> RETURN_TYPE toResultType(
            BigDecimal value,
            Class<RETURN_TYPE> returnType,
            Map<Class<?>, INumberConverter> converters) {

        if (returnType.equals(BigDecimal.class)) {
            return (RETURN_TYPE)value;
        }

        if (Number.class.isAssignableFrom(returnType)) {
            return (RETURN_TYPE)NumberTypeConverter.convert(value, (Class)returnType);
        }

        INumberConverter numberConverter = converters.get(returnType);
        if (numberConverter == null) {
            throw new IllegalArgumentException(String.format("Cannot find converter for '%s'.", returnType.getName()));
        }

        return (RETURN_TYPE)numberConverter.fromBigDecimal(value);
    }
}
