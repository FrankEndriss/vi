package com.happypeople.vi;

/** This models the contents of one "file" the user can edit. It is organized as a
 * list of lines. Implementation most likely would use a TreeList.
 * To edit objects of this type use the Interface LinesModelEditor
 */
public interface LinesModel {

	/** @return Number of available lines */
	long getSize();

	/** 
	 * @param lineNo The line number, starting at 0
	 * @return The line lineNo 
	 */
	String get(long lineNo);

	/** Add a change listener to this LinesModel
	 * @param listener The listener to add
	 */
	void addLinesModelChangedEventListener(LinesModelChangedEventListener listener);
}