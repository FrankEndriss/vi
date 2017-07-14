package com.happypeople.vi.regex;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class InputStreamCharSequence implements CharSequence {

	private final CharSequence delegate;
	
	/**
	 * @param inputStream
	 * @param encoding
	 * @throws IOException
	 */
	public InputStreamCharSequence(InputStream inputStream, String encoding) throws IOException {
		// simple implementation: Read stream into big String, use that
		// TODO optimize to use RandomAccessFile instead or the like
		final InputStream bufferedIn=inputStream instanceof BufferedInputStream? inputStream : new BufferedInputStream(inputStream);
		final Reader r=encoding==null?
				new InputStreamReader(bufferedIn):
				new InputStreamReader(bufferedIn, encoding);
				
		final char[] buf=new char[1024*16];
		final StringBuilder sb=new StringBuilder();
		int i;
		while((i=r.read(buf))>0)
			sb.append(buf, 0, i);
		
		delegate=sb;
	}

	@Override
	public char charAt(int index) {
		return delegate.charAt(index);
	}

	@Override
	public int length() {
		return delegate.length();
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return delegate.subSequence(start, end);
	}

}
