package com.happypeople.vi;

import com.happypeople.vi.LinesModel.LinesModelChangedEvent;

public interface LinesModelChangedEventListener {
	void changedEvent(LinesModelChangedEvent evt);
}