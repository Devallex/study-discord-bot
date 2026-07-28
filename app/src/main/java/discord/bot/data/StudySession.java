package discord.bot.data;

import java.util.ArrayList;

public class StudySession extends DataClass {
    public StudySession(String id) {
        super(id);
    }

    public StudySession() {
    }

    String messageId;
    int correct;
    int incorrect;

    public int countTotalSoFar() {
        return correct + incorrect;
    }

    public int countTotalCards() {
        return cardIDs.size();
    }

    public FlashcardDeck acquireDeck(DataStore data) {
        return FlashcardDeck.acquire(data, deckID);
    }

    public static StudySession acquire(DataStore data, String id) {
        return data.get(new StudySession(id));
    }

    private String deckID;
    private String messageID; // Most recent message in study thread
    private int cardIndex = 0; // Current card being studied
    private ArrayList<String> cardIDs = new ArrayList<String>();

    public void setupDeck(DataStore data) {
        final FlashcardDeck deck = acquireDeck(data);
        for (String cardID : deck.getCardIDs()) {
            // Shuffle order of cards
            int randomIndex = (int) (Math.random() * (cardIDs.size() + 1));
            cardIDs.add(randomIndex, cardID);
        }
    }

    public Flashcard makeNextCard(DataStore data) {
        if (cardIndex >= cardIDs.size()) {
            return null;
        }
        Flashcard card = Flashcard.acquire(data, cardIDs.get(cardIndex));
        cardIndex++;
        return card;
    }

    public double calculateAccuracy() {
        return ((double) correct / (double) (correct + incorrect)) * 100.0;
    }

    public String getDeckID() {
        return deckID;
    }

    public String getMessageID() {
        return messageID;
    }

    public int getCorrect() {
        return this.correct;
    }

    public int getIncorrect() {
        return this.incorrect;
    }

    public int getCardIndex() {
        return this.cardIndex;
    }

    public ArrayList<String> getCardIDs() {
        return cardIDs;
    }

    public boolean checkMessageUpToDate(MessageData message) {
        return message.getRawID().equals(messageID);
    }

    public void setDeckID(String deckID) {
        this.deckID = deckID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public void setIncorrect(int incorrect) {
        this.incorrect = incorrect;
    }

    public void setCardIndex(int cardIndex) {
        this.cardIndex = cardIndex;
    }

    public void setCardIDs(ArrayList<String> cardIDs) {
        this.cardIDs = cardIDs;
    }

    @Override
    public String makeFullID() {
        return "study-session:" + getRawID();
    }

}
