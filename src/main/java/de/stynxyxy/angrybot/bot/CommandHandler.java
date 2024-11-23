package de.stynxyxy.angrybot.bot;

import de.stynxyxy.angrybot.bot.command.*;
import de.stynxyxy.angrybot.networking.Server;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class CommandHandler extends ListenerAdapter {
    private final ArrayList<Command> commands;
    private final Server server;



    public CommandHandler(Server server) {
        this.server = server;
        this.commands = new ArrayList<>();
        registerCommands();
    }

    public void registerCommands() {
        commands.add(new GetIpCommand(this.server));
        commands.add(new StopRestartCommand(this.server));
        commands.add(new SendAllCommand(this.server));
        commands.add(new SendCommand(this.server));
    }

    public CommandData registerCommand(Command command) {
        return command.getCommandData();
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        ArrayList<CommandData> commandsList = new ArrayList<>();
        for (Command command : this.commands) {
            commandsList.add(command.getCommandData());
            System.out.println("register Command: " + command.getCommandData().getName());
        }
        event.getGuild().updateCommands().addCommands(commandsList).queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        System.out.println("slash Command run!");
        for (Command command : this.commands) {
            if (event.getName().equalsIgnoreCase(command.getCommandData().getName())) {
                command.run(event);
            }
        }
    }
}
