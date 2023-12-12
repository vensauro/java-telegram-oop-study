package app.characters;

import org.telegram.telegrambots.meta.api.objects.Update;

import app.Jogo;
import app.util.MessageCallback;
import app.util.Utils;

/**
 * Observador
 */
public class Observador extends Usuario {

    public Observador(long id, String nome) {
        super(id, nome);
    }

    public Observador(long id, String nome, boolean morto) {
        super(id, nome, morto);
    }

    @Override
    public void acao(Jogo jogo, Update update, MessageCallback callback) {
        jogo.getUsuarios().forEach(userEach -> {
            callback.apply(Utils.sendMessage(this.getId(), userEach.toString() + "class is " + ((Jogador)userEach).characterName()));
        });
    }
}
