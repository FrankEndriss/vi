package com.happypeople.vi;

/** Used to express cursor position in the data model, iE line, column
 */
public class DataCursorPosition extends CursorPosition<DataCursorPosition> {
	public DataCursorPosition(final long x, final long y) {
		super(x, y);
	}

	@Override
	public DataCursorPosition addX(final long dX) {
		return new DataCursorPosition(getX()+dX, getY());
	}

	@Override
	public DataCursorPosition addY(final long dY) {
		return new DataCursorPosition(getX(), getY()+dY);
	}
}
