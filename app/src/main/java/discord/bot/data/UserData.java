package discord.bot.data;

import java.util.ArrayList;

import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

public class UserData extends DataClass {
    public UserData(String id) {
        super(id);
    }

    public UserData() {
    }

    public static UserData acquire(DataStore data, GenericInteractionCreateEvent event) {
        return data.get(new UserData(event.getUser().getId()));
    }

    private ArrayList<String> deckIDs = new ArrayList<String>();

    @Override
    public String makeFullID() {
        return "user:" + getRawID();
    }

    public ArrayList<String> getDeckIDs() {
        return deckIDs;
    }

    public ArrayList<Flashcard> makeAllFlashcards(DataStore data) {
        final ArrayList<Flashcard> cards = new ArrayList<Flashcard>();

        for (String deckID : getDeckIDs()) {
            FlashcardDeck deck = FlashcardDeck.acquire(data, deckID);

            for (String cardID : deck.getCardIDs()) {
                cards.add(Flashcard.acquire(data, cardID));
            }
        }

        return cards;
    }

    public void setDeckIDs(ArrayList<String> deckIDs) {
        this.deckIDs = deckIDs;
    }

    public void addDeckID(String deckID) {
        deckIDs.add(deckID);
    }

    public void removeDeckID(String deckID) {
        for (String otherDeckID : deckIDs) {
            if (otherDeckID.equals(deckID)) {
                deckIDs.remove(otherDeckID);
                break;
            }
        }
    }
}
