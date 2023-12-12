package app.characters;

import java.util.ArrayList;
import java.util.List;

import com.vdurmont.emoji.EmojiParser;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import app.Jogo;
import app.util.MessageCallback;
import app.util.Utils;

/**
 * JogadorHeroi
 */
public class JogadorHeroi extends Usuario implements Jogador {

    private String charactName = "Heroi";

    public JogadorHeroi(long id, String nome) {
        super(id, nome);
    }

    public JogadorHeroi(long id, String nome, boolean morto) {
        super(id, nome, morto);
    }

    @Override
    public void votar(Jogo jogo, Update update, MessageCallback callback) {
        SendMessage message = Utils.sendMessage(this.getId(),
                EmojiParser.parseToUnicode("Quem você deseja votar?   :balloon:"));
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        jogo.getUsuarios().forEach(eachUser -> {
            String nomeAlvo = eachUser.getNome();

            if(nomeAlvo == this.getNome()) return;

            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(new InlineKeyboardButton().setText(nomeAlvo)
                    .setCallbackData("votar " + nomeAlvo));
            rowsInline.add(rowInline);
        });

        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
        callback.apply(message);
    }

    @Override
    public void acao(Jogo jogo, Update update, MessageCallback callback) {
        SendMessage message = Utils.sendMessage(this.getId(),
                EmojiParser.parseToUnicode("Quem você deseja investigar?  :flashlight:"));
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        jogo.getUsuarios().forEach(eachUser -> {
            String nomeAlvo = eachUser.getNome();

            if(nomeAlvo == this.getNome()) return;

            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(new InlineKeyboardButton().setText(nomeAlvo)
                    .setCallbackData("investigar " + nomeAlvo));
            rowsInline.add(rowInline);
        });

        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
        callback.apply(message);
    }

    @Override
    public String characterName() {
        return this.charactName;
    }


}
