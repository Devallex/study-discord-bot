package discord.bot.data;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class FlashcardDeck extends DataClass {
    public FlashcardDeck(String id) {
        super(id);
    }

    public FlashcardDeck() {
    }

    public static FlashcardDeck acquire(DataStore data, String id) {
        return data.get(new FlashcardDeck(id));
    }

    private String name = "";
    private String description = "";
    private ArrayList<String> cardIDs = new ArrayList<String>();

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getCardIDs() {
        return cardIDs;
    }

    public void setName(String newName) {
        name = newName;
    }

    public void setDescription(String newDescription) {
        if (newDescription == null) {
            newDescription = "";
            return;
        }
        description = newDescription;
    }

    @Override
    public String makeFullID() {
        return "flashcard-deck:" + getRawID();
    }

}
