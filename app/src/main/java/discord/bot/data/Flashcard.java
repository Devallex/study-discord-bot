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

    private String question = "";
    private String answer = "";

    final int TRUNCATED_LENGTH = 20;

    public String makeTruncatedQuestion() {
        if (question.length() < TRUNCATED_LENGTH) {
            return question;
        }
        return question.substring(0, TRUNCATED_LENGTH) + "...";
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setQuestion(String newQuestion) {
        question = newQuestion;
    }

    public void setAnswer(String newAnswer) {
        answer = newAnswer;
    }

    @Override
    public String makeFullID() {
        return "flashcard:" + getRawID();
    }

}
