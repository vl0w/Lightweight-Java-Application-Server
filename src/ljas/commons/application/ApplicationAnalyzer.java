package ljas.commons.application;

import java.util.Arrays;

import ljas.commons.application.annotations.AttachToEverySession;
import ljas.commons.application.annotations.LJASApplication;

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

	public static boolean hasSessionObject(
			Class<? extends Application> appClass, Class<?> clazz) {
		AttachToEverySession annotation = getAttachToEverySessionAnnotation(appClass);
		return annotation != null
				&& Arrays.asList(annotation.objectClasses()).contains(clazz);
	}

	private static Class<?> findApplicationInterface(Class<?> clazz) {
		for (Class<?> interfacee : clazz.getInterfaces()) {
			if (Application.class.isAssignableFrom(interfacee)) {
				return interfacee;
			}
		}
		return null;
	}

}
