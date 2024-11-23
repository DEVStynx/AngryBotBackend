package de.stynxyxy.angrybot.runnable;

import de.stynxyxy.angrybot.bot.Bot;
import de.stynxyxy.angrybot.db.AngryDataBase;
import de.stynxyxy.angrybot.networking.Server;

import java.sql.Connection;


public class StartServerRunnabble implements Runnable{
    private volatile Server server;
    private final Bot bot;
    private final AngryDataBase connection;
    private final String ADRESS;

    public StartServerRunnabble(Bot bot, AngryDataBase dataBase, String IPADRESS) {
        this.bot = bot;
        this.connection = dataBase;
        this.ADRESS =IPADRESS;
    }

    @Override
    public void run() {
        server = new Server(bot,connection,ADRESS);
        Thread ServerThread = new Thread(() -> server.run());
        ServerThread.start();
    }

    public Server getServer() {
        return server;
    }
}
