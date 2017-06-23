package com.happypeople.vi;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/** Simple implementation.
 * Fixed Size window
 */
@Component
@Scope("prototype")
public class SimpleViewModelImpl implements ViewModel {

	/** underlying data model */
	private final LinesModel linesModel;

	/** Index of first line of linesModel displayed in window */
	private long firstLine=0;
	
	/** Implementation of a logical line and the
	 * display of that line on screen.
	 * TODO: make editable
	 */
	private class ViewLine {
		private long idx;
		ViewLine(long linesModelIdx) {
			this.idx=idx;
		}
		
		/** Calculates the relative position of the cursor if
		 * it is positioned on the viewX char of the line.
		 * Takes into account tabs and multi line display
		 * @param relX logical cursor position in line from ViewCursorPosition
		 * @return ScreenCursorPosition
		 */
		public ScreenCursorPosition getScreenPos(long viewX) {
			final long screenX=getDisplayX(viewX);
			return new ScreenCursorPosition(screenX%sizeX, screenX/sizeX);
		}

		/**
		 * @return the number of lines on screen needed to display that line of the model
		 */
		public long getNumScreenLines() {
			return (getDisplayLineLength()/sizeX)+1;
		}
		
		/** Takes tabulators into account.
		 * @return the displayed length of the line, can differ from the number of chars
		 */
		public long getDisplayLineLength() {
			return getDisplayX(Long.MAX_VALUE);
		}

		/**
		 * @param logicalX
		 * @return
		 */
		public long getDisplayX(final long logicalX) {
			long pos=0;
			final String line=getLine();
			for(int cidx=0; cidx<logicalX && cidx<line.length(); cidx++) {
				if(cidx=='\t')
					pos=calcTabPos(pos);
			}
			return pos;
		}
		
		String getLine() {
			return linesModel.get(idx);
		}
		
		private final static long TAB_SIZE=4;
		
		/**
		 * @param pos x position of cursor
		 * @return x position of cursor if tab added
		 */
		private long calcTabPos(long pos) {
			while((pos++%4)!=0);
			return pos;
		}
	}
	
	/** Window size in lines */
	private final int sizeX;
	private final int sizeY;
	
	/** Listeners */
	private final Set<FirstLineChangedEventListener> flceListeners=new HashSet<FirstLineChangedEventListener>();
	
	public SimpleViewModelImpl(final int sizeX, final int sizeY, final LinesModel linesModel) {
		this.linesModel=linesModel;
		this.sizeX=sizeX;
		this.sizeY=sizeY;
	}

	public void setFirstLine(final long firstLine) {
		if(firstLine>=0 && firstLine <linesModel.getSize() && this.firstLine!=firstLine) {
			this.firstLine=firstLine;
			fireFirstLineChanged(firstLine);
		}
	}

	public boolean scrollUp(final long scrollUpLines) {
		if(scrollUpLines>firstLine) // scroll up before first line not possible
			return false;

		if((-scrollUpLines)+firstLine>linesModel.getSize()) // scroll down after last line not possible
			return false;
		
		setFirstLine(firstLine-scrollUpLines);
		return true;
	}

	public void addFirstLineChangedEventListener(final FirstLineChangedEventListener listener) {
		flceListeners.add(listener);
	}

	protected void fireFirstLineChanged(final long newIdx) {
		fireFirstLineChanged(new FirstLineChangedEvent() {
			public long getFirstVisibleLine() {
				return newIdx;
			}
		});
	}
	protected void fireFirstLineChanged(final FirstLineChangedEvent evt) {
		for(FirstLineChangedEventListener listener : flceListeners)
			listener.firstLineChanged(evt);
	}

	public DataCursorPosition getDataPositionFromViewPosition(ViewCursorPosition cpos) {
		// TODO take into account that long model lines are displayed in more than one screen line
		// Tabulators occupy more than one column can be ignored here
		return new DataCursorPosition(cpos.getX(), cpos.getY()+firstLine);
	}

	public ScreenCursorPosition getScreenPositionFromViewPosition(ViewCursorPosition cpos) {
		// TODO take into account that long model lines are displayed in more than one screen line
		// and that tabulators occupy more than one column
		
		// calculate the number of lines of all lines above the current line
		long screenY=0;
		for(long idx=0; idx<cpos.getY(); idx++) {
			ViewLine viewLine=new ViewLine(idx+firstLine);
			screenY+=viewLine.getNumScreenLines();
		}
		
		// add the line postion of the current line
		final ViewLine viewLine=new ViewLine(cpos.getY()+firstLine);
		ScreenCursorPosition relPos=viewLine.getScreenPos(cpos.getX());
		screenY+=relPos.getY();

		return new ScreenCursorPosition(relPos.getX(), screenY+relPos.getY());
	}

}
