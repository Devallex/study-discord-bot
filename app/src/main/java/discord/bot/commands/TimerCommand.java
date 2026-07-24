package discord.bot.commands;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAmount;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class TimerCommand extends SlashCommand {
    @Override
    public SlashCommandData generate() {
        return Commands
                .slash("timer", "Runs a timer that sends a message in the channel when it's done.")
                .addOption(OptionType.INTEGER, "hours", "Add x hours to the timer.")
                .addOption(OptionType.INTEGER, "minutes", "Add x minutes to the timer.")
                .addOption(OptionType.INTEGER, "seconds", "Add x seconds to the timer.");
    }

    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        OptionMapping hoursOpt = event.getOption("hours");
        OptionMapping minutesOpt = event.getOption("minutes");
        OptionMapping secondsOpt = event.getOption("seconds");

        long hours = 0;
        long minutes = 0;
        long seconds = 0;

        if (hoursOpt != null) {
            hours = hoursOpt.getAsLong();
        }
        if (minutesOpt != null) {
            minutes = minutesOpt.getAsLong();
        }
        if (secondsOpt != null) {
            seconds = secondsOpt.getAsLong();
        }

        LocalDateTime date = LocalDateTime.now(
                ZoneId.of("UTC")).plusSeconds(seconds)
                .plusMinutes(minutes)
                .plusHours(hours);
        long totalSeconds = date.toEpochSecond(ZoneOffset.ofHours(0));
        String totalSecondsString = String.valueOf(totalSeconds);

        long totalDuration = (hours * 3600) + (minutes * 60) + (seconds);

        String userID = event.getUser().getId();

        // <t:1784492700:R>
        event.reply("Timer will go off " + "<t:" + totalSecondsString + ":R>").queue(hook -> {
            scheduler.schedule(() -> {
                hook.editOriginal("Timer completed!").queue();
                hook.retrieveOriginal().queue(message -> {
                    message.reply("<@" + userID + ">: Beep beep beep! Timer complete").queue();
                });
            }, totalDuration, TimeUnit.SECONDS);
        });

        // event.reply("Beep beep beep! Timer went off!").queue();
    }
}
