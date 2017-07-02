package com.happypeople.vi;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/** Tests class ScreenLine */
public class ScreenLineTest {
	/** Object under test. */
	ScreenLine out;

	@Before
	public void setUp() {
		out=new ScreenLine("Hello world", 80);
	}

	@Test
	public void testCalcTabPos() {
		assertEquals("TAB at 0", 3, out.calcTabPos(0));
		assertEquals("TAB at 1", 3, out.calcTabPos(1));
		assertEquals("TAB at 2", 3, out.calcTabPos(2));
		assertEquals("TAB at 3", 3, out.calcTabPos(3));
		assertEquals("TAB at 4", 7, out.calcTabPos(4));
	}

	@Test
	public void testGetNumScreenLines() {
		out.setScreenSizeX(40);
		assertEquals("should be one line", 1, out.getNumScreenLines());
		out.setScreenSizeX(8);
		assertEquals("should be two lines", 2, out.getNumScreenLines());
	}

	private ScreenCursorPosition screenCursorPosition(final int x, final int y) {
		return ScreenCursorPosition.ORIGIN.addX(x).addY(y);
	}
	/** Test screenPosition calculation over simple String (without TABs) */
	@Test
	public void testGetScreenPos1() {
		assertEquals("origin should be origin", screenCursorPosition(0, 0), out.getScreenPos(0));
		assertEquals("position 2", screenCursorPosition(2, 0), out.getScreenPos(2));

		out.setScreenSizeX(4);
		assertEquals("position 3, should be last of first line", screenCursorPosition(3, 0), out.getScreenPos(3));
		assertEquals("position 4, should be start of second line", screenCursorPosition(0, 1), out.getScreenPos(4));
		assertEquals("position 5, should be second of second line", screenCursorPosition(1, 1), out.getScreenPos(5));
		assertEquals("position 7, should be fourth of second line", screenCursorPosition(3, 1), out.getScreenPos(7));
	}

	/** Test render simple String */
	@Test
	public void testRender1() {
		for(final String str : new String[] {
				"hello",
				"world",
				"Hello World",
				"\b",
				""
		}) {
			assertEquals("should be equal", str, new ScreenLine(str, 80).render(1024));
		}
	}

	/** Test render String with tabs */
	@Test
	public void testRender2() {
		assertEquals("should be equal", "\b\b\b\bHello", new ScreenLine("\tHello", 80).render(1024));
		assertEquals("should be equal", "H\b\b\bello", new ScreenLine("H\tello", 80).render(1024));
		assertEquals("should be equal", "He\b\bllo", new ScreenLine("He\tllo", 80).render(1024));
		assertEquals("should be equal", "Hel\blo", new ScreenLine("Hel\tlo", 80).render(1024));
		assertEquals("should be equal", "Hell\b\b\b\bo", new ScreenLine("Hell\to", 80).render(1024));
	}

	/** Test screenPosition calculation over complex String (with TABs) */
	@Test
	public void testGetScreenPos2() {
		// a tab at first position should be expanded to four blanks
		out=new ScreenLine("\tHello World", 80);
		assertEquals("first char is a TAB", screenCursorPosition(3, 0), out.getScreenPos(0));
		assertEquals("first char is a TAB, second not", screenCursorPosition(4, 0), out.getScreenPos(1));

		out.setScreenSizeX(5);
		assertEquals("position 2, should be first of second line", screenCursorPosition(0, 1), out.getScreenPos(2));
		assertEquals("position 3, should be second of second line", screenCursorPosition(1, 1), out.getScreenPos(3));
		assertEquals("position 4, should be third of second line", screenCursorPosition(2, 1), out.getScreenPos(4));
		assertEquals("position 7, should be first of third line", screenCursorPosition(0, 2), out.getScreenPos(7));
	}

}
