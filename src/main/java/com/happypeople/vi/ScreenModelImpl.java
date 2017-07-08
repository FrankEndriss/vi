package com.happypeople.vi;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class models the visible screen.
 * All in all, it is a Deque<ScreenLine>
 */
public class ScreenModelImpl implements ScreenModel {
	private final static Logger log=LoggerFactory.getLogger(ScreenModelImpl.class);

	/** Number of visible lines on screen, iE sum(screenArray[0-n].getNumScreenLines()) */
	private long screenLineCount=0;

	/** X size of the view, used to calculate positions etc */
	private int sizeX=0;

	/** The screenModel holds the lines currently visible
	 * on screen.
	 */
	private final LinkedList<ScreenLine> screenArray=new LinkedList<>();

	private ScreenCursorPosition screenCursorPosition=ScreenCursorPosition.ORIGIN;

	private final Set<ScreenModelChangedEventListener> screenModelChangedEventListeners=new HashSet<>();

	@Override
	public void setSizeX(final int sizeX) {
		if(this.sizeX!=sizeX) {
			this.sizeX=sizeX;
			screenArray.forEach(line -> line.setScreenSizeX(sizeX));
		}
	}

	@Override
	public void insertTop(final String line) {
		final ScreenLine mapping=new ScreenLine(line, sizeX);
		screenArray.addFirst(mapping);
		screenLineCount+=mapping.getNumScreenLines();
		fireChanged();
	}

	@Override
	public void removeTop() {
		final ScreenLine line=screenArray.pollFirst();
		if(line!=null)
			screenLineCount-=line.getNumScreenLines();
		fireChanged();
	}

	@Override
	public void insertBottom(final String line) {
		final ScreenLine mapping=new ScreenLine(line, sizeX);
		screenArray.addLast(mapping);
		screenLineCount+=mapping.getNumScreenLines();
		fireChanged();
	}

	@Override
	public void removeBottom() {
		final ScreenLine line=screenArray.pollLast();
		if(line!=null)
			screenLineCount-=line.getNumScreenLines();
		fireChanged();
	}

	@Override
	public void clear() {
		screenArray.clear();
		screenLineCount=0;
		fireChanged();
	}

	@Override
	public long getScreenLineCount() {
		return screenLineCount;
	}

	@Override
	public long getDataLineCount() {
		return screenArray.size();
	}

	@Override
	public String render(final int idx, final int lengthLimit) {
		return screenArray.get(idx).render(lengthLimit);
	}

	@Override
	public void cursorPositionChanged(final ViewCursorPosition newPos) {
		screenCursorPosition=calcScreenCursorPosition(newPos);
	}

	private ScreenCursorPosition calcScreenCursorPosition(final ViewCursorPosition viewCursorPos) {
		int idx=0;
		int linesAboveCursor=0;
		final Iterator<ScreenLine> iter=screenArray.iterator();
		while(idx<viewCursorPos.getY() && iter.hasNext()) {
			idx++;
			final ScreenLine screenLine=iter.next();
			linesAboveCursor+=screenLine.getNumScreenLines();
		}
		if(iter.hasNext()) {
			final ScreenLine lineUnderCursor=iter.next(); // TODO check if iter.hasNext()
			final ScreenCursorPosition posInLine=lineUnderCursor.getScreenPos(viewCursorPos.getX());
			log.info("posInLine: "+posInLine);
			return posInLine.addY(linesAboveCursor);
		} else { // should not happen, but does
			return ScreenCursorPosition.ORIGIN;
		}
	}

	@Override
	public void addScreenModelChangedEventListener(final ScreenModelChangedEventListener listener) {
		screenModelChangedEventListeners.add(listener);
	}

	protected void fireChanged() {
		for(final ScreenModelChangedEventListener listener : screenModelChangedEventListeners) {
			listener.screenModelChanged(new ScreenModelChangedEvent(this));
		}
	}

}