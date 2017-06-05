package com.happypeople.vi;

public interface View extends LinesModelChangedEventListener, CursorPositionChangedEventListener, FirstLineChangedEventListener {
	
	/**
	 * @return the available lines in the view
	 */
	public int getLines();
	
	/**
	 * @return the available rows in the view
	 */
	public int getRows();

	// TODO more methods to make size changeable, ie addSizeChangedEventListener(...)
}
