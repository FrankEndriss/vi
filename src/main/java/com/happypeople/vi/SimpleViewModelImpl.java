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

	/** First line of model displayed in window */
	private long firstLine=0;
	
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

	public boolean scrollUp(final int scrollUpLines) {
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
		return new ScreenCursorPosition(cpos.getX(), cpos.getY());
	}

}
