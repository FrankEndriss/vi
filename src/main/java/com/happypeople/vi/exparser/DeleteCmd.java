package com.happypeople.vi.exparser;

import java.util.List;

import com.happypeople.vi.EditContext;

public class DeleteCmd extends AbstractExCommand {

	private List<ExAddress> addresses;
	private BufferRef bufferRef;
	private String extraAddress;

	public DeleteCmd(List<ExAddress> addressList, BufferRef bufferRef, String extraAddress) {
		this.addresses=addressList;
		this.bufferRef=bufferRef;
		this.extraAddress=extraAddress;
	}

	@Override
	public void execute(EditContext editContext) {

	}

}
