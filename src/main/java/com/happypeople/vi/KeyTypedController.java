package com.happypeople.vi;

import java.awt.event.KeyListener;

/** This models the controller part.
 * Main function is to parse the input and take appropriate actions.
 * Input is sent by extended interface java.awt.KeyListener
 */
public interface KeyTypedController extends KeyListener {
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

}
