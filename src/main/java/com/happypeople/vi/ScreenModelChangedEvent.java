package com.happypeople.vi;

public class ScreenModelChangedEvent {

	private final ScreenModel source;

	public ScreenModelChangedEvent(final ScreenModel source) {
		this.source=source;
	}

	public ScreenModel getSource() {
		return source;
	}
}