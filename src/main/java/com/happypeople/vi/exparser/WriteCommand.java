package com.happypeople.vi.exparser;

import com.happypeople.vi.EditContext;

public class WriteCommand extends AbstractExCommand {
	private final boolean exclam;
	private final boolean append;
	private final String filename;

	public WriteCommand(final boolean exclam, final boolean append, final String filename) {
		this.exclam=exclam;
		this.append=append;
		this.filename=filename;
	}

	@Override
	public int execute(final EditContext editContext) {
		throw new RuntimeException("not implemented yet");
	}
}
