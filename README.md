# Discord Study Bot

## How to Run the Source Code

(*Note: You should have Java already running on your machine*)

1. Create a new Discord application
[[https://discord.com/developers/applications]]

1. In **General Information**, look for the **Application ID** and click copy.

2. In the following link, replace `YOUR_APPLICATION_ID` with the application ID of your bot. Then open the link and add it to your server.

```https://discord.com/oauth2/authorize?client_id=YOUR_APPLICATION_ID&permissions=8&integration_type=0&scope=bot+applications.commands```

4. Generate a Discord bot token on the **Bot** page, by clicking **Reset Token**.

5. Create a file called `discord.secret` in the root directory (sibling to this README.md file),
and paste that token there.

6. Run `chmox +x gradlew` to make gradlew executable (macOS/linux)

7. You should be able to run `./gradlew run` in a terminal inside the root directory of this project.

8. After the bot loads into your server, force quit and reopen discord to make sure the slash commands load.

9. You should be able to use the bot's slash commands.

## Source Code Structure


```
/
    /app/src/main/java/discord/bot
        /commands
            Contains all command files, which are triggered
            to handle bot interaction events

        /data
            Contains DataStore, which manages all data,
            as well as multiple DataClasses,
            which can be saved to the database.

            DataStore.Java
            DataClass.Java

            (Data stored with Discord IDs)
            UserData.java
            MessageData.java

            (Custom data)
            Flashcard.java
            FlashcardDeck.java
            StudySession.java

        /managers 
            BaseManager.java
            SlashCommandManager.java

        App.java — Entrypoint
    data.json — Automatically created by DataStore
    discord.token — Manually created (contains Bot token)
```