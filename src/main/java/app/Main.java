package app;

import java.util.ArrayList;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import app.characters.JogadorHeroi;
import app.characters.JogadorNormal;
import app.characters.JogadorVilao;
import expancao.JogadorJuiz;

/**
 * Hello world!
 */
public final class Main {
    private Main() {
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        System.out.println("Hello World!");

        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        ArrayList<String> cargos = new ArrayList<String>() {{
            // add(Observador.class.getCanonicalName());
            add(JogadorHeroi.class.getCanonicalName());
            add(JogadorNormal.class.getCanonicalName());
            add(JogadorVilao.class.getCanonicalName());
            add(JogadorJuiz.class.getCanonicalName());
        }};

        try {
            botsApi.registerBot(new DetetiveBot(cargos));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
