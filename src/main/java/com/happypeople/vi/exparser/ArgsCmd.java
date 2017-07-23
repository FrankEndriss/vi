package com.happypeople.vi.exparser;

import com.happypeople.vi.EditContext;
import com.happypeople.vi.GlobalConfig;

/**
 * :ar[gs]
 * Shows the argument-vector in the message line
 */
public class ArgsCmd extends AbstractExCommand {

	@Override
	public int execute(final EditContext editContext) {
		final StringBuilder sb=new StringBuilder("[");
		final GlobalConfig conf=editContext.getGlobalConfig();
		final int argc=Integer.parseInt(conf.getValue("argc").orElse("0"));
		for(int i=0; i<argc; i++) {
			if(i>0)
				sb.append(", ");
			sb.append(conf.getValue("argv["+i+"]"));
		}
		sb.append("]");
		editContext.getMessageTarget().showMessage(sb.toString());
		return ExCommand.SUCCESS;
	}

}
