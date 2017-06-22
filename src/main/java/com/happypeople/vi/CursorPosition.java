package com.happypeople.vi;

/** This models the fairly simple x y coordinates, used as the position of a cursor.
 */
public class CursorPosition {
	private final long x;
	private final long y;
	
	public CursorPosition(final long x, final long y) {
		this.x=x;
		this.y=y;
	}
	
	public long getX() {
		return x;
	}
	
	public long getY() {
		return y;
	}
}
