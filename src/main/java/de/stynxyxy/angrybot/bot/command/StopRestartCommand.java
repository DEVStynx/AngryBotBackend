package de.stynxyxy.angrybot.bot.command;

import de.stynxyxy.angrybot.networking.Server;
import de.stynxyxy.angrybot.networking.Session;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;

public class StopRestartCommand extends Command{

    private final Server server;
    public StopRestartCommand(Server server) {
        super("stop", "Stop All Connections and Restart Server!");
        this.server = server;
    }

    @Override
    public void run(SlashCommandInteractionEvent commandInteractionEvent) {
        this.server.stop();
        this.server.deleteSessionsInServer(commandInteractionEvent.getGuild());
        commandInteractionEvent.reply("Stopped all Connections!").setEphemeral(true).queue();
    }
}
