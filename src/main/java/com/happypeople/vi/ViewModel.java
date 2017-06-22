package com.happypeople.vi;

/** The ViewModel keep track about which part of the LinesModel is currently
 * visible, ie the window.
 */
public interface ViewModel {
	/** Request to set the first visible line on screen 
	 * @param firstLine Index into the LinesModel, the line shown as first line on screen
	 */
	public void setFirstLine(long firstLine);

	/** Sets the first line by scrolling up scrollUpLines.
	 * If not possible, nothing is done and false returned.
	 * @param scrollUpLines number of lines to scroll, may be negative to scroll down
	 * @return true if done, false if not possible (out of index)
	 */
	public boolean scrollUp(int scrollUpLines);

    /** Scrolled the whole window */
    interface FirstLineChangedEvent {
           /** @return the index of the first visible line in the lines model, -1 if the
            * LinesModel is empty. */
           long getFirstVisibleLine();
    }

    void addFirstLineChangedEventListener(FirstLineChangedEventListener listener);
 
     /** The method gets a logical cursor position, and returns the position in the LinesModel.
     * @param cpos Cursor position in view
     * @return calculated cursor position in data model
     */
    DataCursorPosition getDataPositionFromViewPosition(ViewCursorPosition cpos);

    /** The method gets a logical cursor position, and returns the position on screen.
     * iE if all lines are shorter than the screen width, and every character
     * occupies one columns, then the positios are the same.
     * But there are long lines, and tabulators.
     * @param cpos Cursor position in view
     * @return calculated cursor position in data model
     */
    ScreenCursorPosition getScreenPositionFromViewPosition(ViewCursorPosition cpos);
}
