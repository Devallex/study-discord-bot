package discord.bot.data;

import com.google.errorprone.annotations.ForOverride;

// A data class is an object which can be saved to the Data Store
public class DataClass {
    // The ID used by the data class to identify it.
    protected String id;

    public boolean deleted = false;

    public void delete() {
        deleted = true;
    }

    @ForOverride
    public String getRawID() {
        return id; // Implemenet
    }

    @ForOverride
    public String makeFullID() {
        return "data-class:" + getRawID();
    }

    public void setRawID(String id) {
        this.id = id;
    }

    public DataClass(String id) {
        setRawID(id);
    }

    public DataClass() {
    }

    // Called when getting a null DataClass.
    // Use to set custom default logic.
    @ForOverride
    public void setup() {
        // Not used
    }
}
