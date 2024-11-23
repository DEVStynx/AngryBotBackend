package de.stynxyxy.angrybot.bot.events;

import de.stynxyxy.angrybot.bot.Bot;
import de.stynxyxy.angrybot.networking.Server;
import de.stynxyxy.angrybot.networking.Session;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ShellListener extends ListenerAdapter {

    public Server server;

    public ShellListener(Server server) {
        this.server = server;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        System.out.println(event.getMessage().getContentRaw());
        if (event.getChannel().getType().isMessage() && event.getChannel().getName().contains("Shell")) {
            Category category = getCategorybyEvent(event);
            if (category == null) {
                System.out.println("Category is null!");

            }
            int sessionid = -1;

            String raw = category.getName().replace("Session:","").replace(" ","");
            int sessid = Integer.valueOf(raw);
            if (sessionid == -1 || sessionid > server.getSessions().size() || sessionid < 0) {
                System.out.println("There is No Session with the Id: "+sessionid);
                return;
            }
            try {
                Session sess = server.getSessions().get(sessid);
                int id = sess.sendCommandwithId(event.getMessage().getContentRaw());

                System.out.println("Got Command: "+event.getMessage().getContentRaw());
                server.CurrentRespondingCommandChannels.put(id,event.getChannel());
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }
    public static Category getCategorybyEvent(MessageReceivedEvent event) {
        for (Category category : event.getGuild().getCategories()) {
            for (Channel channel: category.getChannels()) {
                if (channel.getId().equals(event.getChannel().getId())) {

                    return category;
                }


            }
        }
        return null;

    }
}
