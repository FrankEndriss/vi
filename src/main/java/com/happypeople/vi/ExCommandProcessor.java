package com.happypeople.vi;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.happypeople.vi.exparser.ExCommand;

public class ExCommandProcessor {
	private final static Logger log=LoggerFactory.getLogger(ExCommandProcessor.class);

	public void process(final List<ExCommand> commands, final EditContext editContext) {
		log.info(""+commands);
		// TODO make undoable and the like...
		for(final ExCommand cmd : commands)
			cmd.execute(editContext);
	}

}
