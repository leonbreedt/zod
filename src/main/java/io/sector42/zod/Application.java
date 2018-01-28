package io.sector42.zod;

import io.sector42.zod.model.Person;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;

public class Application {
    // key obtained by running ./bin/tokens.sh
    private static final String ISSUER_PUBLIC_KEY =
        "-----BEGIN PUBLIC KEY-----\n" +
        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC/kjqmJDtBxIwYrMz8Q20aDSfp\n" +
        "1Y8SuRgUJL1jCobBfEnF56kO+mb+3xnUXaEoibJEsmXTpGtGjAJHKKyzQFaXp/JY\n" +
        "b9VFe/nX/z1l5Lq1O2gXNadqIXgqferH+8oWc9iUk4e9qLuRKVk/IRYvhP42YJm0\n" +
        "AVWU1oFfWBHJhcCgPQIDAQAB\n" +
        "-----END PUBLIC KEY-----";

    private static final Person person = new Person("Leon", "Breedt");

    public static void main(final String[] args) {
        HttpHandler routes = new RoutingHandler()
            .get("/hello", exchange -> Responses.json(exchange, person))
            .get("/string", exchange -> Responses.text(exchange, "Hello, I'm a static string"))
            .setFallbackHandler(exchange -> Responses.text(exchange, "Not Found!"));

        // Wrap root handler with Logging.access() to get access logging.

        JWTAuthorizationCallback authCheck = (exchange, account) -> {
            // This would be a scope check in a real app.
            // return account.hasRole("xxx");
            return true;
        };

        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(Security.jwt(ISSUER_PUBLIC_KEY, routes, authCheck))
                .build();

        server.start();
    }
}