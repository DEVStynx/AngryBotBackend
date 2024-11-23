package de.stynxyxy.angrybot.networking;

import de.stynxyxy.angrybot.bot.Bot;
import de.stynxyxy.angrybot.db.AngryDataBase;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Server {
    private static final int PORT = 12345;
    private List<Session> sessions = new ArrayList<>();
    public HashMap<Integer,Channel> CurrentRespondingCommandChannels = new HashMap<>();
    private boolean running = true;



    private final AngryDataBase angryDataBase;

    public final String IP_ADDRESS;
    private final int MAX_USERS = 100;


    private final Bot bot;
    public Server(Bot bot, AngryDataBase dataBase, String adress) {
        this.bot = bot;
        this.angryDataBase = dataBase;
        this.IP_ADDRESS = adress;
    }
    public Bot getBot() {
        return bot;
    }

    public void run() {
        int sessionIds = 0;
        try (ServerSocket serverSocket = new ServerSocket(PORT,MAX_USERS, InetAddress.getByName(IP_ADDRESS))) {
            System.out.println("Server started and listens...");
            System.out.printf("Server started at ADRESS: %s, PORT: %d\n",IP_ADDRESS,PORT);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New Client connected: " + clientSocket.getInetAddress());


                // Erstelle und starte eine neue Session für jeden verbundenen Client
                Session session = new Session(this,clientSocket,sessionIds);
                sessionIds++;
                for (Guild guild: this.bot.getJda().getGuilds()) {
                    session.CreateSessionInServer(guild);
                }
                sessions.add(session);
                new Thread(session).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public AngryDataBase getConnection() {
        return this.angryDataBase;
    }



    public List<Session> getSessions() {
        return sessions;
    }
    public List<Session> setSessions(List<Session> sessions) {
        this.sessions = sessions;
        return this.sessions;
    }
    public List<Session> addSession(Session session) {
        List<Session> sesList = this.sessions;
        sesList.add(session);
        this.setSessions(sesList);
        return this.getSessions();
    }

    public List<Session> removeSessionById(int id) {
        try {

            List<Session> sesList = this.getSessions();
            sesList.remove(id);

            return this.setSessions(sesList);

        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Session doesn't exist/ Id is too big!"+id);
        }

    }

    public void deleteSessionsInServer(Guild guild) {
        for (Category category : guild.getCategories()) {
            if (hasPrefix(category.getName(), "Session:")) {
                for (GuildChannel channel : category.getChannels()) {
                    channel.delete().queue();
                }
                category.delete().queue();
            }
        }
    }
    public boolean hasPrefix(String s, String prefix) {
        return s.split(" ")[0].equalsIgnoreCase(prefix);
    }

    public void stop() {
        running = false;
        for (Session session : sessions) {
            session.close();
        }
        System.out.println("Server stopped.");
    }
}
