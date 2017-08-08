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

	/** Makes the used Font bigger or smaller by adding increment to the current size.
	 * The result of the addition must be gt 0.
	 * @param increment Font size increment
	 */
	public void adjustFontSize(final int increment);
}
