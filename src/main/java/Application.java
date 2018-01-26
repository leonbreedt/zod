import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import com.jsoniter.output.JsonStream;

public class Application {
    private static class Person {
        String firstName;
        String lastName;
        Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    public static void main(final String[] args) {
        final Person person = new Person("Leon", "Breedt");
        final HttpHandler routeHandler = new RoutingHandler()
            .get("/hello", exchange -> { sendJSON(exchange, person); })
            .get("/string", exchange -> { sendText(exchange, "Hello, I'm a static string"); })
            .setFallbackHandler(exchange -> { sendText(exchange, "Not Found!"); });

        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(routeHandler)
                .build();

        server.start();
    }

    private static void sendText(HttpServerExchange exchange, String text) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send(text);
    }

    private static void sendJSON(HttpServerExchange exchange, Object obj) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(JsonStream.serialize(obj));
    }
}