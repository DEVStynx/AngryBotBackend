package de.stynxyxy.angrybot.bot;

import de.stynxyxy.angrybot.bot.events.BotStartListener;
import de.stynxyxy.angrybot.bot.events.ShellListener;
import de.stynxyxy.angrybot.networking.Server;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

import java.util.ArrayList;

public class Bot {

    private final JDA jda;
    private final ArrayList<Category> sessionCategories = new ArrayList<>();
    private Server server;

    public Bot(String token) {
        this.jda = JDABuilder.createDefault(token)
                .setActivity(Activity.watching("Intellij Programming"))
                .build();

        // Fügt den BotStartListener hinzu
        getJda().addEventListener(new BotStartListener(this));
        getJda().addEventListener(new ShellListener(this.server));
    }

    // Methode zur Übergabe des Server-Objekts und zum Hinzufügen des CommandHandlers
    public void initializeWithServer(Server server) {
        this.server = server;

        // CommandHandler erstellen und Server direkt übergeben
        CommandHandler commandHandler = new CommandHandler(this.server);
        getJda().addEventListener(commandHandler);


    }

    public int getServerSize() {
        return this.server != null ? 1 : 0;
    }

    public JDA getJda() {
        return this.jda;
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
}
