package ljas.functional.application;

import ljas.commons.application.Application;
import ljas.commons.application.annotations.LJASApplication;

@LJASApplication(name = "ljas.testing", version = "1.0")
public interface TestApplication extends Application {
	void sleep(long time);

	int getSum(int numOne, int numTwo);
}
