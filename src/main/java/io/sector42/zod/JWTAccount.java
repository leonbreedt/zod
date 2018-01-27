package io.sector42.zod;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.undertow.security.idm.Account;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class JWTAccount implements Account {
    private DecodedJWT token;
    private Principal principal;
    private Set<String> scopes;

    JWTAccount(DecodedJWT token) {
        assert token != null : "Token can't be null";
        this.token = token;
    }

    @Override
    public Principal getPrincipal() {
        if (principal == null) {
            String fullName = getClaimOrNull(String.class,"name");
            String givenName = fullName == null ? getClaimOrNull(String.class, "given_name") : null;
            String familyName = fullName == null ? getClaimOrNull(String.class, "family_name") : null;
            String name = fullName != null
                ? fullName
                : (givenName != null && familyName != null ? String.format("%s, %s", familyName.toUpperCase(), givenName) : null);
            principal = () -> name;
        }
        return principal;
    }

    @Override
    public Set<String> getRoles() {
        if (scopes == null) {
            List<String> scopeList = token.getClaim("scopes").asList(String.class);
            if (scopeList != null) {
                scopes = new HashSet<>(scopeList);
            } else {
                scopes = new HashSet<>();
            }
        }
        return scopes;
    }

    private <T> T getClaimOrNull(Class<T> clazz, String name) {
        Claim claim = token.getClaim(name);
        if (claim == null) {
            return null;
        }
        return claim.as(clazz);
    }

}
