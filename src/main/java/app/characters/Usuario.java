package app.characters;

import org.telegram.telegrambots.meta.api.objects.Update;

import app.Jogo;
import app.util.MessageCallback;

/**
 * Usuario
 */
abstract public class Usuario {

    private long id;
    private String nome;
    private boolean morto;

    public Usuario(long id, String nome) {
        this.id = id;
        this.nome = nome;
        this.morto = false;
    }

    public Usuario(long id, String nome, boolean morto) {
        this.id = id;
        this.nome = nome;
        this.morto = morto;
    }

    public abstract void acao(Jogo jogo, Update update, MessageCallback callback);

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isMorto() {
        return morto;
    }

    public void setMorto(boolean morto) {
        this.morto = morto;
    }

    @Override
    public String toString() {
        return "Usuario [id=" + id + ", morto=" + morto + ", nome=" + nome + "]";
    }
}
