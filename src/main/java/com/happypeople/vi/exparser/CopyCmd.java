package com.happypeople.vi.exparser;

import java.util.List;

import com.happypeople.vi.EditContext;

public class CopyCmd extends AbstractExCommand {
	private final List<Address> addr2;
	private final Address targetAddr;

	public CopyCmd(final List<Address> addr2, final Address targetAddr) {
		this.addr2=addr2;
		this.targetAddr=targetAddr;
	}

	@Override
	public void execute(final EditContext editContext) {
		throw new RuntimeException("not implemented yet");
	}

}
