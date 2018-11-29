package scalc.internal.converter;

import scalc.SCalc;

import java.math.BigDecimal;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ToNumberConverter {
    public static Number toNumber(Object object, SCalc<?> sCalc) {
        if (object == null) {
            return null;
        }

        if (object instanceof Number) {
            return (Number)object;
        }

        Map<Class<?>, INumberConverter> converters = sCalc.getConverters();
        INumberConverter numberConverter = converters.get(object.getClass());
        if (numberConverter == null) {
            throw new IllegalArgumentException(String.format("Cannot find converter for '%s'.", object));
        }

        return numberConverter.toBigDecimal(object);
    }

    public static <RETURN_TYPE> RETURN_TYPE toResultType(BigDecimal value, SCalc<RETURN_TYPE> sCalc) {
        if (sCalc.getReturnType().equals(BigDecimal.class)) {
            return (RETURN_TYPE)value;
        }

        if (Number.class.isAssignableFrom(sCalc.getReturnType())) {
            return (RETURN_TYPE)NumberTypeConverter.convert(value, (Class)sCalc.getReturnType());
        }

        Map<Class<?>, INumberConverter> converters = sCalc.getConverters();
        INumberConverter numberConverter = converters.get(sCalc.getReturnType());
        if (numberConverter == null) {
            throw new IllegalArgumentException(String.format("Cannot find converter for '%s'.", sCalc.getReturnType().getName()));
        }

        return (RETURN_TYPE)numberConverter.fromBigDecimal(value);
    }
}
