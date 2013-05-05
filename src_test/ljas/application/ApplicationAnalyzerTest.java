package ljas.application;

import ljas.application.annotations.Cached;
import ljas.application.annotations.LJASApplication;
import ljas.exception.ApplicationException;

import org.junit.Test;

public class ApplicationAnalyzerTest {

	@Test(expected = ApplicationException.class)
	public void testValidateApplication_ApplicationHasNoAnnotation()
			throws ApplicationException {
		ApplicationAnalyzer.validateApplication(AppWithNoAnnotation.class);
	}

	@Test(expected = ApplicationException.class)
	public void testValidateApplication_MistakenCaching()
			throws ApplicationException {
		ApplicationAnalyzer.validateApplication(MistakenCaching.class);
	}

	private interface AppWithNoAnnotation extends Application {

	}

	@LJASApplication(name = "", version = "")
	private interface MistakenCaching extends Application {

		@Cached
		String method1();

		@Cached
		void method2(); // Illgal - void is not permitted

		@Cached
		String method3();

	}

}
