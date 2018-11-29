package scalc.internal.converter;

import java.math.BigDecimal;

@SuppressWarnings("unchecked")
public class NumberTypeConverter {
    public static <RETURN_TYPE extends Number> RETURN_TYPE convert(Number value, Class<RETURN_TYPE> returnType) {
        Number valueToConvert = value;
        if (valueToConvert == null) {
            valueToConvert = 0.0;
        }

        if (returnType.isAssignableFrom(valueToConvert.getClass())) {
            return (RETURN_TYPE)valueToConvert;
        }

        if (Double.class.equals(returnType) || double.class.equals(returnType)) {
            return (RETURN_TYPE)((Double)value.doubleValue());
        } else if (Integer.class.equals(returnType) || int.class.equals(returnType)) {
            return (RETURN_TYPE)((Integer)value.intValue());
        } else if (Long.class.equals(returnType) || long.class.equals(returnType)) {
            return (RETURN_TYPE)((Long)value.longValue());
        } else if (Float.class.equals(returnType) || float.class.equals(returnType)) {
            return (RETURN_TYPE)((Float)value.floatValue());
        } else if (Short.class.equals(returnType) || short.class.equals(returnType)) {
            return (RETURN_TYPE)((Short)value.shortValue());
        } else if (BigDecimal.class.equals(returnType)) {
            return (RETURN_TYPE)new BigDecimal(value.toString());
        }

        throw new IllegalArgumentException(String.format("Value of type '%s': %s cannot be converted to type '%s'.",
                valueToConvert.getClass().getName(),
                valueToConvert,
                returnType.getName()));
    }
}
