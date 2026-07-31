package fr.pedalons.service.security.annotation;

import jakarta.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the endpoints that accept a {@code ?t=} tile token.
 *
 * <p>A JAX-RS name binding rather than an interceptor binding, because it gates a {@code
 * ContainerRequestFilter}: the token has to be verified before the resource method runs. Its whole
 * point is to be narrow — only the methods carrying it can turn a tile token into an identified
 * caller, which is what stops the token from working as general-purpose authentication.
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface TileTokenAuth {}
