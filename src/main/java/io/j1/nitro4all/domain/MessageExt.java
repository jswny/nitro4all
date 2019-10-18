package io.j1.nitro4all.domain;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.util.Permission;
import reactor.core.publisher.Mono;

public class MessageExt {

    public static boolean isInGuild(Message msg) {
        boolean result = msg
                .getGuild()
                .hasElement()
                .blockOptional()
                .orElse(false);

        return result;
    }

    public static boolean isMemberAdmin(Message message) {
        boolean result = message
                .getAuthorAsMember()
                .flatMap(Member::getBasePermissions)
                .map(permissions -> permissions.contains(Permission.ADMINISTRATOR))
                .blockOptional()
                .orElse(false);

        return result;
    }

    public static boolean isMemberNitro(Message msg) {
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
}
