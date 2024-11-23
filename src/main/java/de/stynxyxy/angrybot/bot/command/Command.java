package de.stynxyxy.angrybot.bot.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.*;


public abstract class Command {
    private final CommandData commandData;
    private CommandOption[] commandOptions;
    public Command(String name, String description) {
        this.commandData = Commands.slash(name,description);
    }
    public Command(String name, String description, CommandOption option) {

        this.commandData = Commands.slash(name,description).addOptions(List.of(option.getOptionData()));
    }
    public Command(String name, String description, CommandOption[] options) {
        Collection<OptionData> dataCollection = new ArrayList<>();
        for (CommandOption option: options) {
            dataCollection.add(option.getOptionData());
        }
        this.commandData = Commands.slash(name,description).addOptions(dataCollection);
    }
    public Command(String name, String description, ArrayList<CommandOption> options) {
        Collection<OptionData> dataCollection = new ArrayList<>();
        for (CommandOption option: options) {
            dataCollection.add(option.getOptionData());
        }
        this.commandData = Commands.slash(name,description).addOptions(dataCollection);
    }



    public abstract void run(SlashCommandInteractionEvent commandInteractionEvent);


    public CommandData getCommandData() {
        return commandData;
    }
}
