package com.happypeople.vi;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.happypeople.vi.View.ViewSizeChangedEvent;

/** Simple implementation.
 */
@Component
@Scope("prototype")
public class SimpleViewModelImpl implements ViewModel {
	private final static Logger log=LoggerFactory.getLogger(SimpleViewModelImpl.class);

	/** line data source */
	private final LinesModel linesModel;

	/** display target */
	private final ScreenModel screenModel;

	/** Index of first line of linesModel displayed in window */
	private long firstLine=0;

	/** Window size in cols/lines */
	private int sizeX=1;
	private int sizeY=1;

	/** Listeners */
	private final Set<ViewModelChangedEventListener> flceListeners=new HashSet<>();

	public SimpleViewModelImpl(final LinesModel linesModel, final ScreenModel screenModel) {
		this.linesModel=linesModel;
		this.screenModel=screenModel;
	}

	private void setScreenSize(final int x, final int y) {
		log.info("setScreenSize: "+x+" x "+y);
		if(x!=sizeX || y!=sizeY) {
			this.sizeX=x;
			this.sizeY=y;

			screenModel.clear();
			screenModel.setSizeX(x);
			long idx=firstLine;
			while(screenModel.getScreenLineCount()<sizeY && idx<linesModel.getSize()) {
				final String line=linesModel.get(idx);
				log.info("adding line to screenModel: "+line);
				screenModel.insertBottom(linesModel.get(idx));
				idx++;
			}
		}
	}

	@Override
	public void setFirstLine(final long firstLine) {
		log.info("try setting first line idx: "+firstLine);
		if(firstLine>=0 && firstLine <linesModel.getSize() && this.firstLine!=firstLine) {
			screenModel.clear();
			this.firstLine=firstLine;
			long idx=firstLine;
			while(screenModel.getScreenLineCount()<sizeY && idx<linesModel.getSize())
				screenModel.insertBottom(linesModel.get(idx++));

			// TODO fire other event, firstLineChanged is semantically not sufficient
			fireFirstLineChanged(firstLine);
		}
	}

	@Override
	public boolean scrollUp(final long scrollUpLines) {
		if(scrollUpLines==0)
			return true;

		if(scrollUpLines>firstLine) // scroll up before first line not possible
			return false;

		if((-scrollUpLines)+firstLine>linesModel.getSize()) // scroll down after last line not possible
			return false;

		if(scrollUpLines>0) {
			for(long i=0; i<scrollUpLines; i++) {
				screenModel.insertTop(linesModel.get(firstLine-i-1));
				while(screenModel.getScreenLineCount()>sizeY && screenModel.getDataLineCount()>1)
					screenModel.removeBottom();
			}
		} else {
			long bottomLineIdx=firstLine+screenModel.getDataLineCount()-1;
			for(long i=0; i<-scrollUpLines; i++) {
				screenModel.insertBottom(linesModel.get(bottomLineIdx+1));
				while(screenModel.getScreenLineCount()>sizeY && screenModel.getDataLineCount()>1)
					screenModel.removeTop();
				bottomLineIdx++;
			}
		}

		firstLine-=scrollUpLines;
		// TODO fire other event, firstLineChanged is semantically not sufficient
		fireFirstLineChanged(firstLine);
		return true;
	}

	@Override
	public void addFirstLineChangedEventListener(final ViewModelChangedEventListener listener) {
		flceListeners.add(listener);
	}

	protected void fireFirstLineChanged(final long newIdx) {
		final ViewModelChangedEvent evt=new ViewModelChangedEvent(newIdx);
		for(final ViewModelChangedEventListener listener : flceListeners)
			listener.viewModelChanged(evt);
	}

	/*
	@Override
	public DataCursorPosition getDataPositionFromViewPosition(final ViewCursorPosition cpos) {
		// TODO take into account that long model lines are displayed in more than one screen line
		// Tabulators occupy more than one column can be ignored here
		return new DataCursorPosition(cpos.getX(), cpos.getY()+firstLine);
	}
	 */

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

		return ScreenCursorPosition.ORIGIN.addX(relPos.getX()).addY(screenY+relPos.getY());
	}

	@Override
	public void viewSizeChanged(final ViewSizeChangedEvent evt) {
		setScreenSize(evt.getSizeX(), evt.getSizeY());
	}

	@Override
	public long getMaxLogicalScreenLineIdx() {
		return screenModel.getDataLineCount()-1;
	}

	@Override
	public void cursorPositionChanged(final DataCursorPosition pos) {
		// TODO check if pos is prior to first line
		// then scroll up
		// or if pos is after last line
		// then scroll down

		final ViewCursorPosition vPos=ViewCursorPosition.ORIGIN.setX(pos.getX()).setY(pos.getY()-firstLine);
		screenModel.cursorPositionChanged(vPos);
	}

	@Override
	public DataCursorPosition moveCursorUp(final DataCursorPosition oldPos, final int lines) {
		// TODO take into account TABs
		// that is let the screenModel calc the screenCursorPosition, then move up on screen,
		// then calc back the dataCursorPosition from that screen position.

		final DataCursorPosition newPos=oldPos.addY(-lines);

		if(newPos.getY()<0 || newPos.getY()>=linesModel.getSize())
			return oldPos;

		if(lines>0 && newPos.getY()<firstLine)  { // need to scroll up
			if(!scrollUp(firstLine-newPos.getY()))
				return oldPos;
		} else if(lines<0) {
			final long idxOfLastLineOnScreen=firstLine+screenModel.getDataLineCount()-1;
			if(newPos.getY()>idxOfLastLineOnScreen) {
				if(!scrollUp(idxOfLastLineOnScreen-newPos.getY()))
					return oldPos;
			}
		}

		cursorPositionChanged(newPos);
		return newPos;
	}

	@Override
	public void forceRepaint() {
		screenModel.forceRepaint();
	}

}
