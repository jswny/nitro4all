package io.j1.nitro4all.domain;

import discord4j.core.object.entity.GuildEmoji;
import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class MessageEmoijTuple {

    public static Tuple2<Message, Flux<GuildEmoji>> build(Message msg) {
        Flux<GuildEmoji> emojis = msg
                .getGuild()
                .block()
                .getEmojis();

        return Tuples.of(msg, emojis);
    }

    public static boolean doesMessageContainNitroEmoji(Tuple2<Message, Flux<GuildEmoji>> tuple2) {
        Flux<String> emojiIdentifiers = tuple2
                .getT2()
                .filter(GuildEmoji::isAnimated)
                .map(emoji -> emojiIdentifierFromName(emoji.getName()));

        return tuple2
                .getT1()
                .getContent()
                .map(content -> emojiIdentifiers.any(content::contains).block())
                .orElse(false);
    }

    private static boolean doesMessageContainEmoji(GuildEmoji emoji, Tuple2<Message, Flux<GuildEmoji>> tuple2) {
        var result = tuple2
                .getT1()
                .getContent()
                .map(content -> doesStringContainEmoji(content, emoji))
                .orElse(false);

        return result;
    }

    public static Flux<Void> reactWithNitroEmojisFromMessage(Tuple2<Message, Flux<GuildEmoji>> tuple2) {
        Flux<GuildEmoji> emojiFlux = tuple2
                .getT2()
                .filter(emoji -> doesMessageContainEmoji(emoji, tuple2));

        Tuple2<Message, Flux<GuildEmoji>> newTuple = Tuples.of(tuple2.getT1(), emojiFlux);

        Flux<Void> result = reactWithEmojis(newTuple);

        return result;
    }

    public static Flux<Void> reactWithEmojis(Tuple2<Message, Flux<GuildEmoji>> tuple2) {
        Flux<Void> result = tuple2
                .getT2()
                .flatMap(emoji -> tuple2.getT1().addReaction(ReactionEmoji.custom(emoji)));

        return result;
    }

    private static boolean doesStringContainEmoji(String content, GuildEmoji emoji) {
        boolean result = content.contains(emojiIdentifierFromName(emoji.getName()));

        return result;
    }

    private static String emojiIdentifierFromName(String emojiName) {
        return ":" + emojiName + ":";
    }
}
