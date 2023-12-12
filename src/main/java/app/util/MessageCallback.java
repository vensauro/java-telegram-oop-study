package app.util;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * StringCallback
 */
public interface MessageCallback {

    public void apply(SendMessage message);
}
