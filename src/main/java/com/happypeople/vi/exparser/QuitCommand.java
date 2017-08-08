package com.happypeople.vi.exparser;

import com.happypeople.vi.EditContext;

/** End an EditContext
 */
public class QuitCommand extends AbstractExCommand {
	private final boolean exclamated;
	public QuitCommand(final boolean exclamated) {
		this.exclamated=true;
	}
	@Override
	public int execute(final EditContext editContext) {
		editContext.close();
		return ExCommand.SUCCESS;
	}
}
