package com.happypeople.vi;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.list.TreeList;

/** Implementation of LinesModel based on apache commons TreeList
 */
public class LinesModelImpl implements LinesModelEditor {
	private final TreeList<String> content=new TreeList<String>();
	private final Set<LinesModelChangedEventListener> listeners=new HashSet<LinesModelChangedEventListener>();

//	@Override
	public long getSize() {
		return content.size();
	}

//	@Override
	public String get(long lineNo) {
		return content.get((int)lineNo);
	}

//	@Override
	public void replace(final long lineNo, final String newVersionOfLine) {
		content.set((int)lineNo, newVersionOfLine);
		fireChange(new MyLinesModelChangedEvent(lineNo, LinesModelChangeType.CHANGE));
	}

//	@Override
	public void insertAfter(final long lineNo, final String newLine) {
		content.add((int)lineNo+1, newLine);
		fireChange(new MyLinesModelChangedEvent(lineNo+1, LinesModelChangeType.INSERT));
	}

//	@Override
	public void insertBefore(final long lineNo, String newLine) {
		content.add((int)lineNo, newLine);
		fireChange(new MyLinesModelChangedEvent(lineNo, LinesModelChangeType.INSERT));
	}

//	@Override
	public void remove(final long lineNo) {
		content.remove((int)lineNo);
		fireChange(new MyLinesModelChangedEvent(lineNo, LinesModelChangeType.REMOVE));
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
	
	/** Event implementation used in this implementation of the LinesModel
	 */
	private static class MyLinesModelChangedEvent implements LinesModelChangedEvent {
		private final long lineNo;
		private final LinesModelChangeType changeType;

		MyLinesModelChangedEvent(long lineNo, LinesModelChangeType changeType) {
			this.lineNo=lineNo;
			this.changeType=changeType;
		}

		public long getLineNo() {
			return lineNo;
		}

		public LinesModelChangeType getChangeType() {
			return changeType;
		}
	}
}
