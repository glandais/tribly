package fr.pedalons;

import fr.pedalons.util.MinioTestResource;
import fr.pedalons.util.TileserverTestResource;
import io.quarkus.test.common.WithTestResource;

@WithTestResource(MinioTestResource.class)
@WithTestResource(TileserverTestResource.class)
public abstract class AbstractBaseTest {}
