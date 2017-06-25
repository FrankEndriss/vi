package com.happypeople.vi;

/** Implementation of a logical line and the
 * display of that line on screen.
 * TODO: make editable
 */
class ScreenLine {
	private final String line;
	private final int screenSizeX;

	/** TODO use a LineReference instead of a String for
	 * the first argument. That could make it editable, and
	 * hence more dynamic while changes of the line.
	 * @param line the string to display when displaying the line, ie the contents of the line
	 * @param screenSizeX with of the screen in columns
	 */
	ScreenLine(final String line, final int screenSizeX) {
		this.line=line;
		this.screenSizeX=screenSizeX;
	}

	/** Calculates the relative position of the cursor if
	 * it is positioned on the viewX char of the line.
	 * Takes into account tabs and multi line display
	 * @param relX logical cursor position in line from ViewCursorPosition
	 * @return ScreenCursorPosition
	 */
	public ScreenCursorPosition getScreenPos(final long viewX) {
		final long displayX=getDisplayX(viewX);
		return new ScreenCursorPosition(displayX%screenSizeX, displayX/screenSizeX);
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

	/**
	 * @param logicalX
	 * @return
	 */
	public long getDisplayX(final long logicalX) {
		long pos=0;
		final String line=getLine();
		for(int cidx=0; cidx<logicalX && cidx<line.length(); cidx++) {
			if(line.charAt(cidx)=='\t')
				pos=calcTabPos(pos);
			else
				pos++;
		}
		return pos;
	}

	/** @return the line converted to display. That does include
	 * only printable chars which print one-to-one on screen.
	 * The conversion of the line stops at lengthLimit
	 */
	private String render(final int lengthLimit) {
		final String line=getLine();
		final StringBuilder sb=new StringBuilder();
		long pos=0;
		for(final char c : line.toCharArray()) {
			if(c=='\t') {
				pos=calcTabPos(pos);
				while(sb.length()<pos)
					sb.append('\b');
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

	/**
	 * @param pos x position of cursor
	 * @return x position of cursor if tab added
	 */
	private long calcTabPos(long pos) {
		while((pos++%4)!=0);
		return pos;
	}
}