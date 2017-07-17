package com.happypeople.vi;

public interface GlobalConfig {
	
	/** Returns a globally configurated key/value
	 * @param key some key
	 * @return the value for key, null if undef
	 */
	public String getValue(String key);

}
