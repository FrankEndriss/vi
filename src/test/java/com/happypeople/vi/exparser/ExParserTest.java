package com.happypeople.vi.exparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.happypeople.vi.EditContext;

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
				""+Long.MAX_VALUE
		};
		for(final String s : numberstrings) {
			final ExParser parser=new ExParser(new StringReader(s));
			assertEquals("should return the same number string", s, ""+parser.NUMBER());
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
		final ExCommand exCmd=testExCommand("abbrev foo bar");
		assertTrue("should be AbreviationCmd", exCmd instanceof AbbreviationCmd);
		assertTrue("should be AbreviationCmd", testExCommand("abbrev foo bar with ws") instanceof AbbreviationCmd);
		assertTrue("should be AbreviationCmd", testExCommand("abbrev foo bar with ws and \\\nnewline") instanceof AbbreviationCmd);
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
			final ExCommand exCommand=testExCommand(s);
			assertTrue(exCommand instanceof CdCmd);
		}
	}

	@Test
	public void testCopyCmd() throws ParseException {
		for(final String s : Arrays.asList(
				"1,$co200",
				"1,200co0",
				"123co.",
				"co."
				))
		{
			System.out.println("checking: "+s);
			final ExCommand exCommand=testExCommand(s);
			assertTrue(exCommand instanceof CopyCmd);
		}
	}
	@Test
	public void testDeleteCmd() throws ParseException {
		for(final String s : Arrays.asList(
				"1,$d",
				".,.+3d\"a",
				"d200",
				"200d30",
				"d"
				))
		{
			System.out.println("checking: "+s);
			final ExCommand exCommand=testExCommand(s);
			assertTrue(exCommand instanceof DeleteCmd);
		}
	}

	@Test
	public void testQuitCmd() throws ParseException {
		for(final String s : Arrays.asList(
				"q",
				"qu",
				"qui",
				"quit!",
				"q!"
				))
		{
			System.out.println("checking: "+s);
			final List<ExCommand> exCommands=testExCommandLine(s);
			assertEquals("len of command list", 1, exCommands.size());
			assertTrue("should be QuitCommand", exCommands.get(0) instanceof QuitCommand);
		}
	}

	@Test
	public void testWriteCmd() throws ParseException {
		for(final String s : Arrays.asList(
				"w",
				"wq",
				"w!",
				"wq!",
				"w /tmp/bla.f",
				"w! /tmp/bla.f",
				"wq /tmp/bla.f",
				"wq! /tmp/bla.f",
				"1,100w /tmp/bla.f",
				"37wq! /tmp/bla.f",
				"%wq! /tmp/bla.f",
				"12,+1++-wq! /tmp/bla.f"
				))
		{
			System.out.println("checking: "+s);
			final ExCommand exCommand=testExCommand(s);
			assertTrue(exCommand instanceof ExCommand);
		}
	}

	private List<Address> parseAddressList(final String str) throws ParseException {
		final ExParser parser=new ExParser(new StringReader(str));
		return parser.address_list();
	}

	@Test
	public void testAddressList() throws ParseException {
		final EditContext editContext=null;

		List<Address> result=parseAddressList("1,$");
		assertEquals("should be size two", 2, result.size());
		assertEquals("first line x 1", 1, result.get(0).resolve(editContext));
		//assertEquals("last line DOLLAR", ExAddress.DOLLAR, result.get(1));

		result=parseAddressList("200");
		assertEquals("should be size one", 1, result.size());
		assertEquals("first adr line 200", 200, result.get(0).resolve(editContext));

		result=parseAddressList("200-1");
		assertEquals("should be size one", 1, result.size());
		assertEquals("first adr line 199", 199, result.get(0).resolve(editContext));

		result=parseAddressList("200-");
		assertEquals("should be size one", 1, result.size());
		assertEquals("first adr line 199", 199, result.get(0).resolve(editContext));

		result=parseAddressList("200-1+--30");
		assertEquals("should be size one", 1, result.size());
		assertEquals("first adr line 169", 169, result.get(0).resolve(editContext));
	}
}
