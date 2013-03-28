package ljas.application;

import java.util.Arrays;

import ljas.application.annotations.AttachToEverySession;
import ljas.application.annotations.LJASApplication;

/**
 * Helper class for analyzing annotations of an {@link Application} interface.
 * 
 * @author jonashansen
 * 
 */
public class ApplicationInfo {

	private Class<? extends Application> appInterface;

	public ApplicationInfo(Class<? extends Application> appClass) {
		this.appInterface = findApplicationInterface(appClass);
	}

	public LJASApplication getApplicationAnnotation() {
		Class<?> applicationInterface = findApplicationInterface(appInterface);
		return applicationInterface.getAnnotation(LJASApplication.class);
	}

	public String getApplicationName() {
		return getApplicationAnnotation().name();
	}

	public String getApplicationVersion() {
		return getApplicationAnnotation().version();
	}

	public AttachToEverySession getAttachToEverySessionAnnotation() {
		return appInterface.getAnnotation(AttachToEverySession.class);
	}

	public boolean hasSessionObject(Class<?> clazz) {
		AttachToEverySession annotation = getAttachToEverySessionAnnotation();
		return annotation != null
				&& Arrays.asList(annotation.classes()).contains(clazz);
	}

	public boolean equals(Class<? extends Application> other) {
		ApplicationInfo otherInfo = new ApplicationInfo(other);

		LJASApplication annotation1 = getApplicationAnnotation();
		LJASApplication annotation2 = otherInfo.getApplicationAnnotation();
		return annotation1.equals(annotation2);
	}

	@SuppressWarnings("unchecked")
	private static Class<? extends Application> findApplicationInterface(
			Class<? extends Application> clazz) {
		if (clazz.isInterface() && Application.class.isAssignableFrom(clazz)) {
			return clazz;
		}

		for (Class<?> interfacee : clazz.getInterfaces()) {
			if (Application.class.isAssignableFrom(interfacee)) {
				return (Class<? extends Application>) interfacee;
			}
		}
		return null;
	}
}
