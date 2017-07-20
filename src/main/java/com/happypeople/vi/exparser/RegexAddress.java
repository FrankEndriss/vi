package com.happypeople.vi.exparser;

import com.happypeople.vi.EditContext;

/** Line addresses by a regex and search direction */
public class RegexAddress implements Address {
	public enum Dir {
		FORWARD,
		BACKWARD
	}

	private Dir direction;
	private String pattern;

	public RegexAddress(RegexAddress.Dir direction, String pattern) {
		this.direction=direction;
		this.pattern=pattern;
	}

	@Override
	public long resolve(EditContext context) {
		throw new RuntimeException("not implemented yet");
	}

}
