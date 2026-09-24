package fr.pedalons.service.security.annotation;

import jakarta.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the machine-to-machine endpoints biketeam calls over HTTPS to trigger and follow a live
 * migration. Bound to {@code BiketeamM2MFilter}, which checks the shared secret — the same name
 * binding pattern as {@link TileTokenAuth}, so the secret authenticates nothing else.
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface BiketeamM2M {}
