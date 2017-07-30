package com.happypeople.vi.linesModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/** LinesModel based on a readonly file
 */
public class ROFileLinesModelImpl extends AbstracLinesModelImpl {

	private final RandomAccessFile ras;

	/** List of offsets into ras where lines start */
	private final List<Long> lineStartingOffsets=new ArrayList<>();


	/** TODO add File encoding paramter
	 * @param file Path to file
	 * @throws IOException if file notfound or not readeable
	 */
	public ROFileLinesModelImpl(final File file) throws IOException {
		ras=new RandomAccessFile(file, "r");
		recreateLinesIndex();
	}

	private void recreateLinesIndex() throws IOException {
		ras.seek(0);
		lineStartingOffsets.clear();

		long idx=0;
		lineStartingOffsets.add(idx); // at idx=0 starts a line, by definition

		int b;
		boolean lastByteWasLineEnd=false;
		do {
			b=ras.read();
			idx++;
			if(b>=0) {
				if(lastByteWasLineEnd) {
					lineStartingOffsets.add(idx-1);
					lastByteWasLineEnd=false;
				}
				if(b=='\n')
					lastByteWasLineEnd=true;
			}
		}while(b>0);

	}

	/** Note that an empty file has length=1, since the first line is the empty line (implicit line end at end of file)
	 * @return max index for get plus one
	 */
	@Override
	public long getSize() {
		return lineStartingOffsets.size();
	}

	private final static Charset charset=Charset.forName("UTF-8");

	@Override
	public String get(final long lineNo) {
		final ByteArrayOutputStream baos=new ByteArrayOutputStream();
		try {
			ras.seek(lineStartingOffsets.get((int)lineNo));
			int b;
			do {
				b=ras.read();
				if(b>0 && b!='\r' && b!='\n')
					baos.write(b);
				else
					break;
			}while(true);

//			return new String(baos.toByteArray());
			return new String(baos.toByteArray(), charset);

		} catch (final IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("cannot seek/read file");
		}
	}

	@Override
	public void replace(final long lineNo, final String newVersionOfLine) {
		throw new IllegalStateException("this is readonly");
	}

	@Override
	public void insertAfter(final long lineNo, final String newLine) {
		throw new IllegalStateException("this is readonly");
	}

	@Override
	public void insertBefore(final long lineNo, final String newLine) {
		throw new IllegalStateException("this is readonly");
	}

	@Override
	public void remove(final long lineNo) {
		throw new IllegalStateException("this is readonly");
	}
}
