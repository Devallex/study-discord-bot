package discord.bot.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.errorprone.annotations.ForOverride;

// A data class is an object which can be saved to the Data Store
public class DataClass {
    // The ID used by the data class to identify it.
    @JsonIgnoreProperties({ "deleted", "initial" })

    protected String id;

    public boolean deleted = false;

    public void delete() {
        deleted = true;
        // When being pushed, data store knows not to save it
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

    private boolean initial = false;

    // Returns true if this object did not exist when loaded, and contains the
    // default state
    public boolean isInitial() {
        return initial;
    }

    public void setInitial(boolean initial) {
        this.initial = initial;
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
