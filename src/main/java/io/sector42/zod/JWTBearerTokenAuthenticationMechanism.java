package io.sector42.zod;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.util.List;
import java.util.Locale;

public class JWTBearerTokenAuthenticationMechanism implements AuthenticationMechanism {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String LOWERCASE_BEARER_PREFIX = BEARER_PREFIX.toLowerCase();
    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    private final JWTVerifier verifier;
    private final String name;
    private final JWTAuthorizationCallback authorizationCallback;

    public JWTBearerTokenAuthenticationMechanism(JWTVerifier verifier, JWTAuthorizationCallback authorizationCallback) {
        assert verifier != null : "Token verifier is required";
        this.verifier = verifier;
        this.name = "JWT";
        this.authorizationCallback = authorizationCallback;
    }

    @Override
    public AuthenticationMechanismOutcome authenticate(HttpServerExchange exchange, SecurityContext context) {
        AuthenticationMechanismOutcome outcome = AuthenticationMechanismOutcome.NOT_ATTEMPTED;

        List<String> values = exchange.getRequestHeaders().get(Headers.AUTHORIZATION);
        if (values == null) {
            outcome = AuthenticationMechanismOutcome.NOT_ATTEMPTED;
        } else {
            for (String value : values) {
                if (!value.toLowerCase(Locale.ENGLISH).startsWith(LOWERCASE_BEARER_PREFIX)) {
                    continue;
                }

                String tokenValue = value.substring(BEARER_PREFIX_LENGTH);
                if (tokenValue.length() == 0) {
                    context.authenticationFailed("JWT token malformed", name);
                    outcome = AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
                    break;
                }

                try {
                    DecodedJWT token = verifier.verify(tokenValue);
                    JWTAccount account = new JWTAccount(token);
                    boolean isAuthorized = authorizationCallback == null || authorizationCallback.isPermitted(exchange, account);
                    if (isAuthorized) {
                        context.authenticationComplete(account, name, false);
                        outcome = AuthenticationMechanismOutcome.AUTHENTICATED;
                    } else {
                        outcome = AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
                    }
                    break;
                } catch (AlgorithmMismatchException ex) {
                    context.authenticationFailed("JWT algorithm mismatch", name);
                    outcome = AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
                    break;
                } catch (TokenExpiredException ex) {
                    context.authenticationFailed("JWT token expired", name);
                    outcome = AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
                    break;
                } catch (JWTVerificationException ex) {
                    context.authenticationFailed("JWT token verification failed", name);
                    outcome = AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
                    break;
                }
            }
        }

        return outcome;
    }

    @Override
    public ChallengeResult sendChallenge(HttpServerExchange exchange, SecurityContext context) {
        return ChallengeResult.NOT_SENT;
    }

}
