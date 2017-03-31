package de.mathema.pride;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GetterSetterPair {
    protected Method getMethod;
    protected Method setMethod;
    
    public GetterSetterPair(Class<?> objectType, String propertyName) {
    	this(objectType, toGetterName(propertyName), toSetterName(propertyName));
    }
    
    protected static String toGetterName(String propertyName) {
    	return "get" + propertyNameFirstLetterUp(propertyName);
    }
    
    protected static String toSetterName(String propertyName) {
    	return "set" + propertyNameFirstLetterUp(propertyName);
    }
    
    protected static String propertyNameFirstLetterUp(String propertyName) {
    	String firstLetterUpper = propertyName.substring(0, 1).toUpperCase();
    	return firstLetterUpper + propertyName.substring(1);
	}

	public GetterSetterPair(Class<?> objectType, String getterName, String setterName) {
		/* Find the getter-method for this attribute */
		try { getMethod = objectType.getMethod(getterName); }
		catch(NoSuchMethodException x) {
		    throw new IllegalDescriptorException
				("Method " + getterName + " not found in " + objectType);
		}
	
		/* Find the setter method for this attribute, which is a little more difficult as we can't be shure about the
         * parameter type. So we first try to find a setter which matches the getter method's type. If there is no
         * matching one, we look it up by name and assume the first one being suitable.
		 */
        try {
            setMethod = objectType.getMethod(setterName, new Class[] { getMethod.getReturnType() });
        }
        catch(NoSuchMethodException nsmx) {
    		Method[] methods = objectType.getMethods();
    		for (int i = 0; i < methods.length; i++) {
    		    if (methods[i].getName().equals(setterName)) {
        			setMethod = methods[i];
        			break;
    		    }
    		}
        }
	
		if (setMethod == null)
		    throw new IllegalDescriptorException
				("Method " + setterName + " not found in " + objectType);
    }

	public Class<?> type() {
		return getMethod.getReturnType();
	}

	public Class<?> typeFromSetter() {
		return setMethod.getParameterTypes()[0];
	}
	
	public String getterName() {
		return getMethod.getName();
	}

	public String setterName() {
		return setMethod.getName();
	}

	public Object get(Object obj) throws IllegalAccessException, InvocationTargetException {
		return getMethod.invoke(obj);
	}

	public void set(Object obj, Object value) throws IllegalAccessException, InvocationTargetException {
		setMethod.invoke(obj, new Object[] { value });
	}

}
