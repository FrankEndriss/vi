package com.happypeople.vi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Implementation of the mapping of a logical line
 * to the display of that line on screen.
 * TODO: make editable/dynamic
 */
class ScreenLine {
	private final static Logger log=LoggerFactory.getLogger(ScreenLine.class);

	private final String line;
	private int screenSizeX;

	/** TODO use a LineReference instead of a String for
	 * the first argument. That could make it
	 * more dynamic while changes of the line.
	 * @param line the string to display when displaying the line, ie the content of the line
	 * @param screenSizeX with of the screen in columns
	 */
	ScreenLine(final String line, final int screenSizeX) {
		this.line=line;
		this.screenSizeX=screenSizeX;
	}

	/** Resets the size of the screen in X dimension.
	 * @param sizeX the new width of the view, number of columns
	 */
	public void setScreenSizeX(final int sizeX) {
		this.screenSizeX=sizeX;
	}

	/** Calculates the relative position of the cursor if
	 * it is positioned on the viewX char of the line.
	 * Takes into account tabs and multi line display.
	 * @param logicalX logical cursor position in line from ViewCursorPosition
	 * @return ScreenCursorPosition with (0,0) at beginning of the line
	 */
	public ScreenCursorPosition getScreenPos(final long logicalX) {
		final long displayX=getDisplayX(logicalX);
		//log.debug("in getScreenPos(), displayX="+displayX+" screenSizeX="+screenSizeX);
		return ScreenCursorPosition.ORIGIN.addX(displayX%screenSizeX).addY(displayX/screenSizeX);
	}

	/**
	 * @return the number of lines on screen needed to display that line of the model.
	 * Cannot be less than 1 since even an empty line needs one line on screen to be
	 * displayed.
	 */
	public long getNumScreenLines() {
		return (getDisplayLineLength()/screenSizeX)+1;
	}

	/** Takes tabulators into account.
	 * TODO make calculation stop at some point for very long lines, since
	 *      lines bigger than the whole screen never get displayed.
	 * @return the displayed length of the line, can differ from the number of chars
	 */
	public long getDisplayLineLength() {
		return getDisplayX(Long.MAX_VALUE);
	}

	/** Calculates the display position of a char of a line.
	 * This position is not equal to the position of the char
	 * in the string since some chars are displayed using more
	 * than one space. ie TAB, or other special chars.
	 * @param logicalX the position of the char in the lines String
	 * @return The position of the char on display, without considering linebreaks.
	 */
	public long getDisplayX(final long logicalX) {
		if(getLine().length()==0)
			return 0;
		long pos=-1;
		final String line=getLine();
		for(int cidx=0; cidx<=logicalX && cidx<line.length(); cidx++) {
			if(line.charAt(cidx)=='\t')
				pos=calcTabPos(pos<0?0:pos);
			else
				pos++;
		}
		return pos;
	}

	/** @return the line converted to display. That does include
	 * only printable chars which print one-to-one on screen.
	 * The conversion of the line stops at lengthLimit
	 */
	public String render(final int lengthLimit) {
		final String line=getLine();
		final StringBuilder sb=new StringBuilder();
		long pos=0;
		for(final char c : line.toCharArray()) {
			if(c=='\t') {
				pos=calcTabPos(pos);
				do {
					sb.append(' ');
				} while(sb.length()<=pos);
			} else { // TODO do something with non printable chars
				pos++;
				sb.append(c);
			}
			if(pos>=lengthLimit)
				break;
		}
		return sb.toString();
	}

	/** @return Gets the line as given in the constructor */
	private String getLine() {
		return line;
	}

	private final static long TAB_SIZE=4;

	/** Calculates the next tab ending position starting at pos.
	 * So this is, if there is a TAB at position pos, this
	 * method returns the position where the cursor blinks.
	 * 0:3, 1:3, 2:3, 3:3, 4:7, ...
	 * @param pos x position of cursor
	 * @return x position of cursor if tab added
	 */
	public long calcTabPos(long pos) {
		while(((pos+1)%TAB_SIZE)!=0)
			pos++;
		return pos;
	}
}
