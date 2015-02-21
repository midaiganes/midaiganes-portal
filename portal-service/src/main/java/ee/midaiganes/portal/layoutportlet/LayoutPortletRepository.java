package ee.midaiganes.portal.layoutportlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.springframework.transaction.annotation.Transactional;

import ee.midaiganes.cache.SingleVmCache;
import ee.midaiganes.cache.SingleVmPool;
import ee.midaiganes.portal.portletinstance.PortletInstanceRepository;
import ee.midaiganes.portlet.PortletName;

public class LayoutPortletRepository {
    private final PortletInstanceRepository portletInstanceRepository;
    private final LayoutPortletDao layoutPortletDao;
    private final SingleVmCache cache;

    @Inject
    public LayoutPortletRepository(LayoutPortletDao layoutPortletDao, PortletInstanceRepository portletInstanceRepository, SingleVmPool singleVmPool) {
        this.layoutPortletDao = layoutPortletDao;
        this.portletInstanceRepository = portletInstanceRepository;
        this.cache = singleVmPool.getCache(LayoutPortletRepository.class.getName());
    }

    @Transactional
    public void addLayoutPortlet(long layoutId, long rowId, PortletName portletName, int boxIndex) {
        try {
            long portletInstanceId = portletInstanceRepository.addPortletInstance(portletName);
            layoutPortletDao.addLayoutPortlet(layoutId, rowId, portletInstanceId, boxIndex);
        } finally {
            cache.clear();
        }
    }

    public void deleteLayoutPortlet(String windowID) {
        try {
            portletInstanceRepository.deletePortletInstance(windowID);
        } finally {
            cache.clear();
        }
    }

    public List<LayoutPortlet> getLayoutPortlets(long layoutId, long rowId) {
        List<LayoutPortlet> portlets = new ArrayList<>();
        for (LayoutPortlet layoutPortlet : getLayoutPortlets(layoutId)) {
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
        for (LayoutPortlet layoutPortlet : getLayoutPortlets(layoutId)) {
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
            try {
                layoutPortletDao.moveLayoutPortlet(layoutPortlet.getId(), portletBoxId, boxIndex);
            } finally {
                cache.clear();
            }
        } else {
            throw new IllegalArgumentException("Layout portlet not found: layoutId=" + layoutId + ", portletWindowId='" + portletWindowID + "'.");
        }
    }

    private List<LayoutPortlet> getLayoutPortlets(long layoutId) {
        String cacheKey = Long.toString(layoutId);
        List<LayoutPortlet> layoutPortlets = cache.get(cacheKey);
        if (layoutPortlets == null) {
            try {
                layoutPortlets = new ArrayList<>(layoutPortletDao.loadLayoutPortlets(layoutId));
                Collections.sort(layoutPortlets, new Comparator<LayoutPortlet>() {
                    @Override
                    public int compare(LayoutPortlet l1, LayoutPortlet l2) {
                        int i = Long.compare(l1.getRowId(), l2.getRowId());
                        return i == 0 ? Long.compare(l1.getBoxIndex(), l2.getBoxIndex()) : i;
                    }
                });
            } finally {
                cache.put(cacheKey, layoutPortlets);
            }
        }
        return layoutPortlets;
    }
}
