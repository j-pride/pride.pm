package de.mathema.pride;

import java.lang.reflect.Array;
import java.sql.SQLException;

/**
 * This class encapsulates Postgres access functions which are only available when
 * working with a Postgres database and having the appropriate drivers installed.
 * When working with different databases, the functions in here should never be
 * called by the PriDE core, so their shouldn't occur any NoSuchMethodExceptions
 * or ClassNotFoundExceptions.
 * The special functions are required to support Postgres' dynamic NoSQL column
 * types which are not part of the JDBC standard.
 * 
 * @author less02
 */
public class PosgresAccess {

    public static Object extractArrayFromResultSet(Object dbValue, Class<?> targetArrayType)
        throws SQLException {
        Object rawArray = ((java.sql.Array)dbValue).getArray();
        Class<?> targetComponentType = targetArrayType.getComponentType();
        if (targetComponentType.isPrimitive() || targetComponentType.isEnum()) {
            int arrayLength = Array.getLength(rawArray);
            Object unboxedArray = Array.newInstance(targetComponentType, arrayLength);
            for (int i = 0; i < arrayLength; i++) {
                Object rawItemValue = Array.get(rawArray, i);
                if (targetComponentType.isPrimitive()) {
                    Array.set(unboxedArray, i, rawItemValue);
                }
                else {
                    Object enumarizedItemValue = Enum.valueOf((Class<Enum>)targetComponentType, rawItemValue.toString());
                    Array.set(unboxedArray, i, enumarizedItemValue);
                }
            }
            return unboxedArray;
        }
        return rawArray;
    }

}
