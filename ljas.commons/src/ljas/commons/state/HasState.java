package ljas.commons.state;

public interface HasState {
	public void setState(RuntimeEnvironmentState state);

	public RuntimeEnvironmentState getState();
}
