package com.happypeople.vi;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/** This class models the visible screen.
 * All in all, it is a Deque<ScreenLine>
 */
public class ScreenModel {
	/** Number of visible lines on screen, iE sum(screenArray[0-n].getNumScreenLines()) */
	private long screenLineCount=0;

	/** X size of the view, used to calculate positions etc */
	private int sizeX=0;

	/** The screenModel holds the lines currently visible
	 * on screen.
	 */
	private final LinkedList<ScreenLine> screenArray=new LinkedList<>();

	private final Set<ScreenModelChangedEventListener> screenModelChangedEventListeners=new HashSet<>();

	public void setSizeX(final int sizeX) {
		if(this.sizeX!=sizeX) {
			this.sizeX=sizeX;
			screenArray.forEach(line -> line.setScreenSizeX(sizeX));
		}
	}

	public void insertTop(final String line) {
		final ScreenLine mapping=new ScreenLine(line, sizeX);
		screenArray.addFirst(mapping);
		screenLineCount+=mapping.getNumScreenLines();
		fireChanged();
	}

	public void removeTop() {
		final ScreenLine line=screenArray.pollFirst();
		if(line!=null)
			screenLineCount-=line.getNumScreenLines();
		fireChanged();
	}

	public void insertBottom(final String line) {
		final ScreenLine mapping=new ScreenLine(line, sizeX);
		screenArray.addLast(mapping);
		screenLineCount+=mapping.getNumScreenLines();
		fireChanged();
	}
	public void removeBottom() {
		final ScreenLine line=screenArray.pollLast();
		if(line!=null)
			screenLineCount-=line.getNumScreenLines();
		fireChanged();
	}

	public void clear() {
		screenArray.clear();
		screenLineCount=0;
		fireChanged();
	}

	public long getScreenLineCount() {
		return screenLineCount;
	}
	public long getDataLineCount() {
		return screenArray.size();
	}

	/** Render line at idx, and return it
	 * @param idx index of the logical line
	 * @param lengthLimit rendering stops, returned String is not (much) longer than lengthLimit
	 * @return the rendered line, ready to display
	 */
	public String render(final int idx, final int lengthLimit) {
		return screenArray.get(idx).render(lengthLimit);
	}

	public static class ScreenModelChangedEvent {

	}

	public static interface ScreenModelChangedEventListener {
		public void screenModelChanged(ScreenModelChangedEvent evt);
	}

	public void addScreenModelChangedEventListener(final ScreenModelChangedEventListener listener) {
		screenModelChangedEventListeners.add(listener);
	}

	protected void fireChanged() {
		for(final ScreenModelChangedEventListener listener : screenModelChangedEventListeners) {
			listener.screenModelChanged(new ScreenModelChangedEvent());
		}
	}
}