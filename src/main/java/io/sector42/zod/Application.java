package io.sector42.zod;

import io.sector42.zod.model.Person;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;

public class Application {
    private static final String ISSUER_PUBLIC_KEY =
        "-----BEGIN PUBLIC KEY-----\n" +
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA33TqqLR3eeUmDtHS89qF\n" +
        "3p4MP7Wfqt2Zjj3lZjLjjCGDvwr9cJNlNDiuKboODgUiT4ZdPWbOiMAfDcDzlOxA\n" +
        "04DDnEFGAf+kDQiNSe2ZtqC7bnIc8+KSG/qOGQIVaay4Ucr6ovDkykO5Hxn7OU7s\n" +
        "Jp9TP9H0JH8zMQA6YzijYH9LsupTerrY3U6zyihVEDXXOv08vBHk50BMFJbE9iwF\n" +
        "wnxCsU5+UZUZYw87Uu0n4LPFS9BT8tUIvAfnRXIEWCha3KbFWmdZQZlyrFw0buUE\n" +
        "f0YN3/Q0auBkdbDR/ES2PbgKTJdkjc/rEeM0TxvOUf7HuUNOhrtAVEN1D5uuxE1W\n" +
        "SwIDAQAB\n" +
        "-----END PUBLIC KEY-----\n";

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