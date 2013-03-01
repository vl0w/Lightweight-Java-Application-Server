package ljas.commons.tasking.step;

public abstract class NavigationStep extends AbstractTaskStep {

	private static final long serialVersionUID = -9056887764696296782L;

	@Override
	public final boolean isForNavigation() {
		return true;
	}

}
