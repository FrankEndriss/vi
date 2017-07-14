package com.happypeople.vi.regex;

import com.happypeople.vi.LinesModel;

/** Reconstructs a CharSequence from a LinesModel
 */
public class LinesModelCharSequence implements CharSequence {

	private final CharSequence delegate;
	
	public LinesModelCharSequence(final LinesModel source) {
		final StringBuilder sb=new StringBuilder();
		for(int i=0; i<source.getSize(); i++)
			sb.append(source.get(i)).append('\n');
		delegate=sb.toString();
	}

	@Override
	public char charAt(final int index) {
		return delegate.charAt(index);
	}

	@Override
	public int length() {
		return delegate.length();
	}

	@Override
	public CharSequence subSequence(final int start, final int end) {
		return delegate.subSequence(start, end);
	}

}
