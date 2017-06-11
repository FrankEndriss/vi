package com.happypeople.vi;

public interface View extends LinesModelChangedEventListener, CursorPositionChangedEventListener, FirstLineChangedEventListener {
	
	interface ViewSizeChangedEvent {
		/** @return new number of columns */
		public int getSizeX();
		/** @return new number of lines */
		public int getSizeY();
	}

	interface ViewSizeChangedEventListener {
		public void viewSizeChanged(ViewSizeChangedEvent evt);
	}

	/** Add a Observer wich is called whenever the size of the view changes.
	 * @param listener
	 */
	public void addViewSizeChangedEventListener(ViewSizeChangedEventListener listener);

	/**
	 * @return the available lines in the view most likely depending on the size of the application window
	public int getWindowLines();
	 */
	
	/**
	 * @return the available rows in the view most likely depending on the size of the application window
	public int getWindowRows();
	 */
}
