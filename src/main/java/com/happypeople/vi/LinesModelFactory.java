package com.happypeople.vi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class LinesModelFactory {
	@Autowired
	private ApplicationContext context;
	
	public LinesModelEditor createEmpty() {
		return context.getBean(LinesModelImpl.class);
	}
}
