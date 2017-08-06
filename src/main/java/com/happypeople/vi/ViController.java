package com.happypeople.vi;

import java.awt.event.KeyEvent;
import java.io.StringReader;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.happypeople.vi.exparser.ExCommand;
import com.happypeople.vi.exparser.ExParser;

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
	private final Mode mode=Mode.VI_MODE;
	private final LinesModel linesModel;
	private final CursorModel cursorModel;
	private final ExCommandProcessor exCommandProcessor;
	private final ExParser exParser;


	private final ModeStrategy inputModeStrategy_Vi_MODE=new ModeStrategy_VI_MODE();
	private final ModeStrategy inputModeStrategy_EX_MODE=new ModeStrategy_EX_MODE();

	private ModeStrategy inputModeStrategy=inputModeStrategy_Vi_MODE;

	public ViController(final LinesModel linesModel, final CursorModel cursorModel, final ExCommandProcessor exCommandProcessor, final ExParser exParser) {
		this.linesModel=linesModel;
		this.cursorModel=cursorModel;
		this.exCommandProcessor=exCommandProcessor;
		this.exParser=exParser;
	}

	@Override
	public void processInput(final BlockingQueue<KeyEvent> inputQueue, final EditContext editContext) {
		while(true) {
			try {
				final KeyEvent evt=inputQueue.take();
				keyTyped(evt, editContext);
			}catch(final Exception e) {
				e.printStackTrace(System.err);
			}
		}
	}

	public void keyTyped(final KeyEvent e, final EditContext editContext) {
		inputModeStrategy=inputModeStrategy.keyTyped(e, editContext);
	}

	@Override
	public void addModeChangedEventListener(final ModeChangedEventListener listener) {
		// TODO Auto-generated method stub
	}

	private static interface ModeStrategy {
		/** processes the keyEvent and returns the following ModeStrategy, which will process the next event... and so on.
		 * @param keyEvent
		 * @return
		 */
		ModeStrategy keyTyped(KeyEvent keyEvent, EditContext editContext);
	}

	/** Strategy for standard VI mode, like move using <h>, <j>, <k> and <l>
	 */
	private class ModeStrategy_VI_MODE implements ModeStrategy {
		private final Logger log=LoggerFactory.getLogger(ModeStrategy_VI_MODE.class);
		@Override
		public ModeStrategy keyTyped(final KeyEvent keyEvent, final EditContext editContext) {
			final char c=keyEvent.getKeyChar();
			log.info("command char: "+keyEvent.getKeyChar());
			switch(c)  {
				// Simple cursor movement
				case 'h':	cursorModel.moveCursorLeft(1); break;
				case 'j':	cursorModel.moveCursorUp(-1); break;
				case 'k':	cursorModel.moveCursorUp(1); break;
				case 'l':	cursorModel.moveCursorLeft(-1); break;
				case ':':	return inputModeStrategy_EX_MODE;
				default: System.out.println("ignored keyTyped: "+keyEvent);
			}
			return this;
		}
	}

	/** This mode is line oriented. One line is edited in the special ex-edit-line,
	 * after hit <ENTER>, the line is parsed  by the ExParser.
	 * Result is a List<ExCommand>, which is handed to the CommandProcessor for execution.
	 * Then, we switch back to standard CMD_MODE.
	 */
	private class ModeStrategy_EX_MODE implements ModeStrategy {
		private final Logger log=LoggerFactory.getLogger(ModeStrategy_EX_MODE.class);
		private final StringBuilder cmdStringBuilder=new StringBuilder();
		@Override
		public ModeStrategy keyTyped(final KeyEvent keyEvent, final EditContext editContext) {
			if(keyEvent.getKeyChar()=='\n') {
				try {
					exParser.ReInit(new StringReader(cmdStringBuilder.toString()));
					final List<ExCommand> commands=exParser.exCommandLine();
					exCommandProcessor.process(commands, editContext);
				}catch(final Exception e) {
					// TODO show some error in UI
					log.info("Exception while EX_MODE: ", e);
				}finally {
					cmdStringBuilder.setLength(0);
				}
				return inputModeStrategy_Vi_MODE;
			} else {
				cmdStringBuilder.append(keyEvent.getKeyChar());
				editContext.getMessageTarget().showMessage(":"+cmdStringBuilder);
				return this;
			}
			// TODO
			// - exit/clear cmdStringBuilder on ESC
			// - display a cursor in message/edit line
			// - implement edit commands as documented in vim
		}
	}

}
