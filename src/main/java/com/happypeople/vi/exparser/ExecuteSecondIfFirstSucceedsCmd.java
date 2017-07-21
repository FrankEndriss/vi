package com.happypeople.vi.exparser;

import com.happypeople.vi.EditContext;

public class ExecuteSecondIfFirstSucceedsCmd extends AbstractExCommand {
	private final ExCommand first;
	private final ExCommand second;

	public ExecuteSecondIfFirstSucceedsCmd(final ExCommand first, final ExCommand second) {
		this.first=first;
		this.second=second;
		if(first==null || second==null)
			throw new IllegalArgumentException("null args do not make sense here");
	}

	@Override
	public int execute(final EditContext editContext) {
		final int retcode=first.execute(editContext);
		if(retcode==ExCommand.SUCCESS)
			return second.execute(editContext);

		return retcode;
	}

}
