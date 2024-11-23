package de.stynxyxy.angrybot.bot.command;

import de.stynxyxy.angrybot.networking.Server;
import de.stynxyxy.angrybot.networking.Session;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class SendAllCommand extends Command{
    Server server;
    public SendAllCommand(Server server) {

        super("sendall","Sends a Command to All Sessions",new CommandOption(OptionType.STRING,"command","The Command to send!",true));

        this.server = server;
    }


    @Override
    public void run(SlashCommandInteractionEvent commandInteractionEvent) {
        for (Session session : server.getSessions()) {
            session.sendCommand(commandInteractionEvent.getOption("command").getAsString());
            System.out.println("Sent A Command to the Client "+session.getUserid());
        }
        commandInteractionEvent.reply("Sent The Command to all Users!").setEphemeral(true).queue();
    }
}
