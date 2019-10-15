# Nitro4All [![Build Status](https://travis-ci.com/jswny/nitro4all.svg?branch=master)](https://travis-ci.com/jswny/nitro4all)
Nitro4All is a Discord bot which allows all users to use Nitro (animated) emojis. It will react to any message containing a Nitro-only emoji from a user who does not have Nitro with the emoji itself.

Nito4All only works with Nitro emojis from the server in which the message it is reacting to had been sent.

![Demo GIF](images/demo.gif)

## Clyde Bot
Since users without Nitro cannot use Nitro emojis directly, they may sometimes get messages from the built-in Discord bot named "Clyde". There is no way to disable this.

## Server Setup
Due to limitations of the [Discord4J bot library](https://github.com/Discord4J/Discord4J), in order for Nitro4All to properly check if a user has Nitro, all Nitro users in the server must be a member of the default, Discord managed `Nitro Booster` role.

## Commands
### Admin commands
These commands are only available to users who have Administrator permissions.
- `/n4a ping` - Pings Nitro4All, which will respond with `Pong!` if it is online.
- `/n4a nitrocheck toggle` - Toggles checking if a user is Nitro before deciding to potentially react to the user's message. This option effectively allows Nitro4All to react to users who are Nitro already as well as normal users if it is set to `false`.
- `/n4a enabled toggle` - Toggles whether the bot is enabled.
  
## Running the Bot
### Maven Exec
Nitro4All can be run directly from Maven without the need to build a JAR using the [Exec Maven Plugin](https://www.mojohaus.org/exec-maven-plugin/). This method is most convenient for local testing.

To run Nitro4All with this plugin, you must have the following dependencies installed:
- JDK 13
- Maven
You can run the bot as follows:
1. Run the bot with the `exec:java` command including your Discord token:
```sh
DISCORD_TOKEN=<TOKEN> mvn exec:java
```

### Maven
To run Nitro4All with Maven you must have the following dependencies installed:
- JDK 13
- Maven

You can run the bot as follows:
1. Build the JAR: 
```sh
mvn clean install
```
2. Run the JAR including your Discord token: 
```sh
DISCORD_TOKEN=<TOKEN> java -jar target/nitro4all-1.0-SNAPSHOT-fat.jar
```
  - **Note:** the `DISCORD_TOKEN` environment variable must be set appropriately before running the JAR.

### Docker
To run Nitro4All with Docker you must have the following dependencies installed:
- Docker

You can run the bot as follows:
1. Bring the bot up with Docker Compose including your Discord token:
```sh
DISCORD_TOKEN=<TOKEN> docker-compose up -d
```
