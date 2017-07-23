package com.happypeople.vi;

/** An EditContext is usually the construct where one file/buffer is edited,
 * so it is one window, or one tab whithin one window.
 * For example the ex command ":e <file> " changes the edit context to a new one
 *
 * So, such one EditContext object is usually given as an argument to
 * a method, and should not be used outside of this single call.
 */
public interface EditContext {

	/**
	 * @return the current editor of the payload data
	 */
	public LinesModelEditor getLinesModel();

	/**
	 * @return the graphical representation in vi
	 */
	public CursorModel getCursorModel();

	/**
	 * @return A target to place messages to the user
	 */
	public MessageTarget getMessageTarget();

	/**
	 * @return the configuration of this process
	 */
	public GlobalConfig getGlobalConfig();

	/** Runs this EditContext my reading input from the input queue
	 */
	public void run();

	/** Closes this EditContext and its UI-representation.
	 * Does not ask for "Save file?" or the like, just closes.
	 * Its the implementation of the ":q!" command.
	 */
	public void close();
}
