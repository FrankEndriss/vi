package com.happypeople.vi;

public class LinesModelChangedEvent {
	private long lineNo;
	private LinesModelChangeType changeType;

	public enum LinesModelChangeType {
		/** The line was inserted */
		INSERT,
		/** The line was changed. */
		CHANGE,
		/** The line was removed. */
		REMOVE
	}

	public LinesModelChangedEvent(long lineNo, LinesModelChangeType changeType) {
		this.lineNo=lineNo;
		this.changeType=changeType;
	}

	public long getLineNo() {
		return lineNo;
	};

	public LinesModelChangedEvent.LinesModelChangeType getChangeType() {
		return changeType;
	}
}