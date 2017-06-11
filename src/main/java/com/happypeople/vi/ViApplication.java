package com.happypeople.vi;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.happypeople.vi.awt.AwtView;

//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication
public class ViApplication {
	public static void main(String[] args) {
		//SpringApplication.run(ViApplication.class, args);
		System.out.println("args: "+Arrays.asList(args));
		
		// TODO parse args
		
		final LinesModelEditor linesModel=LinesModelBuilder.createEmpty();
		linesModel.insertBefore(0, "firstLine");
		linesModel.insertAfter(0, "secondLine");
		linesModel.insertAfter(1, "thirdLine is longer...and next is an empty line");
		linesModel.insertAfter(2, "");
		linesModel.insertAfter(3, "last (fifth) line");

		final ViewModel viewModel=new SimpleViewModelImpl(80, 20, linesModel);
		final CursorModel cursorModel=new CursorModelImpl(linesModel, viewModel);
		
		final BlockingQueue<KeyEvent> inputQueue=new LinkedBlockingQueue<KeyEvent>();

		final KeyTypedController controller=new ViController(linesModel, cursorModel);
		final View view=new AwtView(linesModel, inputQueue);
		
		linesModel.addLinesModelChangedEventListener(view);
		cursorModel.addCursorPositionChangedEventListener(view);
		viewModel.addFirstLineChangedEventListener(view);
		
		// run the application by accepting input
		controller.processInput(inputQueue);

	}
}
