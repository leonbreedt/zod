package io.sector42.zod;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.handlers.AuthenticationCallHandler;
import io.undertow.security.handlers.AuthenticationConstraintHandler;
import io.undertow.security.handlers.AuthenticationMechanismsHandler;
import io.undertow.security.handlers.SecurityInitialHandler;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collections;

final class Security {
    private Security() {}

    static HttpHandler jwt(String issuerPublicKey, HttpHandler nextHandler, JWTAuthorizationCallback authorizationCallback) {
        JWTBearerTokenAuthenticationMechanism jwtMechanism = new JWTBearerTokenAuthenticationMechanism(
            buildVerifier(issuerPublicKey),
            authorizationCallback
        );

        HttpHandler handler = nextHandler;

        handler = new AuthenticationCallHandler(handler);
        handler = new AuthenticationConstraintHandler(handler);
        handler = new AuthenticationMechanismsHandler(handler, Collections.singletonList(jwtMechanism));
        handler = new SecurityInitialHandler(AuthenticationMode.PRO_ACTIVE, null, handler);

        return handler;
    }

    private static JWTVerifier buildVerifier(String publicKeyValue) {
        String trimmedKeyValue = publicKeyValue.replaceAll("\\n", "")
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("-----BEGIN RSA PUBLIC KEY-----", "")
            .replace("-----END RSA PUBLIC KEY-----", "");

        RSAPublicKey publicKey;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(trimmedKeyValue));
            publicKey = (RSAPublicKey)keyFactory.generatePublic(keySpecX509);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalArgumentException("invalid public key");
        }

        return JWT.require(Algorithm.RSA256(publicKey, null)).build();
    }
}
