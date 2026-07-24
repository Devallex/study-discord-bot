package discord.bot.commands.FlashcardCommand;

import java.util.ArrayList;

import discord.bot.data.DataStore;
import discord.bot.data.Flashcard;
import discord.bot.data.FlashcardDeck;
import discord.bot.data.UserData;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.InteractionCallbackAction;

public class StudySubhandler {
    public DataStore data;

    // region Slash Command
    public InteractionCallbackAction<?> handleCardNew(SlashCommandInteractionEvent event) {
        return event.replyModal(
                Modal.create("flashcard-card-new:" + event.getOption("deck-name").getAsString(),
                        "New Flashcard")
                        .addComponents(
                                Label.of(
                                        "Question",
                                        TextInput.create("question",
                                                TextInputStyle.PARAGRAPH)
                                                .setMinLength(1)
                                                .setMaxLength(500)
                                                .setRequired(true)
                                                .build()),

                                Label.of(
                                        "Answer",
                                        TextInput.create("answer",
                                                TextInputStyle.PARAGRAPH)
                                                .setMinLength(1)
                                                .setMaxLength(500)
                                                .setRequired(true)
                                                .build()))

                        .build());
    }

    public InteractionCallbackAction<?> handleCardView(SlashCommandInteractionEvent event) {
        final String cardID = event.getOption("card-name").getAsString();
        final Flashcard card = Flashcard.acquire(data, cardID);

        final String question = card.getQuestion();
        final String answer = card.getAnswer();

        return event.reply(String.format("""
                **Flashcard**
                Question: ```%s```
                Answer: ||```%s```||
                (ID: `%s`)
                """, question, answer, cardID));
    }

    public InteractionCallbackAction<?> handleCardUpdate(SlashCommandInteractionEvent event) {
        final String cardID = event.getOption("card-name").getAsString();
        final Flashcard card = Flashcard.acquire(data, cardID);

        SelectOption updateOption = SelectOption.of("Update", "update");
        SelectOption deleteOption = SelectOption.of("Delete", "delete");

        return event.replyModal(

                Modal.create("flashcard-card-update:" + cardID, "Update Flashcard")
                        .addComponents(
                                Label.of("Operation",
                                        StringSelectMenu.create("operation")
                                                .addOptions(updateOption,
                                                        deleteOption)
                                                .setDefaultOptions(
                                                        updateOption)
                                                .setRequired(true)
                                                .build()),
                                Label.of(
                                        "Question",
                                        TextInput.create("question",
                                                TextInputStyle.PARAGRAPH)
                                                .setMinLength(1)
                                                .setMaxLength(500)
                                                .setRequired(true)
                                                .setValue(card.getQuestion())
                                                .build()),

                                Label.of(
                                        "Answer",
                                        TextInput.create("answer",
                                                TextInputStyle.PARAGRAPH)
                                                .setMinLength(1)
                                                .setMaxLength(500)
                                                .setRequired(true)
                                                .setValue(card.getAnswer())
                                                .build()))
                        .build());
    }

    // endregion

    // region Modal
    public void handleModal(ModalInteractionEvent event) {
        final Flashcard card;
        final String cardID;

        final String modalID = event.getModalId();

        if (modalID.startsWith("flashcard-card-new:")) {
            String deckID = modalID.split(":")[1];
            FlashcardDeck deck = FlashcardDeck.acquire(data, deckID);

            String question = event.getValue("question").getAsString();
            String answer = event.getValue("answer").getAsString();

            card = new Flashcard(data.randomID());
            card.setDeckID(deck.getRawID());
            card.setQuestion(question);
            card.setAnswer(answer);

            deck.addFlashcard(card);

            data.push(deck, card);

            event.reply(String.format("Created card **%s**!", card.makeTruncatedQuestion())).queue();
        } else if (modalID.startsWith("flashcard-card-update:")) {
            cardID = modalID.split(":")[1];
            card = Flashcard.acquire(data, cardID);

            if (event.getValue("operation").getAsStringList().get(0).equals("delete")) {
                FlashcardDeck deck = FlashcardDeck.acquire(data, card.getDeckID());

                deck.removeFlashcard(card);
                card.delete();
                data.push(deck, card);

                event.reply(String.format("Deleted card **%s**", card.makeTruncatedQuestion())).queue();
                return;
            }

            card.setQuestion(event.getValue("question").getAsString());
            card.setAnswer(event.getValue("answer").getAsString());

            data.push(card);
            event.reply(String.format("Updated card **%s**", card.makeTruncatedQuestion())).queue();
            ;
        }
    }
    // endregion

    // region Autocomplete
    public void handleAutoComplete(CommandAutoCompleteInteractionEvent event) {
        UserData user = UserData.acquire(data, event);

        final AutoCompleteQuery option = event.getFocusedOption();
        final String optionValue = option.getValue().toLowerCase();

        ArrayList<Choice> choices = new ArrayList<Choice>();
        for (Flashcard card : user.makeAllFlashcards(data)) {
            final String question = card.getQuestion().toLowerCase();
            final String answer = card.getAnswer().toLowerCase();

            // If searching doesn't exclude this item
            if (question.contains(optionValue)
                    || answer.contains(optionValue)) {
                if (card.isInitial()) {
                    continue;
                }
                choices.add(new Choice(card.makeTruncatedQuestion(), card.getRawID()));
            }
        }
        event.replyChoices(choices).queue();
    }
    // endregion
}