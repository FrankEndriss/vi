package com.happypeople.vi.exparser;

import com.happypeople.vi.EditContext;

/** Aggregation of SimpleAddress and offset */
public class ExAddress implements Address {
	public final static Address DOT=new DotAddress();
	public final static Address DOLLAR=new DollarAddress();
	public final static Address FIRST=new AbsoluteAddress(1);

	private final Address address;
	private final long offset;

	public ExAddress(final Address address, final long offset) {
		this.address=address;
		this.offset=offset;
	}

	@Override
	public long resolve(final EditContext context) {
		return address.resolve(context)+offset;
	}

	@Override
	public String toString() {
		return "[offset="+offset+" delegate="+address+"]";
	}

}
