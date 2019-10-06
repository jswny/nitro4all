import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.*;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.object.util.Permission;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class Runner {
    public static void main(String[] args) {
        String token = getToken();
        final DiscordClient client = new DiscordClientBuilder(token).build();

        State state = new State();

        registerHandlers(client, state);

        client.login().block();
    }

    static void registerHandlers(DiscordClient client, State state) {
        client.getEventDispatcher().on(ReadyEvent.class)
                .subscribe(ready -> System.out.println("Logged in as " + ready.getSelf().getUsername()));

        client.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(Runner::isMessageInGuild)
                .filter(Runner::isAdmin)
                .filter(msg -> msg.getContent().map("/n4a ping"::equals).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("Pong!"))
                .subscribe();

        client.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(Runner::isMessageInGuild)
                .filter(Runner::isAdmin)
                .filter(msg -> msg.getContent().map("/n4a nitrocheck toggle"::equals).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("Nitro checking for users is now: " + state.toggleNitroCheck()))
                .subscribe();

        client.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(Runner::isMessageInGuild)
                .filter(Runner::isAdmin)
                .filter(msg -> msg.getContent().map("/n4a enabled toggle"::equals).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("Nitro4All enabled is now: " + state.toggleEnabled()))
                .subscribe();

        client.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(Runner::isMessageInGuild)
                .filter(msg -> state.getEnabled())
                .filter(msg -> !state.getNitroCheck() || !isMemberNitro(msg))
                .map(Runner::buildMessageEmojiTuple)
                .filter(Runner::doesMessageEmojiTupleContainNitroEmoji)
                .flatMap(Runner::reactWithNitroEmojis)
                .subscribe();
    }


    static boolean isMessageInGuild(Message msg) {
        boolean result = msg
                .getGuild()
                .hasElement()
                .blockOptional()
                .orElse(false);

        return result;
    }

    static boolean isAdmin(Message message) {
        boolean result = message
                .getAuthorAsMember()
                .flatMap(Member::getBasePermissions)
                .map(permissions -> permissions.contains(Permission.ADMINISTRATOR))
                .blockOptional()
                .orElse(false);

        return result;
    }

    static String getToken() {
        String token = System.getenv("DISCORD_TOKEN");
        if (token == null) {
            throw new RuntimeException("Environment variable $DISCORD_TOKEN was not set!");
        }
        return token;
    }

    static String emojiIdentifierFromName(String emojiName) {
        return ":" + emojiName + ":";
    }

    static boolean isMemberNitro(Message msg) {
        boolean result = msg
                .getAuthorAsMember()
                .flatMap(a -> a == null ? Mono.empty() : Mono.just(a))
                .flatMapMany(Member::getRoles)
                .filter(Role::isManaged)
                .map(Role::getName)
                .any("Nitro Booster"::equals)
                .blockOptional()
                .orElse(false);

        return result;
    }

    static Tuple2<Message, Flux<GuildEmoji>> buildMessageEmojiTuple(Message msg) {
        Flux<GuildEmoji> emojis = msg
                .getGuild()
                .block()
                .getEmojis();

        return Tuples.of(msg, emojis);
    }

    static boolean doesMessageEmojiTupleContainNitroEmoji(Tuple2<Message, Flux<GuildEmoji>> tuple2) {
        Flux<String> emojiIdentifiers = tuple2
                .getT2()
                .filter(GuildEmoji::isAnimated)
                .map(emoji -> emojiIdentifierFromName(emoji.getName()));

        return tuple2
                .getT1()
                .getContent()
                .map(content -> emojiIdentifiers.any(emojiIdentifier -> content.contains(emojiIdentifier)).block())
                .orElse(false);
    }

    static Flux<Void> reactWithNitroEmojis(Tuple2<Message, Flux<GuildEmoji>> tuple2) {
         Flux<Void> result = tuple2
                .getT2()
                .filter(emoji -> doesMessageContainEmoji(emoji, tuple2))
                .flatMap(emoji -> tuple2.getT1().addReaction(ReactionEmoji.custom(emoji)));

        return result;
    }

    static boolean doesMessageContainEmoji(GuildEmoji emoji, Tuple2<Message, Flux<GuildEmoji>> tuple2) {
        var result = tuple2
                .getT1()
                .getContent()
                .map(content -> doesStringContainEmoji(content, emoji))
                .orElse(false);

        return result;
    }

    static boolean doesStringContainEmoji(String content, GuildEmoji emoji) {
        boolean result = content.contains(emojiIdentifierFromName(emoji.getName()));

        return result;
    }
}
