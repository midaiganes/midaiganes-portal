package ee.midaiganes.portal.layoutportlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.springframework.transaction.annotation.Transactional;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;

import ee.midaiganes.portal.portletinstance.PortletInstanceRepository;
import ee.midaiganes.portlet.PortletName;

public class LayoutPortletRepository {
    private final PortletInstanceRepository portletInstanceRepository;
    private final LayoutPortletDao layoutPortletDao;
    private final LoadingCache<Long, ImmutableList<LayoutPortlet>> cache;

    @Inject
    public LayoutPortletRepository(LayoutPortletDao layoutPortletDao, PortletInstanceRepository portletInstanceRepository) {
        this.layoutPortletDao = layoutPortletDao;
        this.portletInstanceRepository = portletInstanceRepository;
        this.cache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(new CacheLoader<Long, ImmutableList<LayoutPortlet>>() {
            @Override
            public ImmutableList<LayoutPortlet> load(Long layoutId) throws Exception {
                List<LayoutPortlet> layoutPortlets = layoutPortletDao.loadLayoutPortlets(layoutId.longValue());
                Collections.sort(layoutPortlets, new Comparator<LayoutPortlet>() {
                    @Override
                    public int compare(LayoutPortlet l1, LayoutPortlet l2) {
                        int i = Long.compare(l1.getRowId(), l2.getRowId());
                        return i == 0 ? Long.compare(l1.getBoxIndex(), l2.getBoxIndex()) : i;
                    }
                });
                return ImmutableList.copyOf(layoutPortlets);
            }
        });
    }

    @Transactional
    public void addLayoutPortlet(long layoutId, long rowId, PortletName portletName, int boxIndex) {
        long portletInstanceId = portletInstanceRepository.addPortletInstance(portletName);
        layoutPortletDao.addLayoutPortlet(layoutId, rowId, portletInstanceId, boxIndex);
        cache.invalidate(Long.valueOf(layoutId));
    }

    public void deleteLayoutPortlet(String windowID) {
        portletInstanceRepository.deletePortletInstance(windowID);
        cache.invalidateAll();
    }

    public List<LayoutPortlet> getLayoutPortlets(long layoutId, long rowId) {
        List<LayoutPortlet> portlets = new ArrayList<>();
        for (LayoutPortlet layoutPortlet : cache.getUnchecked(Long.valueOf(layoutId))) {
            if (layoutPortlet.getRowId() == rowId) {
                portlets.add(layoutPortlet);
            }
        }
        Collections.sort(portlets, new Comparator<LayoutPortlet>() {
            @Override
            public int compare(LayoutPortlet l1, LayoutPortlet l2) {
                return Long.compare(l1.getBoxIndex(), l2.getBoxIndex());
            }
        });
        return portlets;
    }

    // TODO layout/portletWindowId is not unique
    @Nullable
    public LayoutPortlet getLayoutPortlet(long layoutId, String portletWindowID) {
        LayoutPortlet lp = null;
        for (LayoutPortlet layoutPortlet : cache.getUnchecked(Long.valueOf(layoutId))) {
            if (layoutPortlet.getPortletInstance().getPortletNamespace().getWindowID().equals(portletWindowID)) {
                if (lp != null) {
                    throw new IllegalStateException("Found multiple LayoutPortlets in layout(" + layoutId + ") with windowId(" + portletWindowID + "): " + lp + " and "
                            + layoutPortlet);
                }
                lp = layoutPortlet;
            }
        }
        return lp;
    }

    @Transactional
    public void moveLayoutPortlet(String portletWindowID, long layoutId, long portletBoxId, long boxIndex) {
        LayoutPortlet layoutPortlet = getLayoutPortlet(layoutId, portletWindowID);
        if (layoutPortlet != null) {
            layoutPortletDao.moveLayoutPortlet(layoutPortlet.getId(), portletBoxId, boxIndex);
            cache.invalidate(Long.valueOf(layoutId));
        } else {
            throw new IllegalArgumentException("Layout portlet not found: layoutId=" + layoutId + ", portletWindowId='" + portletWindowID + "'.");
        }
    }
}
