package app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import javax.naming.directory.InvalidAttributesException;

import com.vdurmont.emoji.EmojiParser;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import app.characters.Jogador;
import app.characters.Observador;
import app.characters.Usuario;
import app.util.Config;
import app.util.EditCallback;
import app.util.MessageCallback;
import app.util.Utils;

/**
 * Jogo
 */
public class Jogo {

    private boolean iniciado;
    private long chatId;
    private ArrayList<Usuario> usuarios;
    private Estado estado;

    private ArrayList<String> cargos;
    private HashMap<Long, Integer> votos;

    private Random random;

    public Jogo(ArrayList<String> cargos) {
        this.iniciado = false;
        this.usuarios = new ArrayList<>();
        this.estado = Estado.DISCURSAO;
        this.cargos = cargos;
        this.random = new Random();
        this.votos = new HashMap<>();
    }

    public void mensagem(Update update, MessageCallback callback) throws Exception {
        // throw new RuntimeException();
        String messageText = update.getMessage().getText();
        User usuario = update.getMessage().getFrom();
        Optional<Usuario> userFromList = usuarios.stream()
                .filter(userList -> userList.getId() == usuario.getId())
                .findFirst();

        if (messageText.startsWith("/entrar") && iniciado == false) {

            if (userFromList.isPresent())
                throw new InvalidAttributesException();

            String tipo = messageText.split("_")[1];
            switch (tipo) {
            case "jogador":
                String cargo = this.cargos.get(random.nextInt(this.cargos.size()));

                String nomeDoNovoUser = usuario.getUserName() ;
                if(nomeDoNovoUser == null){
                    nomeDoNovoUser = "sem nome";
                }

                Usuario novoJogador = (Usuario) Class.forName(cargo)
                        .getConstructor(long.class, String.class)
                        .newInstance(usuario.getId(), nomeDoNovoUser);
                this.add(novoJogador);
                break;
            case "observador":
                Observador novoObservador = new Observador(usuario.getId(), usuario.getUserName());
                this.add(novoObservador);
                break;

            default:
                throw new InvalidAttributesException();
            }

            SendMessage message = Utils.sendMessage(update.getMessage().getChatId(), "Criado");
            callback.apply(message);

        } else if (messageText.startsWith("/entrar") && this.iniciado == true) {
            SendMessage message = Utils.sendMessage(update.getMessage().getChatId(),
                    "Jogo ocorrendo, não da para criar");
            callback.apply(message);
        }

        if (messageText.startsWith("/start")) {
            this.setIniciado(true);
            SendMessage message = Utils.sendMessage(update.getMessage().getChatId(), "Iniciado");
            callback.apply(message);

            usuarios.forEach(eachUser -> {
                if (eachUser instanceof Jogador) {
                    String characterType = ((Jogador) eachUser).characterName();
                    SendMessage userMensagem = Utils.sendMessage(Long.valueOf(usuario.getId()),
                            "Você é " + characterType);
                    callback.apply(userMensagem);
                } else {
                    SendMessage userMensagem = Utils.sendMessage(Long.valueOf(usuario.getId()), "Observe o jogo");
                    callback.apply(userMensagem);
                }
            });

            new Thread(() -> intervalLoop(update, callback)).start();
        }

        if (messageText.startsWith("/stop")) {
            this.setIniciado(false);
            this.usuarios.clear();
            SendMessage message = Utils.sendMessage(update.getMessage().getChatId(), "Parado");
            callback.apply(message);
        }

        if (messageText.startsWith("echo")) {
            SendMessage message = Utils.sendMessage(update.getMessage().getChatId(), update.getMessage().getText());
            callback.apply(message);
        }

        if (messageText.startsWith("/jogadores")) {
            SendMessage message = Utils.sendMessage(update.getMessage().getChatId(), this.usuarios.toString());
            callback.apply(message);
        }

        System.out.println(usuarios);
    }

    public void reply(Update update, EditCallback callback) {
        String callData = update.getCallbackQuery().getData();
        long messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (callData.startsWith("votar")) {
            String quem = callData.split(" ")[1];
            String answer = "Votado em " + quem;
            EditMessageText new_message = Utils.sendMessage(chatId, messageId, answer);

            Usuario userVotado = this.usuarios.stream()
                    .filter(userFilter -> userFilter.getNome() == quem)
                    .findFirst()
                    .get();

            this.votos.put(userVotado.getId(), this.votos.get(userVotado.getId()) + 1);

            if(callData.split(" ")[2] != null) // ser for juiz da mais um voto
                this.votos.put(userVotado.getId(), this.votos.get(userVotado.getId()) + 1);

            callback.apply(new_message);
        }

        if (callData.startsWith("investigar")) {
            String quem = callData.split(" ")[1];

            Usuario userInvestigado = this.usuarios.stream()
                    .filter(userFilter -> userFilter.getNome() == quem)
                    .findFirst()
                    .get();

            String answer = "Resultado da investigação: " + userInvestigado.toString();
            EditMessageText new_message = Utils.sendMessage(chatId, messageId, answer);

            callback.apply(new_message);
        }
    }

