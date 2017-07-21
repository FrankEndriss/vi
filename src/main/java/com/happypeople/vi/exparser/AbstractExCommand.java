package com.happypeople.vi.exparser;

import com.happypeople.vi.EditContext;

public abstract class AbstractExCommand implements ExCommand {

	@Override
	public boolean canUndo() {
		return false;
	}

	@Override
	public void undo(final EditContext editContext) {
		throw new IllegalStateException("undo() not possible");
	}
}
