package com.happypeople.vi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class CursorModelFactory {
	@Autowired
	private ApplicationContext context;
	
	public CursorModel createCursorModel(final LinesModel linesModel, final ViewModel viewModel) {
		return context.getBean(CursorModelImpl.class, linesModel, viewModel);
	}

}
