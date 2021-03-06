package com.happypeople.vi.exparser;

import com.happypeople.vi.EditContext;

/** Command objects created by ExParser
 * See "Pattern-oriented Software Achritecture" Vol 1, Command Processor p277
 */
public interface ExCommand {
	public final static int SUCCESS = 0;

	public int execute(EditContext editContext);

	public boolean canUndo();
	public void undo(EditContext editContext);
}
