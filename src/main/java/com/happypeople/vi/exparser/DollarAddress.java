package com.happypeople.vi.exparser;

import com.happypeople.vi.EditContext;

/** The line addressed by the "$", the last line */
public class DollarAddress implements Address {
	@Override
	public long resolve(final EditContext context) {
		return context.getLinesModel().getSize();
	}
}
