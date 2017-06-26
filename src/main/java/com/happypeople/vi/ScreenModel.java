package com.happypeople.vi;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/** This class models the visible screen.
 * All in all, it is a Deque<ScreenLine>
 */
public class ScreenModel {
	/** Number of lines on screen */
	private long screenLineCount=0;

	/** The screenModel holds the lines currently visible
	 * on screen.
	 */
	private final LinkedList<ScreenLine> screenArray=new LinkedList<>();

	private final Set<ScreenModelChangedEventListener> screenModelChangedEventListeners=new HashSet<>();

	public void insertTop(final ScreenLine line) {
		screenArray.addFirst(line);
		screenLineCount+=line.getNumScreenLines();
		fireChanged();
	}
	public void removeTop() {
		final ScreenLine line=screenArray.pollFirst();
		if(line!=null)
			screenLineCount-=line.getNumScreenLines();
		fireChanged();
	}

	public void insertBottom(final ScreenLine line) {
		screenArray.addLast(line);
		screenLineCount+=line.getNumScreenLines();
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
	public String render(int idx, int lengthLimit) {
		return screenArray.get(idx).render(lengthLimit);
	}
	
	public static class ScreenModelChangedEvent {
		
	}
	
	public static interface ScreenModelChangedEventListener {
		public void screenModelChanged(ScreenModelChangedEvent evt);
	}
	
	public void addScreenModelChangedEventListener(ScreenModelChangedEventListener listener) {
		screenModelChangedEventListeners.add(listener);
	}
	
	protected void fireChanged() {
		for(ScreenModelChangedEventListener listener : screenModelChangedEventListeners) {
			listener.screenModelChanged(new ScreenModelChangedEvent());
		}
	}
}