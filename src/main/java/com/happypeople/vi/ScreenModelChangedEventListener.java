package com.happypeople.vi;

public interface ScreenModelChangedEventListener {
	/** Screen model changed completly, must be repainted
	 * from scratch.
	 * @param evt some hint or the like
	 */
	public void screenModelChanged(ScreenModelChangedEvent evt);

	/** Position of the cursor changed
	 * @param newPos new position of the cursor
	 */
	public void cusorPositionChanged(ScreenModelChangedEvent evt);

	// TODO
	// The screenModel should provide "formatted text", that is text
	// with colors and bgk-color etc.
	// One special color would be the "CursorColor", which displays that
	// char as the cursor position

	// Another aproach is, to define kind of a overlay to the text. Then,
	// the cursor is a special case of overlay.
	// Overlay- and text-changes can be propagated separatly.
	// The rendering is then first text, second overlays.
}