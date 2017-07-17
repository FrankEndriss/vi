package com.happypeople.vi;

/** Abstraction of a component capable of showing a message to the user.
 */
public interface MessageTarget {
	/** Shows the message msg
	 * @param msg the message
	 */
	void showMessage(String msg);
}
