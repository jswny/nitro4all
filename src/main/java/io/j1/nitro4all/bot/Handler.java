package io.j1.nitro4all.bot;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import io.j1.nitro4all.domain.MessageExt;
import reactor.core.Disposable;

import java.util.function.BooleanSupplier;

public class Handler {

    static Disposable registerStateToggleHandler(DiscordClient client, String name, BooleanSupplier toggleFn) {
        return client.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(MessageExt::isInGuild)
                .filter(MessageExt::isMemberAdmin)
                .filter(msg -> msg.getContent().map(("/n4a " + name + " toggle")::equals).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("Nitro4All " + name + " is now: " + toggleFn.getAsBoolean()))
                .subscribe();
    }
}
