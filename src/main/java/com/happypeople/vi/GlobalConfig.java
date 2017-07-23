package com.happypeople.vi;

import java.util.Optional;

public interface GlobalConfig {

	/** Returns a globally configurated key/value
	 * @param key some key
	 * @return the value for key
	 */
	public Optional<String> getValue(String key);

}
