package com.happypeople.vi.exparser;

import com.happypeople.vi.EditContext;

public class UnabbreviationCmd extends AbstractExCommand {

	private final String lhs;
	
	public UnabbreviationCmd(final String lhs) {
		this.lhs=lhs;
	}

	@Override
	public void execute(EditContext editContext) {
		throw new RuntimeException("not implemented yet");
	}

}
