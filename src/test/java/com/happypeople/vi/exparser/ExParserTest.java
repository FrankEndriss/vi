package com.happypeople.vi.exparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.util.List;

import org.junit.Test;

public class ExParserTest {

	private List<ExCommand> testExCommandLine(final String cmd) throws ParseException {
		final ExParser parser=new ExParser(new StringReader(cmd));
		return parser.exCommandLine();
	}

	private ExCommand testExCommand(final String cmd) throws ParseException {
		final ExParser parser=new ExParser(new StringReader(cmd));
		return parser.exCommand();
	}

	@Test
	public void testNumber() throws ParseException {
		final String[] numberstrings=new String[] {
			"123",
			"0",
			"1",
			"9",
			"17",
			"12314225654656235363265636"
		};
		for(final String s : numberstrings) {
			final ExParser parser=new ExParser(new StringReader(s));
			assertEquals("should return the same number string", s, parser.NUMBER());
		}
	}

	@Test
	public void testFailNumber() throws ParseException {
		final String[] failNumberstrings=new String[] {
			// "0123",
			"",
			"x",
			"x9",
			// "17x",
			// "123142256546562353 63265636",
			// "00"
		};
		for(final String s : failNumberstrings) {
			try {
				final ExParser parser=new ExParser(new StringReader(s));
				parser.NUMBER();
				fail("should have created a ParserException: "+s);
			}catch(final ParseException ex) {
				; // ok
			}
		}
	}

	@Test
	public void testAbbrevCmd() throws ParseException {
		testExCommandLine("abbrev foo bar");
	}

	@Test
	public void testArgsCmd() throws ParseException {
		testExCommandLine("ar");
		testExCommandLine("arg");
		testExCommandLine("args");
	}

	@Test
	public void testCdCmd() throws ParseException {
		testExCommandLine("cd bla");
		testExCommandLine("chdir /foo");
		testExCommandLine("chd! /foo/bar");
		testExCommandLine("cd! mydir");
	}
}
