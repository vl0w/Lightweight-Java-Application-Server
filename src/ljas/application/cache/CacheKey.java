package ljas.application.cache;

import java.lang.reflect.Method;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CacheKey {

	private Method method;
	private Object[] parameterValues;

	public CacheKey(Method method, Object... parameterValues) {
		this.method = method;
		this.parameterValues = parameterValues;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		CacheKey other = (CacheKey) obj;
		EqualsBuilder builder = new EqualsBuilder();
		builder.append(method, other.method);
		builder.append(parameterValues, other.parameterValues);

		return builder.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(method);
		builder.append(parameterValues);
		return builder.toHashCode();
	}
}
