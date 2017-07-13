package com.happypeople.vi.linesModel;

import java.util.HashSet;
import java.util.Set;

import com.happypeople.vi.LinesModel;
import com.happypeople.vi.LinesModelChangedEvent;
import com.happypeople.vi.LinesModelChangedEventListener;

/** Base implementation of LinesModel with change event support
 */
public abstract class AbstracLinesModelImpl implements LinesModel {

	private final Set<LinesModelChangedEventListener> listeners = new HashSet<LinesModelChangedEventListener>();

	public AbstracLinesModelImpl() {
		super();
	}

	@Override
	public void addLinesModelChangedEventListener(LinesModelChangedEventListener listener) {
		listeners.add(listener);
	}

	protected void fireChange(final LinesModelChangedEvent evt) {
		for(LinesModelChangedEventListener listener : listeners) {
			listener.changedEvent(evt);
		}
	}

}