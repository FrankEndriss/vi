package com.happypeople.vi;

import java.awt.event.KeyEvent;
import java.util.concurrent.BlockingQueue;

/** This models the controller part.
 * Main function is to parse the input and take appropriate actions.
 * Input is accepted synchronously through the call of processInput(inputQueue)
 */
public interface KeyTypedController { //extends KeyListener {
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

    /** Reads input from inputQueue until null reference is found, ie the queue is kind of closed. */
    void processInput(BlockingQueue<KeyEvent> inputQueue, EditContext editContext);
}
