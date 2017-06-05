package com.happypeople.vi;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.list.TreeList;

/** Implementation of LinesModel based on apache commons TreeList
 */
public class LinesModelImpl implements LinesModel {
	private final TreeList<String> content=new TreeList<String>();
	private final Set<LinesModelChangedEventListener> listeners=new HashSet<LinesModelChangedEventListener>();

//	@Override
	public long getSize() {
		return content.size();
	}

//	@Override
	public String get(long lineNo) {
		// TODO check index
		return content.get((int)lineNo);
	}

//	@Override
	public void replace(final long lineNo, final String newVersionOfLine) {
		// TODO check index
		content.set((int)lineNo, newVersionOfLine);
		fireChange(new LinesModelLineChangedEvent() {
			public long getLineNo() {
				return lineNo;
			}
		});
	}

//	@Override
	public void insertAfter(final long lineNo, final String newLine) {
		// TODO check index
		content.add((int)lineNo+1, newLine);
		fireChange(new LinesModelLineInsertedEvent() {
			public long getLineNo() {
				return lineNo;
			}
		});
	}

//	@Override
	public void insertBefore(final long lineNo, String newLine) {
		// TODO check index
		content.add((int)lineNo, newLine);
		fireChange(new LinesModelLineInsertedEvent() {
			public long getLineNo() {
				return lineNo;
			}
		});
	}

//	@Override
	public void remove(final long lineNo) {
		content.remove((int)lineNo);
		// TODO check index
		fireChange(new LinesModelLineRemovedEvent() {
			public long getLineNo() {
				return lineNo;
			}
		});
	}

//	@Override
	public void addLinesModelChangedEventListener(LinesModelChangedEventListener listener) {
		listeners.add(listener);
	}
	
	protected void fireChange(final LinesModelChangedEvent evt) {
		for(LinesModelChangedEventListener listener : listeners) {
			listener.changedEvent(evt);
		}
	}
}
