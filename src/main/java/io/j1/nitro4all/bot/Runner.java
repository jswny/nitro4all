package io.j1.nitro4all.bot;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.*;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.object.util.Permission;
import io.j1.nitro4all.domain.MessageEmoijTuple;
import io.j1.nitro4all.domain.MessageExt;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.function.BooleanSupplier;

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
                .flatMap(MessageEmoijTuple::reactWithNitroEmojis)
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
