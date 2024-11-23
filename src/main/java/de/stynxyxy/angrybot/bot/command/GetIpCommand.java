package de.stynxyxy.angrybot.bot.command;

import de.stynxyxy.angrybot.networking.Server;
import de.stynxyxy.angrybot.util.IPUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class GetIpCommand extends Command {
    private final Server server;

    public GetIpCommand(Server server) {
        super("getip", "Returns the server IP", new CommandOption(OptionType.INTEGER,"session-id","The Session Id!",true));
        this.server = server;

    }

    @Override
    public void run(SlashCommandInteractionEvent event) {
        int id = event.getOption("session-id").getAsInt();
        try {
            if (server != null && !server.getSessions().isEmpty() ) {


                event.reply("Client IP: " + server.getSessions().get(id).IPAdress).setEphemeral(true).queue();
            } else {
                event.reply("No clients connected.").setEphemeral(true).queue();
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Session with this Id is not existing!");
            event.reply("There is no Session with this Id!").setEphemeral(true).queue();
            
        }

    }
}
