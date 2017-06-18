package com.happypeople.vi;

import java.awt.event.KeyEvent;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class BeanConfiguration {

	@Bean
	@Scope("prototype")
	public BlockingQueue<KeyEvent> inputQueue() {
		return new LinkedBlockingQueue<KeyEvent>();
	}

	/*
	@Bean
	public AwtViewFactory awtViewFactory() {
		return new AwtViewFactory();
	}

	@Bean
	public LinesModelFactory linesModelFactory() {
		return new LinesModelFactory();
	}

	@Bean
	public ViewModelFactory viewModelFactory() {
		return new ViewModelFactory();
	}

	@Bean
	public CursorModelFactory cursorModelFactory() {
		return new CursorModelFactory();
	}
	*/

}
