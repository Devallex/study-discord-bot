package discord.bot.commands;

import org.jetbrains.annotations.NotNull;

import com.google.errorprone.annotations.ForOverride;

import discord.bot.data.DataStore;
import discord.bot.data.MessageData;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;

/* 
Abstract class which other SlashCommands are made from.
*/
public abstract class SlashCommand {
    public DataStore data;

    private SlashCommandData command = null;

    public SlashCommandData getCommand() {
        return command;
    }

    // Run after DataStore available on startup
    @ForOverride
    public void setup() {
        // Blank
    }

    public SlashCommandData generate() {
        return Commands.slash("null", "null command");
    }

    // Return an array of ModalIDs that should be handled exclusivley by this
    // command. Will call handleModal accordingly.
    public String[] getModalIDs() {
        return new String[] {};
    }

    public void handle(@NotNull SlashCommandInteractionEvent event) {
        // Override to handle receiving a the specific slash command.
    }

    public void handleModal(@NotNull ModalInteractionEvent event) {
        // Override to handle modal responses
        // Requires the modals to have modalIDs specifed in getModalIDs()
    }

    public void handleButton(@NotNull MessageData message, @NotNull ButtonInteractionEvent event) {
        // Override to handle command button interactions
    }

    public void handleAutoComplete(@NotNull CommandAutoCompleteInteractionEvent event) {
        // Override to handle command autocompletes
    }

    public SlashCommand() {
        command = generate();
    }
}
