package ee.midaiganes.portal.group;

import java.io.Serializable;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

import ee.midaiganes.model.PortalResource;
import ee.midaiganes.util.StringUtil;

public class Group implements Serializable, PortalResource {
    private static final long serialVersionUID = 1L;
    @Nonnull
    private static final String RESOURCE = StringUtil.getName(Group.class);

    private final long id;
    @Nonnull
    private final String name;
    private final boolean userGroup;

    public Group(long id, String name, boolean userGroup) {
        this.id = id;
        this.name = Preconditions.checkNotNull(name);
        this.userGroup = userGroup;
    }

    @Override
    public long getId() {
        return id;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public boolean isUserGroup() {
        return userGroup;
    }

    @Override
    @Nonnull
    public String getResource() {
        return RESOURCE;
    }
}
