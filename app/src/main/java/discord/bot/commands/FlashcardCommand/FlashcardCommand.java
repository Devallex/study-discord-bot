package discord.bot.commands.FlashcardCommand;

import discord.bot.commands.SlashCommand;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.requests.restaction.interactions.InteractionCallbackAction;

public class FlashcardCommand extends SlashCommand {
	private DeckSubhandler deckSubhandler = new DeckSubhandler();
	private CardSubhandler cardSubhandler = new CardSubhandler();

	@Override
	public void setup() {
		deckSubhandler.data = data;
		cardSubhandler.data = data;
	}

	@Override
	public SlashCommandData generate() {
		final OptionData deckNameOption = new OptionData(OptionType.STRING, "deck-name",
				"The name of the deck.")
				.setRequired(true)
				.setAutoComplete(true);

		final OptionData cardNameOption = new OptionData(OptionType.STRING, "card-name",
				"The name of the card.")
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
												"Create a new card.")
												.addOptions(deckNameOption),
										new SubcommandData("view",
												"View an existing card.")
												.addOptions(cardNameOption),
										new SubcommandData("update",
												"Modify an existing card.")
												.addOptions(cardNameOption)),

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
			case "flashcard card new":
				reply = cardSubhandler.handleCardNew(event);
				break;
			case "flashcard card view":
				reply = cardSubhandler.handleCardView(event);
				break;
			case "flashcard card update":
				reply = cardSubhandler.handleCardUpdate(event);
				break;
		}

		reply.queue();
	}

	@Override
	public String[] getModalIDs() {
		return new String[] {
				"flashcard-",
		};
	}

	@Override
	public void handleModal(ModalInteractionEvent event) {
		final String modalID = event.getModalId();

		System.out.println(modalID);

		if (modalID.startsWith("flashcard-deck")) {
			deckSubhandler.handleModal(event);
		} else if (modalID.startsWith("flashcard-card")) {
			cardSubhandler.handleModal(event);
		}
	}

	@Override
	public void handleAutoComplete(CommandAutoCompleteInteractionEvent event) {
		final String fullCommand = event.getFullCommandName();

		if (fullCommand.startsWith("flashcard deck") || fullCommand.equals("flashcard card new")) {
			deckSubhandler.handleAutoComplete(event);
			return;
		} else if (fullCommand.startsWith("flashcard card")) {
			cardSubhandler.handleAutoComplete(event);
		}
	}
}