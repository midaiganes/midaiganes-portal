package ee.midaiganes.secureservices;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import ee.midaiganes.portal.layout.Layout;
import ee.midaiganes.portal.layout.LayoutRepository;
import ee.midaiganes.portal.permission.PermissionRepository;
import ee.midaiganes.services.exceptions.PrincipalException;
import ee.midaiganes.services.exceptions.ResourceActionNotFoundException;
import ee.midaiganes.services.exceptions.ResourceNotFoundException;

public class SecureLayoutRepository {
    private static final Logger log = LoggerFactory.getLogger(SecureLayoutRepository.class);
    private static final String VIEW = "VIEW";
    private final LayoutRepository layoutRepository;
    private final PermissionRepository permissionRepository;

    @Inject
    public SecureLayoutRepository(LayoutRepository layoutRepository, PermissionRepository permissionRepository) {
        this.layoutRepository = layoutRepository;
        this.permissionRepository = permissionRepository;
    }

    public ImmutableList<Layout> getLayouts(long userId, long layoutSetId) {
        ImmutableList<Layout> layouts = layoutRepository.getLayouts(layoutSetId);
        ImmutableList.Builder<Layout> allowedLayouts = ImmutableList.builder();
        try {
            for (Layout layout : layouts) {
                addToAllowedLayoutsIfUserHasViewPermission(userId, layout, allowedLayouts);
            }
        } catch (ResourceNotFoundException | ResourceActionNotFoundException e) {
            throw new RuntimeException(e);
        }

        return allowedLayouts.build();
    }

    private void addToAllowedLayoutsIfUserHasViewPermission(long userId, Layout layout, ImmutableList.Builder<Layout> allowedLayouts) throws ResourceNotFoundException,
            ResourceActionNotFoundException {
        if (hasUserViewPermission(userId, layout)) {
            allowedLayouts.add(layout);
        }
    }

    public Layout getLayout(long userId, long layoutSetId, String friendlyUrl) throws PrincipalException {
        Layout layout = layoutRepository.getLayout(layoutSetId, friendlyUrl);
        try {
            if (layout == null) {
                return null;
            }
            if (hasUserViewPermission(userId, layout)) {
                return layout;
            }
        } catch (ResourceNotFoundException | ResourceActionNotFoundException e) {
            throw new RuntimeException(e);
        }
        throw new PrincipalException(userId, layout, VIEW);
    }

    @Nullable
    public Layout getHomeLayout(long userId, long layoutSetId) {
        for (Layout layout : layoutRepository.getChildLayouts(layoutSetId, null)) {
            try {
                if (hasUserViewPermission(userId, layout)) {
                    return layout;
                }
            } catch (ResourceNotFoundException | ResourceActionNotFoundException e) {
                log.debug(e.getMessage(), e);
            }
        }
        return null;
    }

    private boolean hasUserViewPermission(long userId, Layout layout) throws ResourceNotFoundException, ResourceActionNotFoundException {
        return permissionRepository.hasUserPermission(userId, layout, VIEW);
    }
}
