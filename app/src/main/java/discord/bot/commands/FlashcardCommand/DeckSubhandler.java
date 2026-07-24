package discord.bot.commands.FlashcardCommand;

import java.util.ArrayList;

import discord.bot.data.DataStore;
import discord.bot.data.FlashcardDeck;
import discord.bot.data.UserData;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.selections.SelectMenu;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInput.Builder;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.InteractionCallbackAction;

public class DeckSubhandler {
	public DataStore data;

	// region Slash Command
	public InteractionCallbackAction<?> handleDeckNew(SlashCommandInteractionEvent event) {
		return event.replyModal(
				Modal.create("flashcard-deck-new", "New Flashcard Deck")
						.addComponents(
								Label.of(
										"Name",
										TextInput.create("name",
												TextInputStyle.SHORT)
												.setMinLength(3)
												.setMaxLength(32)
												.setRequired(true)
												.build()),

								Label.of(
										"Description",
										TextInput.create("description",
												TextInputStyle.PARAGRAPH)
												.setMinLength(0)
												.setMaxLength(512)
												.setRequired(false)
												.build()))

						.build());
	}

	public InteractionCallbackAction<?> handleDeckView(SlashCommandInteractionEvent event) {
		final String deckID = event.getOption("name").getAsString();
		final FlashcardDeck deck = FlashcardDeck.acquire(data, deckID);

		String description = deck.getDescription();
		if (description.trim().length() == 0) {
			description = "(empty)";
		}

		return event.reply(String.format("""
				Name: **%s**
				Description:
				```
				%s
				```
				(ID: `%s`)
				""", deck.getName(), description, deckID));
	}

	public InteractionCallbackAction<?> handleDeckUpdate(SlashCommandInteractionEvent event) {
		final String deckID = event.getOption("name").getAsString();
		final FlashcardDeck deck = FlashcardDeck.acquire(data, deckID);

		Builder descriptionTextInputBuilder = TextInput.create("description",
				TextInputStyle.PARAGRAPH)
				.setMinLength(0)
				.setMaxLength(512)
				.setRequired(false);

		final String description = deck.getDescription();
		if (description.length() > 0) {
			descriptionTextInputBuilder = descriptionTextInputBuilder.setValue(deck.getDescription());
		}

		SelectOption updateOption = SelectOption.of("Update", "update");
		SelectOption deleteOption = SelectOption.of("Delete", "delete");

		return event.replyModal(

				Modal.create("flashcard-deck-update:" + deckID, "New Flashcard Deck")
						.addComponents(
								Label.of("Operation",
										StringSelectMenu.create("operation")
												.addOptions(updateOption, deleteOption)
												.setDefaultOptions(updateOption)
												.setRequired(true)
												.build()),
								Label.of(
										"Name",
										TextInput.create("name",
												TextInputStyle.SHORT)
												.setMinLength(3)
												.setMaxLength(32)
												.setRequired(true)
												.setValue(deck.getName())
												.build()),

								Label.of(
										"Description",
										descriptionTextInputBuilder.build()))
						.build());
	}

	// endregion

	// region Modal
	public void handleModal(ModalInteractionEvent event) {
		final FlashcardDeck deck;
		final String deckID;

		final String modalID = event.getModalId();

		if (modalID.equals("flashcard-deck-new")) {
			UserData user = UserData.acquire(data, event);

			String name = event.getValue("name").getAsString();
			String description = event.getValue("description").getAsOptionalString();
			if (description == null) {
				description = "";
			}
			deck = new FlashcardDeck(data.randomID());
			deck.setName(name);
			deck.setDescription(description);

			user.addDeckID(deck.getRawID());

			data.push(user, deck);

			event.reply(String.format("Created deck **%s**!", name)).queue();
			;
		} else if (modalID.startsWith("flashcard-deck-update:")) {
			deckID = modalID.split(":")[1];
			deck = FlashcardDeck.acquire(data, deckID);

			if (event.getValue("operation").getAsStringList().get(0).equals("delete")) {
				UserData user = UserData.acquire(data, event);

				user.removeDeckID(deckID);
				deck.delete();
				data.push(user, deck);

				event.reply(String.format("Deleted deck **%s**", deck.getName())).queue();
				return;
			}

			deck.setName(event.getValue("name").getAsString());
			deck.setDescription(event.getValue("description").getAsOptionalString());

			data.push(deck);
			event.reply(String.format("Updated deck **%s**", deck.getName())).queue();
			;
		}
	}
	// endregion

	// region Autocomplete
	public void handleAutoComplete(CommandAutoCompleteInteractionEvent event) {
		UserData user = UserData.acquire(data, event);
		AutoCompleteQuery option = event.getFocusedOption();

		ArrayList<Choice> choices = new ArrayList<Choice>();
		for (String deckID : user.getDeckIDs()) {
			FlashcardDeck deck = FlashcardDeck.acquire(data, deckID);
			final String name = deck.getName();

			// If searching doesn't exclude this item
			if (name.toLowerCase().contains(option.getValue().toLowerCase())) {
				choices.add(new Choice(name, deck.getRawID()));
			}
		}
		event.replyChoices(choices).queue();
	}
	// endregion
}
