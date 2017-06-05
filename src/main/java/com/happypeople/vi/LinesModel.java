package com.happypeople.vi;

/** This models the contents of one "file" the user can edit. It is organized as a
 * list of lines. Implementation most likely would use a TreeList.
 */
public interface LinesModel {
    /** @return Number of available lines */
    long getSize();

    /** gets the line, lineNo starting at 0 */
    String get(long lineNo);

    /** replace content of line, edit */
    void replace(long lineNo, String newVersionOfLine);

    /** create new line */
    void insertAfter(long lineNo, String newLine);
    void insertBefore(long lineNo, String newLine);

    /** remove a line */
    void remove(long lineNo);

    /*
    enum ChangeType {
    	INSERT,
    	CHANGE,
    	REMOVE
    }
    */

    interface LinesModelChangedEvent {
    }
    interface LinesModelLineInsertedEvent extends LinesModelChangedEvent {
    	/** @return the line number of the new line.
    	 * All lines above that one did move up by one.
    	 */
    	long getLineNo();
    }
    interface LinesModelLineChangedEvent extends LinesModelChangedEvent {
    	/** @return the number of the changed line
    	 */
    	long getLineNo();
    }
    interface LinesModelLineRemovedEvent extends LinesModelChangedEvent {
    	/** @return the number the line had before it was removed.
    	 * All lines above that one did move down by one.
    	 */
    	long getLineNo();
    }
    
    public void addLinesModelChangedEventListener(LinesModelChangedEventListener listener);
}
