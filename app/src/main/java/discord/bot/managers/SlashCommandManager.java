package discord.bot.managers;

import java.util.ArrayList;
import java.util.Collection;

import javax.sql.DataSource;

import org.jetbrains.annotations.NotNull;

import discord.bot.commands.SlashCommand;
import discord.bot.data.DataStore;
import discord.bot.data.MessageData;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class SlashCommandManager extends BaseManager {
    private ArrayList<SlashCommand> slashCommands = new ArrayList<SlashCommand>();
    private ArrayList<SlashCommandData> slashCommandData = new ArrayList<SlashCommandData>();

    private SlashCommandListener listener = new SlashCommandListener();

    private class SlashCommandListener extends ListenerAdapter {
        public ArrayList<SlashCommand> slashCommands;
        public DataStore data;

        @Override
        public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
            for (SlashCommand command : slashCommands) {
                if (!command.getCommand().getName().equals(event.getName())) {
                    continue;
                }

                command.handle(event);

                return;
            }
            event.reply("***Command not found***").queue();
        }

        @Override
        // Decides which SlashCommand class should handle a response to a
        // ModalInteractionEvent
        // by checking which ModalIDs it claims to support
        public void onModalInteraction(@NotNull ModalInteractionEvent event) {
            String eventModalID = event.getModalId();
            if (eventModalID == null || eventModalID == "") {
                return;
            }

            for (SlashCommand command : slashCommands) {
                final String[] modalIDs = command.getModalIDs();
                for (String modalID : modalIDs) {
                    if (eventModalID.startsWith(modalID)) { // Allows suffixing custom metadata to modal ID
                                                            // optionally
                        command.handleModal(event);
                        return;
                    }
                }
            }
            event.reply("***Modal not found***").queue();
        }

        @Override
        public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
            for (SlashCommand command : slashCommands) {
                if (!command.getCommand().getName().equals(event.getName())) {
                    continue;
                }

                command.handleAutoComplete(event);

                return;
            }
        }

        @Override
        public void onButtonInteraction(ButtonInteractionEvent event) {
            MessageData message = MessageData.acquire(data, event.getMessage());

            for (SlashCommand command : slashCommands) {
                // Every event handles button events; must choose to ignore based on message
                command.handleButton(message, event);
            }
        }
    }

    public void add(SlashCommand... scs) {
        for (SlashCommand cmd : scs) {
            slashCommands.add(cmd);
            slashCommandData.add(cmd.getCommand());
        }
    }

    @Override
    public void start(JDA api, DataStore data) {
        listener.slashCommands = slashCommands;

        listener.data = data;
        for (SlashCommand cmd : slashCommands) {
            cmd.data = data;
            cmd.setup();
        }

        api.addEventListener(listener);
        api.updateCommands().addCommands(slashCommandData).queue();
    }
}
