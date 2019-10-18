package io.j1.nitro4all.bot;

import discord4j.core.object.util.Snowflake;

import java.util.HashSet;
import java.util.Set;

public class State {
    private boolean nitroCheck = true;
    private boolean enabled = true;
    private Set<Snowflake> epics = new HashSet<>();

    public boolean addAllEpics(Set<Snowflake> snowflakeSet) {
        return epics.addAll(snowflakeSet);
    }

    public boolean removeEpic(Snowflake snowflake) {
        return epics.remove(snowflake);
    }

    public boolean isEpic(Snowflake snowflake) {
        return epics.contains(snowflake);
    }

    public Set<Snowflake> getEpics() { return epics; }

    public boolean toggleNitroCheck() {
        nitroCheck = !nitroCheck;
        return nitroCheck;
    }

    public boolean getNitroCheck() { return nitroCheck; }

    public boolean toggleEnabled() {
        enabled = !enabled;
        return enabled;
    }

    public boolean getEnabled() { return enabled; }
}
