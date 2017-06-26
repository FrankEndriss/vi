package com.happypeople.vi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ViewModelFactory {
	@Autowired
	private ApplicationContext context;
	
	public ViewModel createViewModel(final LinesModel linesModel, final ScreenModel screenModel) {
		return context.getBean(SimpleViewModelImpl.class, 80, 20, linesModel, screenModel);
	}
}
