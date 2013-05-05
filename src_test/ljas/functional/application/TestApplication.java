package ljas.functional.application;

import ljas.application.Application;
import ljas.application.annotations.Cached;
import ljas.application.annotations.LJASApplication;

@LJASApplication(name = "ljas.testing", version = "1.0")
public interface TestApplication extends Application {
	void sleep(long time);

	int getSum(int numOne, int numTwo);

	@Cached
	int getSumCached(int numOne, int numTwo);
}
