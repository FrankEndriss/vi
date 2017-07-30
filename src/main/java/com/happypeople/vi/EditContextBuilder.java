package com.happypeople.vi;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.happypeople.vi.awt.AwtView;
import com.happypeople.vi.exparser.ExParser;
import com.happypeople.vi.linesModel.InMemoryLinesModelImpl;
import com.happypeople.vi.linesModel.ROFileLinesModelImpl;

public class EditContextBuilder {

	private File file;
	private boolean readonly;
	private GlobalConfig globalConfig;

	public EditContextBuilder setGlobalConfig(final GlobalConfig globalConfig) {
		this.globalConfig=globalConfig;
		return this;
	}

	public EditContextBuilder file(final File file) {
		this.file=file;
		return this;
	}

	public EditContextBuilder readonly(final boolean readonly) {
		this.readonly=readonly;
		return this;
	}

	public EditContext build() throws IOException {
		if(!readonly)
			throw new RuntimeException("cant really edit yet :/");

		final BlockingQueue<KeyEvent> inputQueue=new LinkedBlockingQueue<>();

		LinesModelEditor linesModel;
		if(file==null)
			linesModel=new InMemoryLinesModelImpl();
		else
			linesModel=new ROFileLinesModelImpl(file);

		final AwtView view= new AwtView(inputQueue);
		final ScreenModel screenModel=new ScreenModelImpl();
		screenModel.addScreenModelChangedEventListener(view);

		final ViewModel viewModel=new SimpleViewModelImpl(linesModel, screenModel);

		//final CursorModel cursorModel=cursorModelFactory.createCursorModel(linesModel, viewModel);
		final CursorModel cursorModel=new CursorModelImpl(linesModel, viewModel);
		view.addViewSizeChangedEventListener(cursorModel);

		final ExCommandProcessor exCommandProcessor=new ExCommandProcessor();
		final ExParser exParser=new ExParser(new StringReader(""));

		// run the application by accepting input
		//final KeyTypedController controller=context.getBean(ViController.class, linesModel, cursorModel);
		final KeyTypedController controller=new ViController(linesModel, cursorModel, exCommandProcessor, exParser);

		// TODO move to own class/file, iE GenericEditContext or the like
		return new EditContext() {

			@Override
			public LinesModelEditor getLinesModel() {
				return linesModel;
			}

			@Override
			public CursorModel getCursorModel() {
				return cursorModel;
			}

			@Override
			public MessageTarget getMessageTarget() {
				return view;
			}

			@Override
			public GlobalConfig getGlobalConfig() {
				return globalConfig;
			}

			@Override
			public void run() {
				controller.processInput(inputQueue, this);
			}

			@Override
			public void close() {
				// TODO implement a tracker which knows when to exit the application, iE after close of the last EditContext or the like
				System.exit(0);
			}
		};
	}
}
