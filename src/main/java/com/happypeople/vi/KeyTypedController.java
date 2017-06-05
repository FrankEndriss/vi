package com.happypeople.vi;

import java.util.concurrent.BlockingQueue;

/** This models the controller part.
 * Main function is to parse the input and take appropriate actions.
 */
public interface KeyTypedController {
    enum Mode {
        /** hit <Esc> to enter vi mode */
        VI_MODE,
        /** enter ":" to enter command mode */
        COMMAND_MODE,
        /** use "a", "i" or the like to switch to input mode from vi mode. */
        INPUT_MODE
    }

    /** Event created after mode changes.
     */
    interface ModeChangedEvent {
        Mode getMode();
    }

    interface ModeChangedEventListener {
        void modeChanged(ModeChangedEvent evt);
    }
    /** Adds a listener for ModeChangeEvents. Note that the listener
     * is called once asynchronously with the current mode.
     * So no getMode() is needed nor available.
     * @param listener 
     */
    void addModeChangedEventListener(ModeChangedEventListener listener);


    interface InputEvent {
    }

    interface KeyEvent extends InputEvent {
    	int getKeyCode();
    }

    /** Will read inputEvents from input until null or exception while read
     * @param input queue of input events.
     */
    void processInput(final BlockingQueue<InputEvent> input);
}
