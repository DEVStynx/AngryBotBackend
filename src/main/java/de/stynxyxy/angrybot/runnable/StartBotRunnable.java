package de.stynxyxy.angrybot.runnable;

import de.stynxyxy.angrybot.bot.Bot;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Scanner;

public class StartBotRunnable implements Runnable{
    private volatile Bot bot;
    private final String token;
    public StartBotRunnable(String Token) {
        this.token = Token;
    }
    @Override
    public void run() {

        bot = new Bot(this.token);
        Scanner scanner = new Scanner(System.in);
        Thread inputThread = new Thread(() -> {
            //User Inputs
            while (true) {
                int in = scanner.nextInt();
                if (in == 0) {

                    for (Guild server: bot.getJda().getGuilds()) {
                        System.out.println("Deleting all Sessions in: "+server.getName());
                        bot.deleteSessionsInServer(server);
                    }

                } else if (in == 1) {
                    for (Guild server: bot.getJda().getGuilds()) {
                        System.out.println("Adding Session in : "+server.getName());

                    }
                }
            }
        });
        inputThread.start();

    }
    public Bot getBot() {
        return bot;
    }
}
