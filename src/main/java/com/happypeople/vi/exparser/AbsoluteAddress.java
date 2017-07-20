package com.happypeople.vi.exparser;

import com.happypeople.vi.EditContext;

public class AbsoluteAddress implements Address {

	private long line;

	public AbsoluteAddress(long line) {
		this.line=line;
	}

	@Override
	public long resolve(EditContext context) {
		return line;
	}

}
