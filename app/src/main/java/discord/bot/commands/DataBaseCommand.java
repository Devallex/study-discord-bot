package discord.bot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

public class DataBaseCommand extends SlashCommand {
    @Override
    public SlashCommandData generate() {
        OptionData keyOption = new OptionData(OptionType.STRING, "key", "The key to use.").setRequired(true);
        OptionData valueOption = new OptionData(OptionType.STRING, "value", "The value to set.").setRequired(true);

        return Commands.slash("database", "(Developer tool) Set/get values in the bot's database.").addSubcommands(
                new SubcommandData("set", "Sets a value in the database.").addOptions(keyOption, valueOption),
                new SubcommandData("get", "Sets a key's value in the database.").addOptions(keyOption));
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String key = event.getOption("key").getAsString();
        String value;
        ReplyCallbackAction reply = event.reply("<error>");
        switch (event.getSubcommandName()) {
            case "set":
                value = event.getOption("value").getAsString();
                data.set(key, value);
                reply = event.reply("Set `" + key + "` to `" + value + "`");
                break;
            case "get":
                value = data.get(key);
                if (value == null) {
                    value = "**<null>**";
                } else {
                    value = "`" + value + "`";
                }
                reply = event.reply("The key `" + key + "` is " + value + "");
                break;
        }
        reply.queue();
    }
}