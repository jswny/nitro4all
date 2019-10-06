# Nitro4All
Nitro4All is a Discord bot which allows all users to use Nitro animated emojis. It will react to any message containing a Nitro-only emoji from a user who does not have Nitro with the emoji itself.

![Demo GIF](images/demo.gif)

## Server Setup
Due to limitations of the [Discord4J bot library](https://github.com/Discord4J/Discord4J), in order for Nitro4All to properly check if a user has Nitro, all Nitro users in the server must be a member of the default, Discord managed `Nitro Booster` role.

## Commands
- `/n4a ping` - Pings Nitro4All, which will respond with `Pong!` if it is online.
- `/n4a nitrocheck toggle` - Toggles checking if a user is Nitro before deciding to potentially react to the user's message. This option effectively allows Nitro4All to react to users who are Nitro already as well as normal users if it is set to `false`.
  - **Note**: this command is only available to administrators of the server.
- `/n4a enabled toggle` - Toggles whether the bot is enabled.
  - **Note**: this command is only available to administrators of the server.
