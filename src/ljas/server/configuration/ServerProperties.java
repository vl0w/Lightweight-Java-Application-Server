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

		if (propertyValue == null && property.getDefaultValue() != null) {
			return (V) property.getDefaultValue();
		}

		return (V) propertyValue;

	}

	public <V> void set(Property property, V value) {
		if (!property.getValueClass().equals(value.getClass())) {
			throw new IllegalArgumentException("The property "
					+ property.name() + " expects a "
					+ property.getValueClass().getSimpleName());
		}

		properties.put(property, value);
	}

	public boolean isSet(Property property) {
		return properties.containsKey(property)
				|| property.getDefaultValue() != null;
	}

	@Override
	public String toString() {
		return properties.toString();
	}

	public void reset() {
		properties = new HashMap<>();
	}

}
