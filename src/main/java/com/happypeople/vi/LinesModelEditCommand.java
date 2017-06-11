package com.happypeople.vi;

/** Interface for commands to edit a LinesModel. On the controller level of the application, there 
 * should be implementations of this interface for create, change and delete lines of a LinesModel.
 * The controller can then hold a Stack, Queue or List of LinesModelEditCommands to apply and
 * revert them as needed.
 */
public interface LinesModelEditCommand {
	
	/** Apply this command to linesModel
	 * @param linesModel the linesModel to change
	 */
	void apply(LinesModel linesModel);

	/** Revert on linesModel what was done in apply()
	 * @param linesModel the linesModel to change
	 */
	void revert(LinesModel linesModel);

}
