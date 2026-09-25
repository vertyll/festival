package com.vertyll.festival.security;

final class Roles {

    static final String USER = "USER";
    static final String ADMIN = "ADMIN";

    static final String USER_AUTHORITY = "ROLE_" + USER;
    static final String ADMIN_AUTHORITY = "ROLE_" + ADMIN;

    private Roles() {
    }
}
