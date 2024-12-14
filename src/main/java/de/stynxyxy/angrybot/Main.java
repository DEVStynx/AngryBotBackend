package de.stynxyxy.angrybot;

import de.stynxyxy.angrybot.bot.Bot;
import de.stynxyxy.angrybot.db.AngryDataBase;
import de.stynxyxy.angrybot.networking.Server;
import de.stynxyxy.angrybot.runnable.AngryBot;
import de.stynxyxy.angrybot.runnable.StartBotRunnable;
import de.stynxyxy.angrybot.runnable.StartServerRunnabble;
import io.github.cdimascio.dotenv.Dotenv;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        /**
         * Loading Environment Variables
         */
        Dotenv dotEnv = Dotenv.configure().directory("C:\\Users\\jonas\\Desktop\\Development\\java\\AngryBot\\AngryBotBackend\\src\\main\\resources\\.env").load();
        String BotToken = dotEnv.get("BOT_TOKEN");
        String SQLURL = dotEnv.get("SQLDATABASEURL");
        String SQLUSER = dotEnv.get("SQLDATABASEUSERNAME");
        String SQLPASSWORD = dotEnv.get("SQLDATABASEPASSWORD");
        String ADRESS = dotEnv.get("IPADRESS");

        AngryBot angryBot = runThreads(BotToken,SQLURL,SQLUSER,SQLPASSWORD,ADRESS);
        Bot bot = angryBot.getBot();
        Server server = angryBot.getServer();

        // Bot mit Server-Instanz initialisieren
        bot.initializeWithServer(server);
    }

    private static AngryBot runThreads(String botToken, String SQLURL, String SQLUSER, String SQLPASSWORD, String ADRESS) throws InterruptedException {

        // Bot initialisieren
        StartBotRunnable startBotRunnable = new StartBotRunnable(botToken);
        Thread botThread = new Thread(startBotRunnable);
        botThread.start();
        botThread.join();
        Bot bot = startBotRunnable.getBot();
        AngryDataBase dataBase = new AngryDataBase(SQLURL,SQLUSER,SQLPASSWORD);
        // Server initialisieren
        StartServerRunnabble startServerRunnable = new StartServerRunnabble(bot, dataBase,ADRESS);
        startServerRunnable.run();
        Server server = startServerRunnable.getServer();



        return new AngryBot(bot, server,dataBase);
    }
}
