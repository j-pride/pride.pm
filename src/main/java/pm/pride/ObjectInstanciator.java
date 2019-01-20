package pm.pride;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import oracle.jdbc.Const;

public class ObjectInstanciator {
	Method cloneMethod;
	Constructor<?> defaultConstructor;
	Constructor<?> copyConstructor;

	public ObjectInstanciator(Class<?> objectType) {
		try {
			cloneMethod = objectType.getMethod("clone");
		}
		catch(NoSuchMethodException nsmx1) {
			try {
				defaultConstructor = objectType.getConstructor();
			}
			catch(NoSuchMethodException nsmx2) {
				try {
					copyConstructor = objectType.getConstructor(objectType);
				}
				catch(NoSuchMethodException nsmx3) {
					// There is no way to duplicate objects of that type.
					// Well, this is no reason to report a problem
					// Maybe there is no need to create any duplicates at all
				}
			}
		}
	}

	public Object instanciate(Object referenceObject) throws ReflectiveOperationException {
		if (cloneMethod != null) {
			return cloneMethod.invoke(referenceObject);
		}
		if (defaultConstructor != null) {
			return defaultConstructor.newInstance();
		}
		if (copyConstructor != null) {
			return copyConstructor.newInstance(referenceObject);
		}
		throw new NoSuchMethodException();
	}

}
