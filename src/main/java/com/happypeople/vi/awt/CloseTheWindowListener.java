package com.happypeople.vi.awt;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CloseTheWindowListener extends WindowAdapter {
	private final Frame frame;

	CloseTheWindowListener(Frame frame) {
		this.frame = frame;
	}

	public void windowClosing(WindowEvent e) {
		// TODO ask to save current file if needed
		frame.dispose();
	}

	public void windowClosed(WindowEvent e) {
		System.exit(0);
	}

	public void windowIconified(WindowEvent e) {
		// TODO stop blinking cursor
	}

	public void windowDeiconified(WindowEvent e) {
		// TODO start blinking cursor
	}

	public void windowActivated(WindowEvent e) {
		// TODO start cursor blinking
	}

	public void windowDeactivated(WindowEvent e) {
		// TODO stop blinking cursor
	}
}