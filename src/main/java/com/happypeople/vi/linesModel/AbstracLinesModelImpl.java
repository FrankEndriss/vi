package com.happypeople.vi.linesModel;

import java.util.HashSet;
import java.util.Set;

import com.happypeople.vi.LinesModelChangedEvent;
import com.happypeople.vi.LinesModelChangedEventListener;
import com.happypeople.vi.LinesModelEditor;

/** Base implementation of LinesModel with change event support
 */
public abstract class AbstracLinesModelImpl implements LinesModelEditor {

	private final Set<LinesModelChangedEventListener> listeners = new HashSet<>();

	public AbstracLinesModelImpl() {
		super();
	}

	@Override
	public void addLinesModelChangedEventListener(final LinesModelChangedEventListener listener) {
		listeners.add(listener);
	}

	protected void fireChange(final LinesModelChangedEvent evt) {
		for(final LinesModelChangedEventListener listener : listeners) {
			listener.changedEvent(evt);
		}
	}

}