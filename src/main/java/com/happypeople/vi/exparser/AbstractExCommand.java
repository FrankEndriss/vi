package com.happypeople.vi.exparser;

import com.happypeople.vi.CursorModel;
import com.happypeople.vi.EditContext;
import com.happypeople.vi.LinesModelEditor;
import com.happypeople.vi.MessageTarget;

public abstract class AbstractExCommand implements ExCommand {

	@Override
	public boolean canUndo() {
		return false;
	}

	@Override
	public void undo(EditContext editContext) {
		throw new IllegalStateException("undo not possible");
	}

}
