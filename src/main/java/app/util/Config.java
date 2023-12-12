package app.util;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Config
 */
public class Config {

    private static Config instance;
    private String botUserName;
    private String botToken;
    private int duration;

    private Config() {
        Dotenv dotenv = Dotenv.load();
        this.botToken = dotenv.get("BOT_TOKEN");
        this.botUserName = dotenv.get("BOT_USERNAME");
        this.duration = Integer.parseInt(dotenv.get("SECONDS_DURATION")) * 1000;
    }

    public static Config getInstance() {
        if(instance == null) {
            instance = new Config();
            return instance;
        } else {
            return instance;
        }
    }

    public String getBotUserName() {
        return botUserName;
    }

    public String getBotToken() {
        return botToken;
    }

    public int getDuration() {
        return duration;
    }
}
