package de.stynxyxy.angrybot.bot.command;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandOption {
    private OptionData optionData;
    public CommandOption(OptionType type, String name, String description) {
        this(type,name,description,false);
    }
    public CommandOption(OptionType type, String name, String description, boolean required) {
        this(type,name,description,required,false);

    }
    public CommandOption(OptionType type, String name, String description, boolean required, boolean autocomplete) {
        this.optionData = new OptionData(type,name,description,required,autocomplete);
    }

    public OptionData getOptionData() {
        return optionData;
    }

    public String getName() {
        return this.optionData.getName();
    }
    public String getDescription() {
        return this.optionData.getDescription();
    }
    public OptionType getType() {
        return this.optionData.getType();
    }

    public OptionData setOptionData(OptionData data) {
        this.optionData = data;
        return this.optionData;
    }
}
