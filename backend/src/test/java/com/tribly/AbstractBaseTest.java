package com.tribly;

import com.tribly.util.MinioTestResource;
import com.tribly.util.TileserverTestResource;
import io.quarkus.test.common.WithTestResource;

@WithTestResource(MinioTestResource.class)
@WithTestResource(TileserverTestResource.class)
public abstract class AbstractBaseTest {}
