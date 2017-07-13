package com.happypeople.vi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.happypeople.vi.linesModel.InMemoryLinesModelImpl;

@Component
public class LinesModelFactory {
	@Autowired
	private ApplicationContext context;
	
	public LinesModelEditor createEmpty() {
		return context.getBean(InMemoryLinesModelImpl.class);
	}
}
