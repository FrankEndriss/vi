package com.happypeople.vi;

/** Scrolled the whole window */
public class ViewModelChangedEvent {
	private final long firstVisibleLine;
	private final Type type;

	public enum Type {
		FirstLineChanged,
		SizeChanged,
	};

	public ViewModelChangedEvent(final long firstVisibleLine) {
		this.type=Type.FirstLineChanged;
		this.firstVisibleLine=firstVisibleLine;
	}
	/** @return the index of the first visible line in the lines model, -1 if the
	 * LinesModel is empty. */
	long getFirstVisibleLine() {
		return firstVisibleLine;
	}
}