package com.happypeople.vi;

/** The ViewModel keep track about which part of the LinesModel is currently
 * visible, and where is the cursor.
 * Additionally there are methods to request cursor movement.
 * One can add listeners for cursor movement and change of first visible line on screen.
 */
public interface ViewModel {
	/** Low-Level cursor movement, request to position the cursor somewhere
	 * on the visible screen.
	 * @param screenLine line number starting at 0
	 * @param charInLine column number starting at 0
	 */
	public void moveCursorToScreenPosition(int screenLine, int charInLine);

	/** Request the cursor to be moved lines lines up (on the screen)
	 * @param lines number of lines, use negative to move down
	 */
	public void moveCursorUp(int lines);

	/** Request the cursor to be moved columns left (on the screen)
	 * @param chars number of columns, use negative to move right
	 */
	public void moveCursorLeft(int chars);
		
	/** Request to set the first visible line on screen 
	 * @param firstLine
	 */
	public void setFirstLine(long firstLine);

     // generic ViewModel events
     interface ViewModelChangeEvent {
    	 // empty
     }

     /** scroll the whole window */
     interface FirstLineChangedEvent extends ViewModelChangeEvent {
             /** @return the index of the first visible line
             */
            long getFirstVisibleLine();
     }

     void addFirstLineChangedEventListener(FirstLineChangedEventListener listener);

     /** change cursor position */
     interface CursorPositionChangedEvent extends ViewModelChangeEvent {
             /** @return Column position of the cursor after change */
            int getX();
             /** @return Row position of the cursor after change */
            int getY();
     }

     void addCursorPositionChangedEventListener(CursorPositionChangedEventListener listener);

}
