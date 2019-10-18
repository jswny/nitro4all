package io.j1.nitro4all.bot;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.*;
import discord4j.core.object.util.Snowflake;
import io.j1.nitro4all.domain.MessageEmoijTuple;
import io.j1.nitro4all.domain.MessageExt;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.lang.Math;

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
                .filter(MessageExt::isInGuild)
                .filter(MessageExt::isMemberAdmin)
                .filter(msg -> msg.getContent().map("/n4a ping"::equals).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("Pong!"))
                .subscribe();

        client.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(MessageExt::isInGuild)
                .filter(msg -> state.getEnabled())
                .filter(msg -> !state.getNitroCheck() || !MessageExt.isMemberNitro(msg))
                .map(MessageEmoijTuple::build)
                .filter(MessageEmoijTuple::doesMessageContainNitroEmoji)
                .flatMap(MessageEmoijTuple::reactWithNitroEmojisFromMessage)
                .subscribe();

        String addName = "epics";

        client.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(MessageExt::isInGuild)
                .filter(msg -> state.getEnabled())
                .filter(MessageExt::isMemberAdmin)
                .filter(msg -> msg.getContent().map(content -> content.startsWith("/n4a " + addName + " add")).orElse(false))
                .map(msg -> Tuples.of(msg, msg.getUserMentions()))
                .map(tuple -> {
                    Snowflake guildId = tuple.getT1().getGuild().map(Guild::getId).block();
                    Flux<Member> memberFlux = tuple.getT2().flatMap(user -> user.asMember(guildId));
                    return Tuples.of(tuple.getT1(), memberFlux);
                })
                .map(tuple -> {
                    state.addAllEpics(tuple.getT2().map(Member::getId).collect(Collectors.toSet()).block());
                    return tuple;
                })
                .map(tuple -> tuple.mapT1(Message::getChannel))
                .flatMap(tuple -> tuple.getT1().flatMap(channel -> channel.createMessage("Nitro4All added epic users: " + tuple.getT2().map(Member::getNicknameMention).collect(Collectors.joining(", ")).block())))
                .subscribe();

        client.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(MessageExt::isInGuild)
                .filter(MessageExt::isMemberAdmin)
                .filter(msg -> msg.getContent().map(("/n4a " + addName + " get")::equals).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("Nitro4All epic users: " + state.getEpics().stream().map(Snowflake::toString).collect(Collectors.joining(", "))))
                .subscribe();

        client.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(MessageExt::isInGuild)
                .filter(message -> message.getAuthor().map(author -> state.isEpic(author.getId())).orElse(false))
                .map(MessageEmoijTuple::build)
                .flatMap(tuple -> {
                    List<GuildEmoji> emojiList = tuple.getT2().collect(Collectors.toList()).block();
                    Collections.shuffle(emojiList);
                    List<GuildEmoji> subItems = emojiList.subList(0, 10);
                    Flux<GuildEmoji> emojis = Flux.fromIterable(subItems);
                    return MessageEmoijTuple.reactWithEmojis(Tuples.of(tuple.getT1(), emojis));
                })
                .subscribe();

        Handler.registerStateToggleHandler(client, "enabled", state::toggleEnabled);
        Handler.registerStateToggleHandler(client, "nitrocheck", state::toggleNitroCheck);
    }

    static String getToken() {
        String token = System.getenv("DISCORD_TOKEN");
        if (token == null) {
            throw new RuntimeException("Environment variable $DISCORD_TOKEN was not set!");
        }
        return token;
    }
}
