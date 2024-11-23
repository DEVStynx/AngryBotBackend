package de.stynxyxy.angrybot.bot.events;

import de.stynxyxy.angrybot.Main;
import de.stynxyxy.angrybot.bot.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

public class BotStartListener extends ListenerAdapter {
    public Bot bot;
    public BotStartListener(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        for (Guild guild : this.bot.getJda().getGuilds()) {
            this.bot.deleteSessionsInServer(guild);
            List<Role> debugRoles = guild.getRolesByName("DebugRole",true);
            if (debugRoles.isEmpty()) {
                guild.createRole().setName("DebugRole").setColor(Color.GREEN).setMentionable(false).setPermissions(1L).queue();
            }
        }

    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        for (Guild guild : this.bot.getJda().getGuilds()) {
            this.bot.deleteSessionsInServer(guild);

        }

    }


}
