package me.yhamarsheh.bridgersumo.enums;

import java.util.UUID;

public enum Debug {

    DUMMY_UUID(UUID.fromString("00000000-0000-0000-0000-000000000000"));

    UUID uuid;
    Debug(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }
}
