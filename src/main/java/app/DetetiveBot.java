package app;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.vdurmont.emoji.EmojiParser;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import app.util.Config;
import app.util.Utils;

public class DetetiveBot extends TelegramLongPollingBot {

    private Jogo jogo;

    public DetetiveBot(DefaultBotOptions options, ArrayList<String> cargos) {
        super(options);
        this.jogo = new Jogo(cargos);
    }

    public DetetiveBot(ArrayList<String> cargos) {
        this.jogo = new Jogo(cargos);
    }

    @Override
    public void onUpdateReceived(Update update) {

        // envia que esta digitando
        try {
            SendChatAction sendChatAction = new SendChatAction()
                    .setChatId(update.getMessage().getChatId())
                    .setAction(ActionType.TYPING);
            execute(sendChatAction);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            System.out.println("erro");
        }
        Utils.log(update, "bot_answer"); // Loga no terminal o estado

        // cria uma collection com os erros para tratar todos no mesmo local
        final Set<Exception> exceptionHolder = ConcurrentHashMap.newKeySet();
        if (update.hasMessage() && update.getMessage().hasText()) {
            try {
                jogo.mensagem(update, e -> {
                    try {
                        execute(e);
                    } catch (TelegramApiException e1) {
                        exceptionHolder.add(e1);
                    }
                });
            } catch (Exception e1) {
                exceptionHolder.add(e1);
            }
        } else if (update.hasCallbackQuery()) {
            try {
                jogo.reply(update, e -> {
                    try {
                        execute(e);
                    } catch (TelegramApiException e1) {
                        exceptionHolder.add(e1);
                    }
                });
                jogo.reply(update, this);
            } catch (Exception e1) {
                exceptionHolder.add(e1);
            }
        }

        if (!exceptionHolder.isEmpty()) {
            try {
                execute(Utils.sendMessage(update.getMessage().getChatId(),
                        EmojiParser.parseToUnicode("Deu erro:  :sweat:")));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public String getBotUsername() {
        return Config.getInstance().getBotUserName();
    }

    @Override
    public String getBotToken() {
        return Config.getInstance().getBotToken();
    }
}
