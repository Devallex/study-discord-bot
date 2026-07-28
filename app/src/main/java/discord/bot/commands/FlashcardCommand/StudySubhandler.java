package discord.bot.commands.FlashcardCommand;

import java.util.ArrayList;
import discord.bot.data.DataStore;
import discord.bot.data.Flashcard;
import discord.bot.data.MessageData;
import discord.bot.data.StudySession;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.requests.restaction.interactions.InteractionCallbackAction;

public class StudySubhandler {
	public DataStore data;

	// region Slash Command
	public void handleStudyNew(SlashCommandInteractionEvent event) {
		StudySession session = new StudySession(data.randomID());

		final String deckID = event.getOption("deck-name").getAsString();
		session.setDeckID(deckID);
		session.setupDeck(data);

		replyWithCard(session, event);

	}
	// endregion

	private void replyWithCard(StudySession session, IReplyCallback event) {
		Flashcard card = session.makeNextCard(data);

		if (card == null) {
			final String deckName = session.acquireDeck(data).getName();

			session.delete();
			data.push(session);

			if (session.countTotalCards() == 0) {
				// System.out.println("THERE ARE NO CARDS #####");
				// System.out.println(session.getCardIndex());
				// System.out.println(session.getMessageID());
				// System.out.println(session.get());
				event.reply(String.format(
						"""
								There are no cards in the deck **`%s`**, try to add some!
								""", deckName)).queue();

				return;
			}

			System.out.print(session.calculateAccuracy());

			event.reply(String.format("""
					**You finished studying!**
					Deck Name: ***%s***
					Cards Studied: `%d`

					Correct: `%d` :white_check_mark:
					Incorrect: `%d` :x:

					Accuracy: **`%.2f%%`**
					""",
					deckName,
					session.countTotalCards(),
					session.getCorrect(),
					session.getIncorrect(),
					session.calculateAccuracy()))
					.queue();
			;
			return;
		}

		event.reply(
				String.format("""
						**Flashcard (%d/%d)**
						Question: ```%s```
						Answer: ||```%s```||
						""",
						session.countTotalSoFar() + 1,
						session.countTotalCards(),
						card.getQuestion(),
						card.getAnswer()))
				.addComponents(ActionRow.of(
						Button.success("correct", "Correct").withEmoji(Emoji.fromFormatted("👍")),
						Button.danger("incorrect", "Incorrect").withEmoji(Emoji.fromFormatted("👎"))))
				.queue(hook -> {
					hook.retrieveOriginal().queue(message -> {
						MessageData msg = MessageData.acquire(data, message);
						msg.setStudySessionID(session.getRawID());
						session.setMessageID(msg.getRawID());
						data.push(session, msg);
					});
				});
	}

	// region Button
	public void handleButton(MessageData message, ButtonInteractionEvent event) {
		String studySessionID = message.getStudySessionID();
		if (studySessionID == null) {
			return;
		}

		StudySession session = StudySession.acquire(data, studySessionID);

		final String componentId = event.getComponentId();

		if (componentId.equals("correct")) {
			session.setCorrect(session.getCorrect() + 1);
		} else if (componentId.equals("incorrect")) {

			session.setIncorrect(session.getIncorrect() + 1);
		} else {
			event.reply("Invalid interaction!").setEphemeral(true).queue();
			return;
		}

		event.getMessage().editMessageComponents(ActionRow.of(
				Button.success("correct", "Correct").withEmoji(Emoji.fromFormatted("👍")).asDisabled(),
				Button.danger("incorrect", "Incorrect").withEmoji(Emoji.fromFormatted("👎")))
				.asDisabled()).queue();

		if (!session.checkMessageUpToDate(message)) {
			event.reply(
					"You are replying on an old message.")
					.setEphemeral(true)
					.queue();
			return;
		}

		replyWithCard(session, event);
	}
	// endregion
}