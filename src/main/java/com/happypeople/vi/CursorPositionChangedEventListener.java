package com.happypeople.vi;

import com.happypeople.vi.CursorModel.CursorPositionChangedEvent;

public interface CursorPositionChangedEventListener {
	void cursorPositionChanged(CursorPositionChangedEvent evt);
}