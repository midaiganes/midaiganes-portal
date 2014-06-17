package ee.midaiganes.portal.portletinstance;

import java.io.Serializable;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

import ee.midaiganes.model.PortalResource;

public class PortletInstance implements Serializable, PortalResource {
    private static final long serialVersionUID = 1L;

    private final long id;
    private final PortletNamespace portletNamespace;

    public PortletInstance(long id, PortletNamespace portletNamespace) {
        this.id = id;
        this.portletNamespace = portletNamespace;
    }

    @Override
    public long getId() {
        return id;
    }

    public PortletNamespace getPortletNamespace() {
        return portletNamespace;
    }

    @Override
    @Nonnull
    public String getResource() {
        return getResourceName();
    }

    @Nonnull
    public static String getResourceName() {
        String resourceName = PortletInstance.class.getName();
        return Preconditions.checkNotNull(resourceName);
    }

    @Override
    public String toString() {
        return "PortletInstance [id=" + id + ", portletNamespace=" + portletNamespace + "]";
    }
}
