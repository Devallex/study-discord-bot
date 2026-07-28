package discord.bot.data;

import java.util.ArrayList;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

public class MessageData extends DataClass {
    public MessageData(String id) {
        super(id);
    }

    public MessageData() {
    }

    public static MessageData acquire(DataStore data, Message msg) {
        return data.get(new MessageData(msg.getId()));
    }

    private String studySessionID;

    @Override
    public String makeFullID() {
        return "message:" + getRawID();
    }

    public String getStudySessionID() {
        return studySessionID;
    }

    public void setStudySessionID(String studySessionID) {
        this.studySessionID = studySessionID;
    }
}
