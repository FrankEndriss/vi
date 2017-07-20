package com.happypeople.vi.exparser;

import com.happypeople.vi.EditContext;

/** The line addressed by ".", the current line */
public class DotAddress implements SimpleAddress {

	@Override
	public long resolve(EditContext context) {
		throw new RuntimeException("not implemented yet");
	}

}
