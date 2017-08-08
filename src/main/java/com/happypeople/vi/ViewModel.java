package com.happypeople.vi;

import com.happypeople.vi.View.ViewSizeChangedEventListener;

/** The ViewModel keep track about which part of the LinesModel is currently
 * visible, ie the window.
 */
public interface ViewModel extends ViewSizeChangedEventListener {
	/** Request to set the first visible line on screen
	 * @param firstLine Index into the LinesModel, the line shown as first line on screen
	 */
	public void setFirstLine(long firstLine);

	/** Request vertical movement of cursor. This moves the window in a way
	 * that the cursor will stay within the window (scrolls up or down)
	 * if necessary.
	 * Note that the movement is done so that the cursor tries not to move
	 * left or right on screen, so it takes respect of tabs in the lines,
	 * and the tab stop size of the view.
	 * @param pos the old position
	 * @param lines number of lines to scroll up, might be negative to scroll down
	 * @return the new cursor position
	 */
	public DataCursorPosition moveCursorUp(DataCursorPosition oldPos, int lines);

	/** Tells the viewModel that the position of the cursor changed to newPos.
	 * The viewModel needs to scroll if necessary.
	 * @param newPos the new position of the cursor.
	 */
	public void cursorPositionChanged(DataCursorPosition newPos);

	/** Sets the first line by scrolling up scrollUpLines.
	 * If not possible, nothing is done and false returned.
	 * Note that the cursor position does not change, ie the cursor
	 * moves down on screen (for a positive number).
	 * Can be implemented as setFirstLine(currentFirstLine-scrollUpLines)
	 * @param scrollUpLines number of lines to scroll, may be negative to scroll down
	 * @return true if done, false if not possible (out of index)
	 */
	public boolean scrollUp(long scrollUpLines);

	void addFirstLineChangedEventListener(ViewModelChangedEventListener listener);

	/** The method gets a logical cursor position, and returns the position in the LinesModel.
	 * TODO check if really needed public
	 * @param cpos Cursor position in view
	 * @return calculated cursor position in data model
    DataCursorPosition getDataPositionFromViewPosition(ViewCursorPosition cpos);
	 */

	/** The method gets a logical cursor position, and returns the position on screen.
	 * iE if all lines are shorter than the screen width, and every character
	 * occupies one columns, then the positios are the same.
	 * But there are long lines, and tabulators.
	 * TODO check if really needed public
	 * @param cpos Cursor position in view
	 * @return calculated cursor position in data model
	 */
	ScreenCursorPosition getScreenPositionFromViewPosition(ViewCursorPosition cpos);

	/**
	 * @return the index of the last logical line visible on screen
	 */
	public long getMaxLogicalScreenLineIdx();

	public void forceRepaint();
}
