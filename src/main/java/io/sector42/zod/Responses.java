package io.sector42.zod;

import com.jsoniter.output.JsonStream;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

final class Responses {
    private Responses() {}

    static void text(HttpServerExchange exchange, String text) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send(text);
    }

    static void json(HttpServerExchange exchange, Object object) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(JsonStream.serialize(object));
    }
}
