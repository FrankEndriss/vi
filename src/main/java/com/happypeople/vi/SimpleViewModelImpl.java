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

	/** display target */
	private final ScreenModel screenModel;

	/** Index of first line of linesModel displayed in window */
	private long firstLine=0;

	/** Window size in lines */
	private final int sizeX;
	private final int sizeY;

	/** Listeners */
	private final Set<FirstLineChangedEventListener> flceListeners=new HashSet<>();

	public SimpleViewModelImpl(final int sizeX, final int sizeY, final LinesModel linesModel, final ScreenModel screenModel) {
		this.linesModel=linesModel;
		this.screenModel=screenModel;
		this.sizeX=sizeX;
		this.sizeY=sizeY;
		
		// init screenModel
		screenModel.clear();
		long idx=firstLine;
		while(screenModel.getScreenLineCount()<sizeY && idx<linesModel.getSize())
			screenModel.insertBottom(new ScreenLine(linesModel.get(idx++), sizeX));
	}

	@Override
	public void setFirstLine(final long firstLine) {
		if(firstLine>=0 && firstLine <linesModel.getSize() && this.firstLine!=firstLine) {
			screenModel.clear();
			this.firstLine=firstLine;
			long idx=firstLine;
			while(screenModel.getScreenLineCount()<sizeY && idx<linesModel.getSize())
				screenModel.insertBottom(new ScreenLine(linesModel.get(idx++), sizeX));

			// TODO fire other event, firstLineChanged is semantically not sufficient
			fireFirstLineChanged(firstLine);
		}
	}

	@Override
	public boolean scrollUp(final long scrollUpLines) {
		if(scrollUpLines>firstLine) // scroll up before first line not possible
			return false;

		if((-scrollUpLines)+firstLine>linesModel.getSize()) // scroll down after last line not possible
			return false;

		if(scrollUpLines==0)
			return true;

		if(scrollUpLines>0) {
			for(long i=0; i<scrollUpLines; i++) {
				screenModel.insertTop(new ScreenLine(linesModel.get(firstLine-i-1), sizeX));
			}
		} else {
			long bottomLineIdx=firstLine+screenModel.getDataLineCount();
			for(long i=0; i<-scrollUpLines; i++) {
				screenModel.insertBottom(new ScreenLine(linesModel.get(bottomLineIdx), sizeX));
				bottomLineIdx++;
			}
		}

		firstLine-=scrollUpLines;
		// TODO fire other event, firstLineChanged is semantically not sufficient
		fireFirstLineChanged(firstLine);
		return true;
	}

	@Override
	public void addFirstLineChangedEventListener(final FirstLineChangedEventListener listener) {
		flceListeners.add(listener);
	}

	protected void fireFirstLineChanged(final long newIdx) {
		fireFirstLineChanged(new FirstLineChangedEvent() {
			@Override
			public long getFirstVisibleLine() {
				return newIdx;
			}
		});
	}
	protected void fireFirstLineChanged(final FirstLineChangedEvent evt) {
		for(final FirstLineChangedEventListener listener : flceListeners)
			listener.firstLineChanged(evt);
	}

	@Override
	public DataCursorPosition getDataPositionFromViewPosition(final ViewCursorPosition cpos) {
		// TODO take into account that long model lines are displayed in more than one screen line
		// Tabulators occupy more than one column can be ignored here
		return new DataCursorPosition(cpos.getX(), cpos.getY()+firstLine);
	}

	@Override
	public ScreenCursorPosition getScreenPositionFromViewPosition(final ViewCursorPosition cpos) {
		// calculate the number of lines of all lines above the current line
		long screenY=0;
		for(long idx=0; idx<cpos.getY(); idx++) {
			final ScreenLine viewLine=new ScreenLine(linesModel.get(idx+firstLine), sizeX);
			screenY+=viewLine.getNumScreenLines();
		}

		// add the line postion of the current line
		final ScreenLine viewLine=new ScreenLine(linesModel.get(cpos.getY()+firstLine), sizeX);
		final ScreenCursorPosition relPos=viewLine.getScreenPos(cpos.getX());
		screenY+=relPos.getY();

		return new ScreenCursorPosition(relPos.getX(), screenY+relPos.getY());
	}

}
