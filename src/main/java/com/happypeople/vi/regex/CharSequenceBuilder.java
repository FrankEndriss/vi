package com.happypeople.vi.regex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.happypeople.vi.LinesModel;

/** Builder for CharSequence instances
 */
public class CharSequenceBuilder {
	// possible inputs
	private File file;
	private LinesModel linesModel;
	private InputStream inputStream;

	// if input is byte based, this enconding is used to convert to chars
	private String encoding;

	
	public CharSequence build() throws IOException {
		if(file!=null)
			return new InputStreamCharSequence(new FileInputStream(file), encoding);
		else if(inputStream!=null) 
			return new InputStreamCharSequence(inputStream, encoding);
		else if(linesModel!=null)
			return new LinesModelCharSequence(linesModel);
		else
			throw new IllegalStateException("no input given");
	}
	
	private CharSequenceBuilder setInput(File file, LinesModel linesModel, InputStream inputStream) {
		this.file=file;
		this.linesModel=linesModel;
		this.inputStream=inputStream;
		return this;
	}

	public CharSequenceBuilder setInput(final File file) {
		return setInput(file, null, null);
	}
	
	public CharSequenceBuilder setInput(final LinesModel linesModel) {
		return setInput(null, linesModel, null);
	}
	
	public CharSequenceBuilder setInput(final InputStream in) {
		return setInput(null, null, in);
	}
	
	public CharSequenceBuilder setByteEnconding(final String encoding) {
		this.encoding=encoding;
		return this;
	}

}
