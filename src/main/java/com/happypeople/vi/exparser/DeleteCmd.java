package com.happypeople.vi.exparser;

import java.util.List;

import com.happypeople.vi.EditContext;

public class DeleteCmd extends AbstractExCommand {

	private final List<Address> addresses;
	private final BufferRef bufferRef;
	private final long extraAddress;

	public DeleteCmd(final List<Address> addressList, final BufferRef bufferRef, final long extraAddress) {
		this.addresses=addressList;
		this.bufferRef=bufferRef;
		this.extraAddress=extraAddress;
	}

	@Override
	public int execute(final EditContext editContext) {
		throw new RuntimeException("not implemented yet");
	}

}
