package app.util;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

/**
 * StringCallback
 */
public interface EditCallback {

    public void apply(EditMessageText message);
}
