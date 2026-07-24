package discord.bot.data;

public class Flashcard extends DataClass {
    public Flashcard(String id) {
        super(id);
    }

    public Flashcard() {
    }

    public static Flashcard acquire(DataStore data, String id) {
        return data.get(new Flashcard(id));
    }

    private String deckID;
    private String question = "";
    private String answer = "";

    final int TRUNCATED_LENGTH = 35;

    public String makeTruncatedQuestion() {
        if (question.length() <= TRUNCATED_LENGTH) {
            return question;
        }
        return question.substring(0, TRUNCATED_LENGTH) + "...";
    }

    public String getDeckID() {
        return deckID;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setDeckID(String deckID) {
        this.deckID = deckID;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String makeFullID() {
        return "flashcard:" + getRawID();
    }

}
