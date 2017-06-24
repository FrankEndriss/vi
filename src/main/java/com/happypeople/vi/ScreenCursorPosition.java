package com.happypeople.vi;

/** Used for cursor positioning on screen
 */
public class ScreenCursorPosition extends CursorPosition<ScreenCursorPosition> {
	public ScreenCursorPosition(final long x, final long y) {
		super(x, y);
	}

	@Override
	public ScreenCursorPosition addX(final long dX) {
		return new ScreenCursorPosition(getX()+dX, getY());
	}

	@Override
	public ScreenCursorPosition addY(final long dY) {
		return new ScreenCursorPosition(getX(), getY()+dY);
	}
}
