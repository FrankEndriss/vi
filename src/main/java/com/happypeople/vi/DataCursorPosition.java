package com.happypeople.vi;

/** Used to express cursor position in the data model, iE line, column
 */
public class DataCursorPosition extends CursorPosition {
	public DataCursorPosition(long x, long y) {
		super(x, y);
	}
}
