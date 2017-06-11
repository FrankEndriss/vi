package com.happypeople.vi;

/** The ViewModel keep track about which part of the LinesModel is currently
 * visible, ie the window.
 */
public interface ViewModel {
	/** Request to set the first visible line on screen 
	 * @param firstLine
	 */
	public void setFirstLine(long firstLine);

	/** Sets the first line by scrolling up scrollUpLines.
	 * If not possible, nothing is done and false returned.
	 * @param scrollUpLines number of lines to scroll
	 * @return true if done, false if not possible (out of index)
	 */
	public boolean scrollUp(int scrollUpLines);

     /** scroll the whole window */
     interface FirstLineChangedEvent {
             /** @return the index of the first visible line */
            long getFirstVisibleLine();
     }

     void addFirstLineChangedEventListener(FirstLineChangedEventListener listener);
 
     /** The method gets a cursor position, and returns the position in the LinesModel.
     * @param cursorX cursor position X 
     * @param cursorY cursor position Y
     * @param ret array must be of length>=2, then after return it has on
     * ret[0] the X position in the LinesModel, and on
     * ret[1] the Y position in the LinesModel
     */
    void getModelPositionFromCursorPosition(int cursorX, int cursorY, long[] ret);

}
