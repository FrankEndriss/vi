package com.happypeople.vi;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ViApplication {
	private final static Logger log=LoggerFactory.getLogger(ViApplication.class);

	public static void main(final String[] args) {

		// TODO get rid of spring in startup, to slow.
		final ConfigurableApplicationContext context=
				new SpringApplicationBuilder(ViApplication.class).headless(false).run(args);

		try {
			runTheApp(context, args);
		}catch(final Exception e) {
			log.error("some error, main thread ended: ", e);
		}
	}

	private static OptionSet parseArgs(final String[] args) {
		final OptionParser parser=new OptionParser();
		parser.acceptsAll(Arrays.asList("R", "readonly"), "readonly mode, no write to file possible"); // readonly
		parser.accepts("w"); // one window per file/editor
		parser.accepts("t"); // one window and one tab within that window per file/editor

		return parser.parse(args);
	}

	public static void runTheApp(final ConfigurableApplicationContext context, final String[] args) throws IOException {
		final OptionSet cliArgs=parseArgs(args);

		final Properties globalProps=new Properties();
		globalProps.put("argc", args.length);
		for(int i=0; i<args.length; i++)
			globalProps.put("argv["+i+"]", args[i]);
		final GlobalConfig globalConfig=new GlobalConfig() {
			@Override
			public Optional<String> getValue(final String key) {
				return Optional.ofNullable(globalProps.getProperty(key));
			}
		};

		final EditContextBuilder builder=new EditContextBuilder();
		builder.setGlobalConfig(globalConfig).readonly(true);

		if(!cliArgs.nonOptionArguments().isEmpty())
			builder.file(new File(args[0]));

		final EditContext editContext=builder.build();

		// TODO think about how to run an editContext, especially if there is more than one
		editContext.run();


		/*

		//final AwtViewFactory awtViewFactory=context.getBean(AwtViewFactory.class);
		final LinesModelFactory linesModelFactory=context.getBean(LinesModelFactory.class);
		//final ViewModelFactory viewModelFactory=context.getBean(ViewModelFactory.class);
		final CursorModelFactory cursorModelFactory=context.getBean(CursorModelFactory.class);

		// TODO springify
		final BlockingQueue<KeyEvent> inputQueue=new LinkedBlockingQueue<>();

		// TODO parse args

		final LinesModel linesModel;
		if(cliArgs.nonOptionArguments().isEmpty()) {
			final LinesModelEditor linesModelE=linesModelFactory.createEmpty();

			// remove for testing
			linesModelE.insertBefore(0, "firstLine");
			linesModelE.insertAfter(0, "secondLine");
			linesModelE.insertAfter(1, "thirdLine is longer...and next is an empty line");
			linesModelE.insertAfter(2, "");
			linesModelE.insertAfter(3, "last (fifth) line");
			for(int i=4; i<100; i++)
				linesModelE.insertAfter(i, "another..."+i);
			linesModel=linesModelE;
		}else {
			final Object arg=cliArgs.nonOptionArguments().get(0);
			linesModel=new ROFileLinesModelImpl(new File(""+arg));
		}

//		final View view= awtViewFactory.createAwtView(screenModel, inputQueue);
		final AwtView view= new AwtView(inputQueue);
		final ScreenModel screenModel=new ScreenModelImpl();
		screenModel.addScreenModelChangedEventListener(view);

//		final ViewModel viewModel=viewModelFactory.createViewModel(linesModel, screenModel);
		final ViewModel viewModel=new SimpleViewModelImpl(linesModel, screenModel);

		final CursorModel cursorModel=cursorModelFactory.createCursorModel(linesModel, viewModel);
		view.addViewSizeChangedEventListener(cursorModel);

		// run the application by accepting input
		final KeyTypedController controller=context.getBean(ViController.class, linesModel, cursorModel);
		controller.processInput(inputQueue);
		*/

	}

}
