package discord.bot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class MyCommand extends SlashCommand {
    @Override
    public SlashCommandData generate() {
        return Commands.slash("mycommand", "description");
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        event.reply("HEELLO!!").queue();
    }
}