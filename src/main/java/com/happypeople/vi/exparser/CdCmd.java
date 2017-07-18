package com.happypeople.vi.exparser;

import com.happypeople.vi.EditContext;

public class CdCmd extends AbstractExCommand {

	private boolean exclamated;
	private String path;

	public CdCmd(boolean exclamated, String path) {
		this.exclamated=exclamated;
		this.path=path;
	}

	@Override
	public void execute(EditContext editContext) {
		throw new RuntimeException("not implemented yet");
	}

}
