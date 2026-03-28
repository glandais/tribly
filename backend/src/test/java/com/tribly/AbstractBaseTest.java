package com.tribly;

import com.tribly.util.MinioTestResource;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusTest;

@WithTestResource(MinioTestResource.class)
public abstract class AbstractBaseTest {
}
