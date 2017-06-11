package com.happypeople.vi;

import com.happypeople.vi.LinesModelEditor.LinesModelChangedEvent;

public interface LinesModelChangedEventListener {
	void changedEvent(LinesModelChangedEvent evt);
}