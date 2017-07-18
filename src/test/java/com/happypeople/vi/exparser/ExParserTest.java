package com.happypeople.vi.exparser;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.Arrays;
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
		for(final String s : Arrays.asList("ar", "arg", "args")) {
			final ExCommand cmd=testExCommand(s);
			assertNotNull("must not be null", cmd);
			assertTrue("must be ArgsCmd", cmd instanceof ArgsCmd);
		}
	}

	@Test
	public void testCdCmd() throws ParseException {
		for(final String s : Arrays.asList("cd bla", "chdir /foo", "chd! /foo/bar", "cd! mydir")) {
			System.out.println("checking: "+s);
			ExCommand exCommand=testExCommand(s);
		}
	}
}
