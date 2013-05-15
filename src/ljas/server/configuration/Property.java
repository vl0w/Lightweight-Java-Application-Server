package ljas.server.configuration;

public enum Property {
	PORT(Integer.class, 7755), //
	MAXIMUM_CLIENTS(Integer.class, Integer.MAX_VALUE), //
	LOG4J_PATH(String.class);

	private Class<?> valueClass;
	private Object defaultValue;

	public Class<?> getValueClass() {
		return valueClass;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	Property(Class<?> valueClass) {
		this(valueClass, null);
	}

	private <V> Property(Class<V> valueClass, V defaultValue) {
		this.valueClass = valueClass;
		this.defaultValue = defaultValue;
	}

}
