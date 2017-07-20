package com.happypeople.vi.exparser;

import java.util.List;

import com.happypeople.vi.EditContext;

public class CopyCmd extends AbstractExCommand {
	private final List<ExAddress> addr2;
	private final ExAddress targetAddr;

	public CopyCmd(List<ExAddress> addr2, ExAddress targetAddr) {
		this.addr2=addr2;
		this.targetAddr=targetAddr;
	}

	@Override
	public void execute(EditContext editContext) {
		throw new RuntimeException("not implemented yet");
	}

}