    public void reply(Update update, DetetiveBot bot) throws TelegramApiException {
        String callData = update.getCallbackQuery().getData();
        long messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (callData.startsWith("matar")) {
            String quem = callData.split(" ")[1];
            String answer = "Matou " + quem;
            EditMessageText new_message = Utils.sendMessage(chatId, messageId, answer);

            Usuario userMatado = this.usuarios.stream()
                    .filter(userFilter -> userFilter.getNome() == quem)
                    .findFirst()
                    .get();


            for (Usuario usuarioForEach : this.usuarios) {
                if(usuarioForEach.getId() == userMatado.getId())
                    usuarioForEach.setMorto(true);
            }

            SendMessage messageToKilled = Utils.sendMessage(userMatado.getId(), "Você morreu");

            bot.execute(new_message);
            bot.execute(messageToKilled);
        }
    }

    private void intervalLoop(Update update, MessageCallback callback) {
        try {
            while (this.isIniciado()) {
                Thread.sleep(Config.getInstance().getDuration());

                String mensagem = "Default";

                switch (this.getEstado()) {
                case DISCURSAO:
                    System.out.println("DISCURSAO");
                    ArrayList<Usuario> vivos = this.usuarios.stream()
                        .filter(userFilter -> userFilter.isMorto() == false)
                        .collect(Collectors.toCollection(ArrayList::new));

                    ArrayList<Usuario> mortos = this.usuarios.stream()
                        .filter(userFilter -> userFilter.isMorto() == true)
                        .collect(Collectors.toCollection(ArrayList::new));

                    SendMessage discursaoMessage = Utils.sendMessage(update.getMessage().getChatId(),
                        "vivos: " + vivos.toString() + "\nmortos: " + mortos.toString());
                    callback.apply(discursaoMessage);

                    if(vivos.size() <= 2) {
                        callback.apply(Utils.sendMessage(update.getMessage().getChatId(), "Acabouu, digite /stop"));
                        // return;
                    }

                    mensagem = EmojiParser.parseToUnicode("Quem é vilão ?? :think:");
                    this.setEstado(Estado.VOTACAO);
                    break;

                case VOTACAO:
                    System.out.println("VOTACAO");
                    mensagem = EmojiParser.parseToUnicode("Quem deve sair da cidade?  :pencil:");
                    this.votos.clear();
                    usuarios.forEach(userEach -> {
                        this.votos.put(userEach.getId(), 0);
                    });
                    usuarios.forEach(userEach -> {
                        if (userEach instanceof Jogador)
                            ((Jogador) userEach).votar(this, update, callback);
                    });
                    this.setEstado(Estado.ACAO);
                    break;

                case ACAO:
                    System.out.println("ACAO");
                    int max = Collections.max(this.votos.values());
                    ArrayList<Long> maioresIds = this.votos.entrySet().stream()
                            .filter(entry -> entry.getValue() == max)
                            .map(entry -> entry.getKey())
                            .collect(Collectors.toCollection(ArrayList::new));

                    ArrayList<Usuario> removidos = this.usuarios.stream()
                            .filter(userFilter -> maioresIds.contains(userFilter.getId()))
                            .collect(Collectors.toCollection(ArrayList::new));

                    SendMessage rmMessage = Utils.sendMessage(update.getMessage().getChatId(),
                            removidos.toString() + " foram removidos");
                    callback.apply(rmMessage);

                    for (Usuario usuarioFor : this.usuarios) {
                        if(maioresIds.contains(usuarioFor.getId()))
                            usuarioFor.setMorto(true);
                    }

                    mensagem = EmojiParser.parseToUnicode("Hora de agir ??  :black_joker:");
                    usuarios.forEach(userEach -> {
                        userEach.acao(this, update, callback);
                    });
                    this.setEstado(Estado.DISCURSAO);
                    break;
                }
                SendMessage sendMessage = Utils.sendMessage(update.getMessage().getChatId(), mensagem);
                callback.apply(sendMessage);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public boolean isIniciado() {
        return this.iniciado;
    }

    public boolean getIniciado() {
        return this.iniciado;
    }

    public void setIniciado(boolean iniciado) {
        this.iniciado = iniciado;
    }

    public long getChatId() {
        return this.chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public ArrayList<Usuario> getUsuarios() {
        return this.usuarios;
    }

    public void setUsuarios(ArrayList<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public Estado getEstado() {
        return this.estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Jogo iniciado(boolean iniciado) {
        this.iniciado = iniciado;
        return this;
    }

    public Jogo chatId(long chatId) {
        this.chatId = chatId;
        return this;
    }

    public Jogo usuarios(ArrayList<Usuario> usuarios) {
        this.usuarios = usuarios;
        return this;
    }

    public Jogo estado(Estado estado) {
        this.estado = estado;
        return this;
    }

    public boolean add(Usuario e) {
        return usuarios.add(e);
    }

    public HashMap<Long, Integer> getVotos() {
        return votos;
    }

    public void setVotos(HashMap<Long, Integer> votos) {
        this.votos = votos;
    }
}
