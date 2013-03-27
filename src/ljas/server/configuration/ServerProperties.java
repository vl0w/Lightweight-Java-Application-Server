package ljas.server.configuration;

import java.util.HashMap;
import java.util.Map;

public class ServerProperties {

	private Map<Property, Object> properties;

	public ServerProperties() {
		properties = new HashMap<>();
	}

	@SuppressWarnings("unchecked")
	public <V> V get(Property property) {
		Object propertyValue = properties.get(property);

		if (propertyValue == null && property.defaultValue != null) {
			return (V) property.defaultValue;
		}

		return (V) propertyValue;

	}

	public <V> void set(Property property, V value) {
		if (!property.valueClass.equals(value.getClass())) {
			throw new IllegalArgumentException("The property "
					+ property.name() + " expects a "
					+ property.valueClass.getSimpleName());
		}

		properties.put(property, value);
	}

	public boolean isSet(Property property) {
		return properties.containsKey(property)
				|| property.defaultValue != null;
	}

	@Override
	public String toString() {
		return properties.toString();
	}

	public void reset() {
		properties = new HashMap<>();
	}

}
