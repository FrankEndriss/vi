package com.happypeople.vi.linesModel;

/** Design of a LinesModel based on LineRef
 */
public interface LineRefBasedLinesModel {

	public interface LineRef {
		/**
		 * @return
		 */
		CharSequence getContent();
		long getLineNumber();

		/** @return true if this line was removed. If it is once removed it will be removed forever. */
		public boolean isRemoved();

		// and the edit
		public void setContent(CharSequence chars);
		public void insertAt(long pos, CharSequence chars);
		public void removeAt(long pos, long count);
		/** Remove followed by insert.
		 * @param pos
		 * @param count
		 * @param chars
		 */
		public void changeAt(long pos, long count, CharSequence chars);
	}

	long getSize();
	LineRef getLineRef(long lineNumber);

	// and the edit
	void remove(LineRef lineRef);


	LineRef insertBefore(LineRef lineRef, CharSequence content);
	LineRef insertAfter(LineRef lineRef, CharSequence content);
	/** Needed to insert first line in empty model */
	LineRef insertFirst(CharSequence content);


}
