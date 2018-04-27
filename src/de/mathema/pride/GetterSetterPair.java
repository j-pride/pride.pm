package de.mathema.pride;

import java.lang.reflect.Method;

public class GetterSetterPair {
    public static final String GET_METHOD_PREFIX = "get";
    public static final String SET_METHOD_PREFIX = "set";

    protected Method[] getMethod;
    protected Method setMethod;
    private String constantGetValue;

    public GetterSetterPair(Class<?> objectType, String getterName, String setterName) {
        if (getterName.startsWith(GET_METHOD_PREFIX)) {
            getMethod = new Method[1];
        }
        else {
            constantGetValue = getterName;
        }
        initSetterAndLastGetter(objectType, getterName, setterName);
    }

    public GetterSetterPair(Class<?> objectType, String propertyNameChain) {
        String[] propertyNames = propertyNameChain.split("\\.");
        getMethod = new Method[propertyNames.length];
        int i = 0;
        for (;i < propertyNames.length-1; i++) {
            getMethod[i] = findGetter(objectType, toGetterName(propertyNames[i]));
            objectType = getMethod[i].getReturnType();
        }
        initSetterAndLastGetter(objectType, toGetterName(propertyNames[i]), toSetterName(propertyNames[i]));
    }
    
    protected static String toGetterName(String propertyName) {
        return GET_METHOD_PREFIX + propertyNameFirstLetterUp(propertyName);
    }
    
    protected static String toSetterName(String propertyName) {
        return SET_METHOD_PREFIX + propertyNameFirstLetterUp(propertyName);
    }
    
    protected static String propertyNameFirstLetterUp(String propertyName) {
        String firstLetterUpper = propertyName.substring(0, 1).toUpperCase();
        return firstLetterUpper + propertyName.substring(1);
    }

    public void initSetterAndLastGetter(Class<?> objectType, String getterName, String setterName) {
        if (!isConstantGetValue()) {
            getMethod[getMethod.length - 1] = findGetter(objectType, getterName);
        }
        if (setterName == null) {
            return;
        }

        /* Find the setter method for this attribute, which is a little more difficult as we can't be shure about the
         * parameter type. So we first try to find a setter which matches the getter method's type. If there is no
         * matching one, we look it up by name and assume the first one being suitable.
         */
        try {
            setMethod = objectType.getMethod(setterName, new Class[] { lastGetter().getReturnType() });
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

    public Method findGetter(Class<?> objectType, String getterName) {
        try {
            return objectType.getMethod(getterName);
        }
        catch(NoSuchMethodException x) {
            throw new IllegalDescriptorException
                ("Method " + getterName + " not found in " + objectType);
        }
    }

    private Method lastGetter() {
        return getMethod[getMethod.length-1];
    }
    
    public Class<?> type() {
        return lastGetter().getReturnType();
    }

    public Class<?> typeFromSetter() {
        return setMethod.getParameterTypes()[0];
    }
    
    public String getterName() {
        return lastGetter().getName();
    }

    public String setterName() {
        return setMethod.getName();
    }

    public Object get(Object obj) throws ReflectiveOperationException {
        if (isConstantGetValue())
            return constantGetValue;
        return get(obj, getMethod.length);
    }

    public Object getDirectOwner(Object obj) throws ReflectiveOperationException {
        return get(obj, getMethod.length-1);
    }

    protected Object get(Object obj, int depth) throws ReflectiveOperationException {
        for (int i = 0; i < depth; i++) {
            obj = getMethod[i].invoke(obj);
            if (obj == null)
                break;
        }
        return obj;
    }

    public void set(Object obj, Object value) throws ReflectiveOperationException {
        if (setMethod == null) return;
        set(obj, value, getMethod.length-1);
    }

    public void set(Object obj, Object value, int depth) throws ReflectiveOperationException {
        if (setMethod == null) return;
        for (int i = 0; i < depth; i++) {
            obj = getMethod[i].invoke(obj);
            if (obj == null)
                break;
        }
        if (obj == null)
            throw new IllegalArgumentException("Target (sub) object is null");
        setMethod.invoke(obj, value);
    }

    public boolean isConstantGetValue() {
        return this.constantGetValue != null;
    }
}
