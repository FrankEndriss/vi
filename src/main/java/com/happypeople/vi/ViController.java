package com.happypeople.vi;

import java.awt.event.KeyEvent;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/** Implementation of a vi controller.
 * It takes input (most likely from the keyboard) and triggers appropriate actions
 * on the data models. 
 * The affected models are a LinesModelEditor and a CursorModel.
 * Additionally one can add a listener to be called when the editor mode
 * changes, like from command mode to edit mode.
 */
@Component
@Scope("prototype")
public class ViController implements KeyTypedController {
	private Mode mode=Mode.VI_MODE;
	private final LinesModelEditor linesModel;
	private final CursorModel cursorModel;
	
	private ModeStrategy inputModeStrategy=new ModeStrategy_VI_MODE();

	public ViController(final LinesModelEditor linesModel, final CursorModel cursorModel) {
		this.linesModel=linesModel;
		this.cursorModel=cursorModel;
	}

	public void processInput(final BlockingQueue<KeyEvent> inputQueue) {
		while(true) {
			try {
				final KeyEvent evt=inputQueue.take();
				keyTyped(evt);
			}catch(Exception e) {
				e.printStackTrace(System.err);
			}
		}
	}

	public void keyTyped(KeyEvent e) {
		inputModeStrategy=inputModeStrategy.keyTyped(e);
	}

	public void addModeChangedEventListener(ModeChangedEventListener listener) {
		// TODO Auto-generated method stub
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
		private final Logger log=LoggerFactory.getLogger(ModeStrategy_VI_MODE.class);
		public ModeStrategy keyTyped(KeyEvent keyEvent) {
			final char c=keyEvent.getKeyChar();
			log.info("command char: "+keyEvent.getKeyChar());
			switch(c)  {
				// Simple cursor movement
				case 'h':	cursorModel.moveCursorLeft(1); break;
				case 'j':	cursorModel.moveCursorUp(-1); break;
				case 'k':	cursorModel.moveCursorUp(1); break;
				case 'l':	cursorModel.moveCursorLeft(-1); break;
				default: System.out.println("ignored keyTyped: "+keyEvent);
			}
			return this;
		}
	}

}
