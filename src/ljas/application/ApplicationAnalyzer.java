package ljas.application;

import java.lang.reflect.Method;
import java.util.Arrays;

import ljas.application.annotations.AttachToEverySession;
import ljas.application.annotations.Cached;
import ljas.application.annotations.LJASApplication;
import ljas.exception.ApplicationException;

/**
 * Helper class for analyzing annotations of an {@link Application} interface.
 * 
 * @author jonashansen
 * 
 */
public class ApplicationAnalyzer {
	public static LJASApplication getApplicationAnnotation(
			Class<? extends Application> appClass) {
		Class<?> applicationInterface = findApplicationInterface(appClass);
		return applicationInterface.getAnnotation(LJASApplication.class);
	}

	public static boolean areApplicationsEqual(
			Class<? extends Application> appClass1,
			Class<? extends Application> appClass2) {
		LJASApplication annotation1 = getApplicationAnnotation(appClass1);
		LJASApplication annotation2 = getApplicationAnnotation(appClass2);
		return annotation1.equals(annotation2);
	}

	public static String getApplicationName(
			Class<? extends Application> appClass) {
		return getApplicationAnnotation(appClass).name();
	}

	public static String getApplicationVersion(
			Class<? extends Application> appClass) {
		return getApplicationAnnotation(appClass).version();
	}

	public static AttachToEverySession getAttachToEverySessionAnnotation(
			Class<? extends Application> appClass) {
		return findApplicationInterface(appClass).getAnnotation(
				AttachToEverySession.class);
	}

	public static boolean isCached(Class<? extends Application> appClass,
			Method method) {
		return method.getAnnotation(Cached.class) != null;
	}

	public static void validateApplication(Class<? extends Application> appClass)
			throws ApplicationException {
		// Does the 'main' annotation exist?
		LJASApplication applicationAnnotation = ApplicationAnalyzer
				.getApplicationAnnotation(appClass);
		if (applicationAnnotation == null) {
			throw new ApplicationException("Missing annotation '"
					+ LJASApplication.class.getSimpleName()
					+ "' on the Server application.");
		}

		// A cached method always has to return a value
		for (Method m : appClass.getMethods()) {
			if (isCached(appClass, m) && m.getReturnType().equals(Void.TYPE)) {
				throw new ApplicationException(
						"Method "
								+ m.getName()
								+ " is cached but does not return a object which can be cached (void).");
			}
		}
	}

	public static boolean hasSessionObject(
			Class<? extends Application> appClass, Class<?> clazz) {
		AttachToEverySession annotation = getAttachToEverySessionAnnotation(appClass);
		return annotation != null
				&& Arrays.asList(annotation.classes()).contains(clazz);
	}

	private static Class<?> findApplicationInterface(Class<?> clazz) {
		if (clazz.isInterface() && Application.class.isAssignableFrom(clazz)) {
			return clazz;
		}

		for (Class<?> interfacee : clazz.getInterfaces()) {
			if (Application.class.isAssignableFrom(interfacee)) {
				return interfacee;
			}
		}
		return null;
	}

}
