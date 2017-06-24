package com.happypeople.vi;

/** Models a logical cursor position relativ to a view.
 * This is, a view shows some logical lines on screen.
 * If one of these lines is fairly long, it can occupy
 * more than one line on the screen.
 */
public class ViewCursorPosition extends CursorPosition<ViewCursorPosition> {

	/** The Position (0,0) */
	public final static ViewCursorPosition ORIGIN=new ViewCursorPosition(0, 0);

	private ViewCursorPosition(final long x, final long y) {
		super(x, y);
	}

	@Override
	public ViewCursorPosition addX(final long dX) {
		return new ViewCursorPosition(getX()+dX, getY());
	}

	@Override
	public ViewCursorPosition addY(final long dY) {
		return new ViewCursorPosition(getX(), getY()+dY);
	}
}
