package com.happypeople.vi;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.happypeople.vi.awt.AwtViewFactory;

//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ViApplication {
	private final static Logger log=LoggerFactory.getLogger(ViApplication.class);

	public static void main(final String[] args) {
		final ConfigurableApplicationContext context=
				new SpringApplicationBuilder(ViApplication.class).headless(false).run(args);

		System.out.println("args: "+Arrays.asList(args));

		try {
			runTheApp(context);
		}catch(final Exception e) {
			log.error("some error, main thread ended: ", e);
		}
	}

	public static void runTheApp(final ConfigurableApplicationContext context) {

		final AwtViewFactory awtViewFactory=context.getBean(AwtViewFactory.class);
		final LinesModelFactory linesModelFactory=context.getBean(LinesModelFactory.class);
		final ViewModelFactory viewModelFactory=context.getBean(ViewModelFactory.class);
		final CursorModelFactory cursorModelFactory=context.getBean(CursorModelFactory.class);

		// TODO springify
		final BlockingQueue<KeyEvent> inputQueue=new LinkedBlockingQueue<>();

		// TODO parse args

		final LinesModelEditor linesModel=linesModelFactory.createEmpty();

		log.info("linesModel="+linesModel);

		// remove for testing
		linesModel.insertBefore(0, "firstLine");
		linesModel.insertAfter(0, "secondLine");
		linesModel.insertAfter(1, "thirdLine is longer...and next is an empty line");
		linesModel.insertAfter(2, "");
		linesModel.insertAfter(3, "last (fifth) line");
		for(int i=4; i<100; i++) 
			linesModel.insertAfter(i, "another..."+i);

		final ScreenModel screenModel=new ScreenModel();
		final View view= awtViewFactory.createAwtView(screenModel, inputQueue);

		final ViewModel viewModel=viewModelFactory.createViewModel(linesModel, screenModel);
		view.addViewSizeChangedEventListener(viewModel);

		final CursorModel cursorModel=cursorModelFactory.createCursorModel(linesModel, viewModel);
		view.addViewSizeChangedEventListener(cursorModel);
		final KeyTypedController controller=context.getBean(ViController.class, linesModel, cursorModel);

		linesModel.addLinesModelChangedEventListener(view);
		cursorModel.addCursorPositionChangedEventListener(view);
		viewModel.addFirstLineChangedEventListener(view);

		// run the application by accepting input
		controller.processInput(inputQueue);

	}

}
