package de.stynxyxy.angrybot.runnable;

import de.stynxyxy.angrybot.bot.Bot;
import de.stynxyxy.angrybot.db.AngryDataBase;
import de.stynxyxy.angrybot.networking.Server;

public class AngryBot {


    private Bot bot;
    private Server server;
    private AngryDataBase dataBase;

    public AngryBot(Bot bot, Server server, AngryDataBase dataBase) {
        this.bot = bot;
        this.server = server;
        this.dataBase = dataBase;
    }
    public Bot getBot() {
        return bot;
    }

    public Server getServer() {
        return server;
    }
    public AngryDataBase getDataBase() {
        return dataBase;
    }
}
