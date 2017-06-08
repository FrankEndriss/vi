package com.happypeople.vi;

import java.awt.event.KeyEvent;

public class ViController implements KeyTypedController {
	private Mode mode=Mode.VI_MODE;
	private final LinesModel linesModel;
	private final ViewModel viewModel;
	
	private ModeStrategy inputModeStrategy=new ModeStrategy_VI_MODE();

	public ViController(LinesModel linesModel, ViewModel viewModel) {
		this.linesModel=linesModel;
		this.viewModel=viewModel;
	}

	public void keyTyped(KeyEvent e) {
		inputModeStrategy=inputModeStrategy.keyTyped(e);
	}

	public void addModeChangedEventListener(ModeChangedEventListener listener) {
		// TODO Auto-generated method stub
	}

	
	public void keyPressed(KeyEvent e) {
		// ignore, use keyTyped()
	}

	public void keyReleased(KeyEvent e) {
		// ignore, use keyTyped()
	}

	private static interface ModeStrategy {
		/** processes the keyEvent and returns the following ModeStrategy, which will process the next event... and so on.
		 * @param keyEvent
		 * @return
		 */
		ModeStrategy keyTyped(KeyEvent keyEvent);
	}
	
	/** Strategy for standard VI mode, like move using <h>, <j>, <k> and <l>
	 */
	private class ModeStrategy_VI_MODE implements ModeStrategy {
		public ModeStrategy keyTyped(KeyEvent keyEvent) {
			final char c=keyEvent.getKeyChar();
			switch(c)  {
				// Simple cursor movement
				case 'h':	viewModel.moveCursorLeft(1); break;
				case 'j':	viewModel.moveCursorUp(-1); break;
				case 'k':	viewModel.moveCursorUp(1); break;
				case 'l':	viewModel.moveCursorLeft(-1); break;
				default: System.out.println("ignored keyTyped: "+keyEvent);
			}
			return this;
		}
	}

}
