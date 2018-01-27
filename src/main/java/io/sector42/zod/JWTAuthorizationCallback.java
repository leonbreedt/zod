package io.sector42.zod;

import io.undertow.server.HttpServerExchange;

interface JWTAuthorizationCallback {
    boolean isPermitted(HttpServerExchange exchange, JWTAccount account);
}
