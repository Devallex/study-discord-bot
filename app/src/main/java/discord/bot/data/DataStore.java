package discord.bot.data;

import java.io.File;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;

/* 
The data store is the class which manages a key-value "database" to persist
all user/bot data between restarts.
*/
public class DataStore {
    final String DataPath = "../data.json"; // Where the data is kept.
    final String ClassPrefix = "class:";

    HashMap<String, String> data = new HashMap<>();

    private void setInternal(String key, String value) {
        data.put(key, value);
    }

    private void deleteInternal(String key) {
        data.remove(key);
    }

    public void set(String key, String value) {
        setInternal(key, value);
        save();
    }

    public String get(String key) {
        return data.get(key);
    }

    // Write a data class to the database
    public void push(DataClass object) {
        pushInternal(object);
        save();
    }

    public void delete(DataClass... objects) {
        for (DataClass object : objects) {
            deleteInternal(object);
        }
        save();
    }

    public void deleteInternal(DataClass object) {
        final String id = object.makeFullID();
        final String key = ClassPrefix + id;

        deleteInternal(key);
    }

    private void pushInternal(DataClass object) {
        if (object.deleted) {
            deleteInternal(object);
            return;
        }

        if (object.getRawID() == null) {
            System.out.println("Attempt to save object with null ID");
            return;
        }

        final String id = object.makeFullID();
        final String key = ClassPrefix + id;

        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(object);
            setInternal(key, json);
        } catch (Exception e) {
            System.out.println("WARNING: Failed to save DataClass " + key);
            return;
        }
    }

    public void push(DataClass... objects) {
        for (DataClass object : objects) {
            pushInternal(object);
        }
        save();
    }

    // Returns true if succeeded.
    public <T extends DataClass> T get(T object) {
        final String key = ClassPrefix + object.makeFullID();

        String json = get(key);
        if (json == null) {
            object.setInitial(true);
            object.setup();
            return object;
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            object = (T) (mapper.readValue(json, object.getClass()));
            object.setInitial(false);
            return object;
        } catch (Exception e) {
            System.out.println("WARNING: Failed to retrieve DataClass " + key);
            System.out.print("Exception: ");
            System.out.println(e);
        }
        return null;
    }

    // Write to disk
    private void save() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            String json = mapper.writeValueAsString(data);

            File output = new File(DataPath);
            Files.write(json.getBytes(), output);

        } catch (Exception e) {
            System.out.println("WARNING: Failed to write to disk:");
            System.out.println(e);
            return;
        }
        System.out.println("Saved data!");
    }

    @SuppressWarnings("unchecked")
    private void load() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            File input = new File(DataPath);
            Scanner scanner = new Scanner(input);
            String json = "";
            while (scanner.hasNextLine()) {
                json += scanner.nextLine() + "\n";
            }
            scanner.close();

            data = mapper.readValue(json, (new HashMap<String, String>()).getClass());
        } catch (Exception e) {
            System.out.println("Using blank data file.");
        }
    }

    public String randomID() {
        return UUID.randomUUID().toString();
    }

    public DataStore() {
        load();
    }
}
