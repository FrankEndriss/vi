package com.happypeople.vi;

public interface View extends ScreenModelChangedEventListener {

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
	 * The first call to listener is done synchronously.
	 * @param listener
	 */
	public void addViewSizeChangedEventListener(ViewSizeChangedEventListener listener);

	/** Sets the view visible/invisible.
	 * Optional, for some views this might not make sense.
	 * @param visible
	 */
	public void setVisible(boolean visible);

}
