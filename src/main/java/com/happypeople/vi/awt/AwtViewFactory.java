package com.happypeople.vi.awt;

import java.awt.event.KeyEvent;
import java.util.concurrent.BlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.happypeople.vi.LinesModel;
import com.happypeople.vi.View;

@Component
public class AwtViewFactory {
	@Autowired
	private ApplicationContext context;
	
	/** This method creates an AWT based view
	 * @param linesModel
	 * @param inputQueue
	 * @return
	 */
	public View createAwtView(final LinesModel linesModel, final BlockingQueue<KeyEvent> inputQueue) {
		return context.getBean(AwtView.class, linesModel, inputQueue);
	}
}
