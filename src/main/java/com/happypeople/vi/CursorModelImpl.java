package com.happypeople.vi;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.happypeople.vi.View.ViewSizeChangedEvent;

@Component
@Scope("prototype")
public class CursorModelImpl implements CursorModel {
	//private final Set<CursorPositionChangedEventListener> cpceListeners=new HashSet<>();

	private DataCursorPosition cursorPos=DataCursorPosition.ORIGIN;

	private int sizeX=0;
	private int sizeY=0;

	private final LinesModel linesModel;
	private final ViewModel viewModel;

	public CursorModelImpl(final LinesModel linesModel, final ViewModel viewModel) {
		this.linesModel=linesModel;
		this.viewModel=viewModel;
	}

	@Override
	public void moveCursorToScreenPosition(final int posX, final int posY) {
		throw new RuntimeException("not implemented yet");
		/*
		this.cPosX=posX;
		this.cPosY=posY>=sizeY?sizeY-1:posY;
		fireCursorPosition(cPosX, cPosY);
		*/
	}

	@Override
	public void moveCursorUp(final int lines) {
		cursorPos=viewModel.moveCursorUp(cursorPos, lines);
		/*
		// number of lines we need to scroll the window up
		if(lines<0) {
			moveCursorDown(-lines);
			return;
		}

		final long scrollUpLines=lines>cursorPos.getY()?lines-cursorPos.getY():0;
		if(scrollUpLines>0) {
			final boolean success=viewModel.scrollUp(scrollUpLines);
			if(!success) // TODO show error message like "line out of range..."
				return;
		}
		final long actualLines=lines-scrollUpLines; // actual movement of cursor, might be 0

		if(actualLines!=0) {
			long newY=cursorPos.getY()-actualLines;
			if(newY>=sizeY)
				newY=sizeY-1;

			cursorPos=cursorPos.setY(newY);
			// Note that we do not alter cPosX, in spite of that the
			// line at cPosY could be shorter than the old line, ie
			// the cursor can not be displayed at position cPosX because
			// the line is simply not that long.
			// This behavior is implemented in the renderer, which displays
			// the cursor in this case at the end of the line.
			fireCursorPosition(cursorPos);
		}
	}

	private void moveCursorDown(final int lines) {
		if(lines<0) {
			moveCursorUp(-lines);
			return;
		}

		// Is the cursor on the last line of the LinesModel?
		// -> no cursor down possible
		final DataCursorPosition dataCursorPos=viewModel.getDataPositionFromViewPosition(cursorPos);
		if(dataCursorPos.getY()+lines>=linesModel.getSize())
			return;

		// TODO Things to check:
		// *Is the cursor on the last line of the ViewModel?
		//   -> need to scroll view down
		final ViewCursorPosition newPos=cursorPos.addY(lines);	// new position
		final long idxOfLastVisibleLine=viewModel.getMaxLogicalScreenLineIdx();
		if(newPos.getY()>idxOfLastVisibleLine) {
			if(!viewModel.scrollUp(idxOfLastVisibleLine-newPos.getY()))
				return; // not possible to do the move/scroll
		}

		cursorPos=newPos;
		viewModel.dataCursorPositionChanged(cursorPos);
		*/
	}

	/* We need to make sure that the cursor position does not move "out-of-range",
	 * this is not left of the beginning of the line, and not right of the end of the line.
	 * @see com.happypeople.vi.CursorModel#moveCursorLeft(int)
	 */
	@Override
	public void moveCursorLeft(final int chars) {
		long newPosX=cursorPos.getX()-chars;
		if(newPosX<0)
			newPosX=0;
		else { // check if right of end of line
			final String line=linesModel.get(cursorPos.getY());
			System.out.println("line under cursor: "+line);
			if(newPosX>line.length()-1)
				newPosX=line.length()-1;
			if(newPosX<0)
				newPosX=0;
		}
		cursorPos=cursorPos.setX(newPosX);
		viewModel.cursorPositionChanged(cursorPos);
	}

	/*
	@Override
	public void addCursorPositionChangedEventListener(final CursorPositionChangedEventListener listener) {
		cpceListeners.add(listener);
	}

	protected void fireCursorPosition(final ViewCursorPosition pos) {
		fireCursorPosition(new CursorPositionChangedEvent() {
			@Override
			public ViewCursorPosition getCursorPosition() {
				return pos;
			}
		});
	}

	protected void fireCursorPosition(final CursorPositionChangedEvent evt) {
		for(final CursorPositionChangedEventListener listener : cpceListeners)
			listener.cursorPositionChanged(evt);
	}
	*/

	@Override
	public void viewSizeChanged(final ViewSizeChangedEvent evt) {
		this.sizeX=evt.getSizeX();
		this.sizeY=evt.getSizeY();
		// TODO take care if the cursor is still on screen
		viewModel.viewSizeChanged(evt);
	}
}
