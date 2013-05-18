package ljas.session;

/**
 * Represents an address to another device. It includes a host name (ip address)
 * and port.
 * 
 * @author jonashansen
 * 
 */
public class Address {

	private static final String DELIMITER = ":";
	private String host;
	private int port;

	public Address(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public String toString() {
		return host + DELIMITER + port;
	}

	/**
	 * Parses the host and port from an input string. <br>
	 * The host and port must be delimited with ' <b>:</b> ' <br>
	 * <br>
	 * Example: <br>
	 * localhost:9000
	 * 
	 * @param hostAndPort
	 * @return
	 */
	public static Address parseFromString(String hostAndPort) {
		int indexOfDelimiter = hostAndPort.indexOf(DELIMITER);

		if (indexOfDelimiter == -1) {
			throw new IllegalArgumentException("Missing delimiter. Use '"
					+ DELIMITER + "' to delimit your host and port");
		} else if (indexOfDelimiter == 0) {
			throw new IllegalArgumentException("Missing host");
		} else if (indexOfDelimiter == hostAndPort.length() - 1) {
			throw new IllegalArgumentException("Missing port");
		}

		String host = hostAndPort.substring(0, indexOfDelimiter);
		int port = Integer.valueOf(hostAndPort.substring(indexOfDelimiter + 1));

		return new Address(host, port);
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

}
