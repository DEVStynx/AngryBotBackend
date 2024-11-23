package de.stynxyxy.angrybot.runnable;

import de.stynxyxy.angrybot.bot.Bot;
import de.stynxyxy.angrybot.db.AngryDataBase;
import de.stynxyxy.angrybot.networking.Server;

import java.sql.Connection;


public class StartServerRunnabble implements Runnable{
    private volatile Server server;
    private Bot bot;
    private AngryDataBase connection;

    public StartServerRunnabble(Bot bot, AngryDataBase dataBase) {
        this.bot = bot;
        this.connection = dataBase;
    }

    @Override
    public void run() {
        server = new Server(bot,connection);
        Thread ServerThread = new Thread(() -> server.run());
        ServerThread.start();
    }

    public Server getServer() {
        return server;
    }
}
