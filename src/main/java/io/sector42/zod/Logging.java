package io.sector42.zod;

import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.accesslog.AccessLogHandler;
import io.undertow.server.handlers.accesslog.AccessLogReceiver;

final class Logging {
    private Logging() {}

    static HttpHandler access(HttpHandler nextHandler) {
        return new AccessLogHandler(
            nextHandler,
            new AccessLogger(),
            "combined",
            (ClassLoader)null);
    }

    private static class AccessLogger implements AccessLogReceiver {
        @Override
        public void logMessage(String message) {
            System.out.println(message);
        }
    }
}
