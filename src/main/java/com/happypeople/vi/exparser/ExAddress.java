package com.happypeople.vi.exparser;

import com.happypeople.vi.EditContext;

/** Aggregation of SimpleAddress and offset */
public class ExAddress implements SimpleAddress {
	public final static SimpleAddress DOT=new DotAddress();

	private SimpleAddress simpleAddress;
	private long offset;

	public ExAddress(SimpleAddress simpleAddress, long offset) {
		this.simpleAddress=simpleAddress;
		this.offset=offset;
	}

	@Override
	public long resolve(EditContext context) {
		return simpleAddress.resolve(context)+offset;
	}

}
