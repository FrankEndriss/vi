package com.happypeople.vi;

import java.util.Arrays;

import com.happypeople.vi.awt.AwtView;

//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication
public class ViApplication {
	public static void main(String[] args) {
		//SpringApplication.run(ViApplication.class, args);
		System.out.println("args: "+Arrays.asList(args));
		
		// TODO parse args
		
		final LinesModel linesModel=LinesModelBuilder.createEmpty();
		linesModel.insertBefore(0, "firstLine");
		linesModel.insertAfter(0, "secondLine");
		linesModel.insertAfter(1, "thirdLine is longer...and next is an empty line");
		linesModel.insertAfter(2, "");
		linesModel.insertAfter(3, "last (fifth) line");

		final ViewModel viewModel=new SimpleViewModelImpl(80, 20, linesModel);
		final KeyTypedController controller=new ViController(linesModel, viewModel);
		final View view=new AwtView(linesModel, controller);

		
		linesModel.addLinesModelChangedEventListener(view);
		viewModel.addCursorPositionChangedEventListener(view);
		viewModel.addFirstLineChangedEventListener(view);
		

	}
}
