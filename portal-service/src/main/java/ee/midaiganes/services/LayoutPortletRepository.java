package ee.midaiganes.services;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.model.LayoutPortlet;
import ee.midaiganes.model.PortletName;
import ee.midaiganes.services.SingleVmPool.Cache;
import ee.midaiganes.services.dao.LayoutPortletDao;

@Resource(name = PortalConfig.LAYOUT_PORTLET_REPOSITORY)
public class LayoutPortletRepository {
	private final PortletInstanceRepository portletInstanceRepository;
	private final LayoutPortletDao layoutPortletDao;
	private final Cache cache;

	public LayoutPortletRepository(LayoutPortletDao layoutPortletDao, PortletInstanceRepository portletInstanceRepository) {
		this.layoutPortletDao = layoutPortletDao;
		this.portletInstanceRepository = portletInstanceRepository;
		this.cache = SingleVmPool.getCache(LayoutPortletRepository.class.getName());
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, value = PortalConfig.TXMANAGER)
	public void addLayoutPortlet(long layoutId, long rowId, PortletName portletName) {
		try {
			long portletInstanceId = portletInstanceRepository.addPortletInstance(portletName);
			layoutPortletDao.addLayoutPortlet(layoutId, rowId, portletInstanceId);
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

	public LayoutPortlet getLayoutPortlet(long layoutId, long rowId) {
		for (LayoutPortlet layoutPortlet : getLayoutPortlets(layoutId)) {
			if (layoutPortlet.getRowId() == rowId) {
				return layoutPortlet;
			}
		}
		return null;
	}

	public LayoutPortlet getLayoutPortlet(long layoutId, String portletWindowID) {
		for (LayoutPortlet layoutPortlet : getLayoutPortlets(layoutId)) {
			if (layoutPortlet.getPortletInstance().getPortletNamespace().getWindowID().equals(portletWindowID)) {
				return layoutPortlet;
			}
		}
		return null;
	}

	private List<LayoutPortlet> getLayoutPortlets(long layoutId) {
		String cacheKey = Long.toString(layoutId);
		List<LayoutPortlet> layoutPortlets = cache.get(cacheKey);
		if (layoutPortlets == null) {
			layoutPortlets = layoutPortletDao.loadLayoutPortlets(layoutId);
			cache.put(cacheKey, layoutPortlets);
		}
		return layoutPortlets;
	}
}
