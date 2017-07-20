package com.happypeople.vi.exparser;

import com.happypeople.vi.EditContext;

public class AbbreviationCmd extends AbstractExCommand {

	private final String lhs;
	private final String rhs;

	public AbbreviationCmd(final String lhs, final String rhs) {
		this.lhs=lhs;
		this.rhs=rhs;
		if((lhs==null && rhs!=null) || (rhs==null && lhs!=null))
			throw new IllegalArgumentException("both or none arg must be null or not null");
	}

	@Override
	public void execute(EditContext editContext) {
		// TODO store the mapping in editContext
		throw new RuntimeException("not implemented yet");
	}

}
