package com.happypeople.vi.linesModel;

import org.apache.commons.collections4.list.TreeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.happypeople.vi.LinesModelChangedEvent;
import com.happypeople.vi.LinesModelEditor;

/** Implementation of LinesModel based on apache commons TreeList
 * in memory only.
 */
@Component
@Scope("prototype")
public class InMemoryLinesModelImpl extends AbstracLinesModelImpl implements LinesModelEditor {
	private final static Logger log=LoggerFactory.getLogger(InMemoryLinesModelImpl.class);

	private final TreeList<String> content=new TreeList<String>();
	@Override
	public long getSize() {
		return content.size();
	}

	@Override
	public String get(long lineNo) {
		return content.get((int)lineNo);
	}

	@Override
	public void replace(final long lineNo, final String newVersionOfLine) {
		content.set((int)lineNo, newVersionOfLine);
		fireChange(new LinesModelChangedEvent(lineNo, LinesModelChangedEvent.LinesModelChangeType.CHANGE));
	}

	@Override
	public void insertAfter(final long lineNo, final String newLine) {
		content.add((int)lineNo+1, newLine);
		fireChange(new LinesModelChangedEvent(lineNo+1, LinesModelChangedEvent.LinesModelChangeType.INSERT));
	}

	@Override
	public void insertBefore(final long lineNo, String newLine) {
		content.add((int)lineNo, newLine);
		fireChange(new LinesModelChangedEvent(lineNo, LinesModelChangedEvent.LinesModelChangeType.INSERT));
	}

	@Override
	public void remove(final long lineNo) {
		content.remove((int)lineNo);
		fireChange(new LinesModelChangedEvent(lineNo, LinesModelChangedEvent.LinesModelChangeType.REMOVE));
	}
	
}
