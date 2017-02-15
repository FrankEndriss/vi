It should be possible to define and implement a
ViModel, which models all the state existing in an instance of vi.
For a first throw one File should be viewable.
i.E:

interface ViewModel {
	/** returns the index of the windows first visible line
	* (the line to display at top of the view).
	*/
	int getFirstVisibleLine();

	/** Cursor position within the visible window. */
	int getCursorPosX();
	int getCursorPosY();
	
	/** scroll the whole window */
	interface FirstLineChangedEvent extends ViewModelChangeEvent {
		int getFirstVisibleLine();
	}
	interface FirstLineChangedEventListener {
		void firstLineChanged(FirstLineChangedEvent evt)
	}
	void addFirstLineChangedEventListener(FirstLineChangedEventListener listener);

	/** change cursor position */
	interface CursorPositionChangedEvent extends ViewModelChangeEvent {
		int getX();
		int getY();
	}
	interface CursorPositionChangedEventListener {
		void cursorPositionChanged(CursorPositionChangedEvent evt);
	}
	void addCursorPositionChangedEventListener(CursorPositionChangedEventListener listener);
}

interface ControllerModel {
	enum Mode {
		/** hit <Esc> to enter vi mode */
		VI_MODE,
		/** enter ":" to enter command mode */
		COMMAND_MODE,
		/** use "a", "i" or the like to switch to input mode from vi mode. */
		INPUT_MODE
	}


	interface ModeChangedEvent {
		Mode getMode();
	}
	interface ModeChangedEventListener {
		void modeChanged(ModeChangedEvent evt);
	}
	void addModeChangedEventListener(ModeChangedEventListener listener);
	
	
	/** user typed a key */
	void processInput(int keyCode);
}

interface LinesModel {
	/** @return Number of available lines */
	long size();

	/** gets the line, lineNo starting at 0 */
	CharSequence get(long lineNo);

	/** replace content of line, edit */
	void replace(long lineNo, CharSequence @NotNull line);

	/** create new line */
	void insertAfter(long lineNo, CharSequence @NotNull newLine);
	void insertBefore(long lineNo, CharSequence @NotNull newLine);

	/** remove a line */
	void remove(long lineNo);
}

interface LinesModelBuilder {
	LinesModel createEmpty();
	LinesModel load(Reader in);
}
