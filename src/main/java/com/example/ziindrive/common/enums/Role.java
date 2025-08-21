package com.example.ziindrive.common.enums;

import java.util.Set;

public enum Role {

    VIEWER(Set.of("READ", "DOWNLOAD", "FAVORITE")),

    ADMIN(Set.of("READ", "DOWNLOAD", "FAVORITE",
            "UPLOAD", "RENAME", "DELETE", "RESTORE")),

    MANAGER(Set.of("READ", "DOWNLOAD", "FAVORITE",
            "UPLOAD", "RENAME", "DELETE", "RESTORE",
            "SHRED", "SHRED_ALL", "DELETE_FOLDER", "GRANT_ROLE"));

    private final Set<String> permissions;

    Role(Set<String> permissions) {
        this.permissions = permissions;
    }

    public Set<String> getPermissions() {
        return this.permissions;
    }
}
