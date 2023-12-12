package app.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Utils
 */
public class Utils {


    public static void log(Update update, String bot_answer) {
        String username = update.getMessage().getFrom().getUserName();
        long user_id = update.getMessage().getFrom().getId();
        String message_text = update.getMessage().getText();
        long chat_id = update.getMessage().getChatId();

        System.out.println("\n ----------------------------");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        System.out.println("Message from " + username + ". (id = " + user_id + ") \n Text - " + message_text
            + " from chat: ( " + chat_id + ")");
        System.out.println("Bot answer: \n Text - " + bot_answer);
    }

    public static SendMessage sendMessage(Long chatId, String message) {
        return new SendMessage()
            .setChatId(chatId)
            .setText(message);
    }

    public static EditMessageText sendMessage(Long chatId, Long messageId, String message) {
        return new EditMessageText()
            .setChatId(chatId)
            .setMessageId(Math.toIntExact(messageId))
            .setText(message);
    }

    public static void setTimeout(Runnable runnable, int delay){
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (Exception e){
                System.err.println(e);
            }
        }).start();
    }
}
