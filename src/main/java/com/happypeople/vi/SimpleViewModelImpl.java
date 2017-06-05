package com.happypeople.vi;

import java.util.HashSet;
import java.util.Set;

/** Simple implementation.
 * Fixed Size window
 */
public class SimpleViewModelImpl implements ViewModel {

	/** underlying data model */
	private final LinesModel linesModel;

	/** First line of model displayed in window */
	private long firstLine=0;
	
	/** cursor position within window in lines */
	private int cPosX=0;
	private int cPosY=0;

	/** Window size in lines */
	private final int sizeX;
	private final int sizeY;
	
	/** Listeners */
	private final Set<FirstLineChangedEventListener> flceListeners=new HashSet<FirstLineChangedEventListener>();
	private final Set<CursorPositionChangedEventListener> cpceListeners=new HashSet<CursorPositionChangedEventListener>();
	
	public SimpleViewModelImpl(final int sizeX, final int sizeY, final LinesModel linesModel) {
		this.linesModel=linesModel;
		this.sizeX=sizeX;
		this.sizeY=sizeY;
	}

	public void setFirstLine(long firstLine) {
		// TODO Auto-generated method stub
		
	}

	public void moveCursorToScreenPosition(int posX, int posY) {
		this.cPosX=posX;
		this.cPosY=posY>=sizeY?sizeY-1:posY;
		fireCursorPosition(cPosX, cPosY);
	}

	public void moveCursorUp(int lines) {
		this.cPosY=lines>cPosY?0:cPosY-lines;
		if(cPosY>=sizeY)
			cPosY=sizeY-1;
		fireCursorPosition(cPosX, cPosY);
	}

	public void moveCursorLeft(int chars) {
		cPosX-=chars;
		if(cPosX<0)
			cPosX=0;
		fireCursorPosition(cPosX, cPosY);
	}

	public void addFirstLineChangedEventListener(FirstLineChangedEventListener listener) {
		flceListeners.add(listener);
	}

	public void addCursorPositionChangedEventListener(CursorPositionChangedEventListener listener) {
		cpceListeners.add(listener);
	}

	protected void fireCursorPosition(final int lX, final int lY) {
		fireCursorPosition(new CursorPositionChangedEvent() {
			public int getX() {
				return lX;
			}

			public int getY() {
				return lY;
			}
		});
	}

	protected void fireCursorPosition(CursorPositionChangedEvent evt) {
		for(CursorPositionChangedEventListener listener : cpceListeners)
			listener.cursorPositionChanged(evt);
	}
	
	protected void fireFirstLineChanged(final long newIdx) {
		fireFirstLineChanged(new FirstLineChangedEvent() {
			public long getFirstVisibleLine() {
				return newIdx;
			}
		});
	}
	protected void fireFirstLineChanged(FirstLineChangedEvent evt) {
		for(FirstLineChangedEventListener listener : flceListeners)
			listener.firstLineChanged(evt);
	}

}
