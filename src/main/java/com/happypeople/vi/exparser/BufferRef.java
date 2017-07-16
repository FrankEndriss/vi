package com.happypeople.vi.exparser;

/** Reference to a buffer, which is addressed by a single alphanumeric.
 * It is used in several command, usually to optionally store
 * modified lines or data.
 */
public class BufferRef {
	public static BufferRef DEFAULT_BUFFER=new BufferRef();
	public static String BUFFERNAMES="0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private final char name;

	private BufferRef() {
		this.name='.';
	}

	public BufferRef(final char name) {
		if(!BUFFERNAMES.contains(""+name))
			throw new IllegalArgumentException("Buffername must be one of: "+BUFFERNAMES);
		this.name=name;
	}

	public char getName() {
		return name;
	}

}
