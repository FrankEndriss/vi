package com.happypeople.vi;

import com.happypeople.vi.View.ViewSizeChangedEventListener;

/** A CursorModel keeps track of the cursor position on screen.
 */
public interface CursorModel extends ViewSizeChangedEventListener {

	/** Low-Level cursor movement, request to position the cursor somewhere
	 * on the visible screen.
	 * @param screenLine line number starting at 0
	 * @param charInLine column number starting at 0
	 */
	void moveCursorToScreenPosition(int screenLine, int charInLine);

	/** Request the cursor to be moved lines lines up (on the screen)
	 * @param lines number of lines, use negative to move down
	 */
	void moveCursorUp(int lines);

	/** Request the cursor to be moved columns left (on the screen)
	 * @param chars number of columns, use negative to move right
	 */
	void moveCursorLeft(int chars);

	void addCursorPositionChangedEventListener(CursorPositionChangedEventListener listener);

    /** change cursor position on screen */
    public interface CursorPositionChangedEvent {
        /** @return Position of cursor in ViewModel */
    	ViewCursorPosition getCursorPosition();
    }

}