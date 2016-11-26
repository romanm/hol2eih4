package hol2eih4;

import org.springframework.core.env.Environment;

public class PropertyHolder {

	private Environment env;

	public PropertyHolder(Environment env) {
		this.env = env;
	}

	public String get(String property) {
		return env.getProperty(property);
	}

}
