package com.happypeople.vi;

public interface ScreenModel {

	/** Sets the column count of the screen
	 * @param sizeX
	 */
	void setSizeX(int sizeX);

	/** Insert a line at top of the screen, may throw
	 * out line(s) at the bottom.
	 * @param line the inserted line
	 */
	void insertTop(String line);

	/** Removes the top line of the screen.
	 */
	void removeTop();

	/** Inserts a line at the bottom of the screen, may
	 * throw out line(s) at the top.
	 * @param line the inserted line
	 */
	void insertBottom(String line);

	/** Removes the last line on the screen.
	 */
	void removeBottom();

	/** Removes all lines
	 */
	void clear();

	/** Note that one data line can be displayed on
	 * more that one screen line. (Whenever the line
	 * is longer than the screenSizeX)
	 * @return the number of data lines
	 */
	long getDataLineCount();

	/**
	 * @return the number of (used) screen lines
	 */
	long getScreenLineCount();

	/** Render line at idx, and return it
	 * TODO check if needed public (should be private)
	 * @param idx index of the logical line
	 * @param lengthLimit rendering stops, returned String is not (much) longer than lengthLimit
	 * @return the rendered line, ready to display
	 */
	String render(int idx, int lengthLimit);

	/** Add the listener to this screen model, ie the view instance
	 * @param listener the view listening to this screen model
	 */
	void addScreenModelChangedEventListener(ScreenModelChangedEventListener listener);

	/** Tell this screen model that the cursor position changed.
	 * @param newPos the new position of the cursor. That position
	 * is within getDataLineCount(), else may throw IllegalArgumentException
	 */
	void cursorPositionChanged(ViewCursorPosition newPos);

	/** TODO get rid of this getter
	 * @return the current cursor position
	 */
	ScreenCursorPosition getCursorPosition();

}