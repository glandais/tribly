package fr.pedalons;

import fr.pedalons.util.MinioTestResource;
import fr.pedalons.util.TileserverTestResource;
import io.quarkus.test.common.TestResourceScope;
import io.quarkus.test.common.WithTestResource;

// GLOBAL scope: with the default MATCHING_RESOURCES, Quarkus restarts (app + containers,
// ~15s) between classes with different resource sets — Surefire's dynamic fork dispatch
// interleaves them constantly. GLOBAL starts the resources once per JVM, no restarts.
@WithTestResource(value = MinioTestResource.class, scope = TestResourceScope.GLOBAL)
@WithTestResource(value = TileserverTestResource.class, scope = TestResourceScope.GLOBAL)
public abstract class AbstractBaseTest {}
