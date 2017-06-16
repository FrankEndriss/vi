package com.happypeople.vi;

import java.util.HashSet;
import java.util.Set;

public class CursorModelImpl implements CursorModel {
	private final Set<CursorPositionChangedEventListener> cpceListeners=new HashSet<CursorPositionChangedEventListener>();

	private int cPosX;
	private int cPosY;

	// TODO implement as listener of application window and/or renderer
	private int sizeX=80;
	private int sizeY=20;
	
	private final LinesModel linesModel;
	private final ViewModel viewModel;
	
	public CursorModelImpl(LinesModel linesModel, ViewModel viewModel) {
		this.linesModel=linesModel;
		this.viewModel=viewModel;
	}

	public void moveCursorToScreenPosition(final int posX, final int posY) {
		this.cPosX=posX;
		this.cPosY=posY>=sizeY?sizeY-1:posY;
		fireCursorPosition(cPosX, cPosY);
	}

	public void moveCursorUp(final int lines) {
System.out.println("moveCursorUp, lines="+lines);
		// number of lines we need to scroll up
		final int scrollUpLines=lines>cPosY?lines-cPosY:0;
		if(scrollUpLines>0) {
			boolean success=viewModel.scrollUp(scrollUpLines);
			if(!success) // TODO show error message like "line out of range..."
				return;
		}
		final int actualLines=lines-scrollUpLines; // actual movement of cursor, might be 0
		if(actualLines!=0) {
			this.cPosY-=actualLines;
			if(cPosY>=sizeY)
				cPosY=sizeY-1;
		}
		
		// Note that we do not alter cPosX, in spite of that the
		// line at cPosY could be shorter than the old line, ie
		// the cursor can not be displayed at position cPosX because
		// the line is simply not that long.
		// This behaviour is implemented in the renderer, which displays
		// the cursor in this case at the end of the line.
		fireCursorPosition(cPosX, cPosY);
	}

	/* We need to make sure that the cursor position does not move "out-of-range",
	 * this is not left of the beginning of the line, and not right of the end of the line.
	 * @see com.happypeople.vi.CursorModel#moveCursorLeft(int)
	 */
	public void moveCursorLeft(final int chars) {
		int newPosX=cPosX-chars;
		if(newPosX<0)
			newPosX=0;
		else {
			long[] modelPos=new long[2];
			viewModel.getModelPositionFromCursorPosition(newPosX, cPosY, modelPos);
			final String line=linesModel.get(modelPos[1]);
			System.out.println("line under cursor: "+line);
			if(newPosX>line.length()-1)
				newPosX=line.length()-1;
			if(newPosX<0)
				newPosX=0;
		}
		
		cPosX=newPosX;
		fireCursorPosition(cPosX, cPosY);
	}
	
	public void addCursorPositionChangedEventListener(CursorPositionChangedEventListener listener) {
		cpceListeners.add(listener);
	}

	protected void fireCursorPosition(final int lX, final int lY) {
		fireCursorPosition(new CursorPositionChangedEvent() {
			public int getScreenX() {
				return lX;
			}

			public int getScreenY() {
				return lY;
			}
		});
	}

	protected void fireCursorPosition(CursorPositionChangedEvent evt) {
		for(CursorPositionChangedEventListener listener : cpceListeners)
			listener.cursorPositionChanged(evt);
	}
}
