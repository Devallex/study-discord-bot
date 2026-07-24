package discord.bot.commands.FlashcardCommand;

import java.util.ArrayList;

import discord.bot.commands.SlashCommand;
import discord.bot.data.FlashcardDeck;
import discord.bot.data.UserData;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInput.Builder;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.InteractionCallbackAction;

public class FlashcardCommand extends SlashCommand {
	private DeckSubhandler deckSubhandler = new DeckSubhandler();

	@Override
	public void setup() {
		deckSubhandler.data = data;
	}

	@Override
	public SlashCommandData generate() {
		final OptionData deckNameOption = new OptionData(OptionType.STRING, "name",
				"The name of the deck.")
				.setRequired(true)
				.setAutoComplete(true);

		return Commands.slash("flashcard", "Manage flashcards")
				.addSubcommandGroups(
						new SubcommandGroupData("deck", "Manage flashcard decks.")
								.addSubcommands(
										new SubcommandData("new",
												"Create a new deck."),
										new SubcommandData("view",
												"View an existing deck.")
												.addOptions(deckNameOption),
										new SubcommandData("update",
												"Update or delete an existing deck.")
												.addOptions(deckNameOption)),

						new SubcommandGroupData("card", "Manage flashcards.")
								.addSubcommands(
										new SubcommandData("new",
												"Create a new card."),
										new SubcommandData("update",
												"Modify an existing card."),
										new SubcommandData("delete",
												"Delete an existing card.")),

						new SubcommandGroupData("study", "Study your flashcards.")
								.addSubcommands(
										new SubcommandData("card",
												"Study a single card."),
										new SubcommandData("deck",
												"Study a single deck, in a random order."),
										new SubcommandData("delete",
												"Delete an existing card.")));
	}

	@Override
	public void handle(SlashCommandInteractionEvent event) {
		InteractionCallbackAction<?> reply = event.reply("***Command not handled***");

		switch (event.getFullCommandName()) {
			case "flashcard deck new":
				reply = deckSubhandler.handleDeckNew(event);
				break;
			case "flashcard deck view":
				reply = deckSubhandler.handleDeckView(event);
				break;
			case "flashcard deck update":
				reply = deckSubhandler.handleDeckUpdate(event);
				break;
		}

		reply.queue();
	}

	@Override
	public String[] getModalIDs() {
		return new String[] {
				"flashcard-deck-new",
				"flashcard-deck-update"
		};
	}

	@Override
	public void handleModal(ModalInteractionEvent event) {
		final String modalID = event.getModalId();

		System.out.println(modalID);

		if (modalID.startsWith("flashcard-deck")) {
			deckSubhandler.handleModal(event);
		}
	}

	@Override
	public void handleAutoComplete(CommandAutoCompleteInteractionEvent event) {
		final String fullCommand = event.getFullCommandName();

		if (fullCommand.startsWith("flashcard deck")) {
			deckSubhandler.handleAutoComplete(event);
			return;
		}
	}
}