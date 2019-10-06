import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.GuildEmoji;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.object.util.Snowflake;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class Runner {
    public static void main(String[] args) {
        String token = System.getenv("DISCORD_TOKEN");
        if (token == null) {
            throw new RuntimeException("Environment variable $DISCORD_TOKEN was not set!");
        }

        final DiscordClient client = new DiscordClientBuilder(token).build();

        client.getEventDispatcher().on(ReadyEvent.class)
                .subscribe(ready -> System.out.println("Logged in as " + ready.getSelf().getUsername()));

        client.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(msg -> msg.getContent().map("/n4a ping"::equals).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("Pong!"))
                .subscribe();

        client.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(msg -> !msg
                        .getAuthorAsMember()
                        .flatMap(member -> member.getRoles().any(role -> role.getName().equals("Nitro Booster") && role.isManaged()))
                        .block()
                )
                .map(msg -> {
                    Flux<GuildEmoji> emojis = msg
                            .getGuild()
                            .block()
                            .getEmojis();

                    return Tuples.of(msg, emojis);
                })
                .filter(tuple2 -> {
                    Flux<String> emojiIdentifiers = tuple2
                            .getT2()
                            .filter(GuildEmoji::isAnimated)
                            .map(emoji -> emojiIdentifierFromName(emoji.getName()));

                    return tuple2
                            .getT1()
                            .getContent()
                            .map(content -> emojiIdentifiers.any(emojiIdentifier -> content.contains(emojiIdentifier)).block())
                            .orElse(false);
                })
                .flatMap(tuple2 -> {
                    GuildEmoji matchingEmoji = tuple2
                            .getT2()
                            .filter(emoji -> tuple2
                                    .getT1()
                                    .getContent()
                                    .map(content -> content
                                            .contains(emojiIdentifierFromName(emoji.getName()))).orElse(false))
                            .singleOrEmpty()
                            .block();
                    return tuple2.getT1().addReaction(ReactionEmoji.custom(matchingEmoji));
                })
                .subscribe();

        client.login().block();
    }

    public static String emojiIdentifierFromName(String emojiName) {
        return ":" + emojiName + ":";
    }
}
