package app.characters;

import org.telegram.telegrambots.meta.api.objects.Update;

import app.Jogo;
import app.util.MessageCallback;

/**
 * Jogador
 */
public interface Jogador {

    public void votar(Jogo jogo, Update update, MessageCallback callback);

    public String characterName();
}
