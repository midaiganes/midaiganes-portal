package ee.midaiganes.model;

import javax.annotation.Nonnull;

public interface PortalResource {
    long getId();

    @Nonnull
    String getResource();
}
