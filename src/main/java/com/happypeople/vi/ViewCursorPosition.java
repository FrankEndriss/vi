package com.happypeople.vi;

/** Models a logical cursor position relativ to a view.
 * This is, a view shows some logical lines on screen.
 * If one of these lines is fairly long, it can occupy
 * more than one line on the screen.
 */
public class ViewCursorPosition extends CursorPosition {
	public ViewCursorPosition(long x, long y) {
		super(x, y);
	}
}
