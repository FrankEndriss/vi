package com.happypeople.vi.exparser;

import java.util.List;

import com.happypeople.vi.EditContext;

public class DeleteCmd extends AbstractExCommand {

	private List<ExAddress> addresses;
	private BufferRef bufferRef;
	private long extraAddress;

	public DeleteCmd(List<ExAddress> addressList, BufferRef bufferRef, long extraAddress) {
		this.addresses=addressList;
		this.bufferRef=bufferRef;
		this.extraAddress=extraAddress;
	}

	@Override
	public void execute(EditContext editContext) {
		throw new RuntimeException("not implemented yet");
	}

}
