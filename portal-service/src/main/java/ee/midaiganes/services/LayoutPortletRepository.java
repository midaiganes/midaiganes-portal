package ee.midaiganes.services;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.model.LayoutPortlet;
import ee.midaiganes.model.PortletName;
import ee.midaiganes.services.SingleVmPool.Cache;
import ee.midaiganes.services.rowmapper.LayoutPortletRowMapper;

@Component(value = PortalConfig.LAYOUT_PORTLET_REPOSITORY)
public class LayoutPortletRepository {

	private final JdbcTemplate jdbcTemplate;
	private final PortletInstanceRepository portletInstanceRepository;

	private static final String GET_LAYOUT_PORTLETS = "SELECT LayoutPortlet.id, LayoutPortlet.layoutId, LayoutPortlet.rowId, LayoutPortlet.portletInstanceId, PortletInstance.id, PortletInstance.portletContext, PortletInstance.portletName, PortletInstance.windowID FROM LayoutPortlet JOIN PortletInstance ON (LayoutPortlet.portletInstanceId = PortletInstance.id) WHERE layoutId = ?";
	private static final String ADD_LAYOUT_PORTLET = "INSERT INTO LayoutPortlet (layoutId, rowId, portletInstanceId) VALUES(?, ?, ?)";
	private static final LayoutPortletRowMapper layoutPortletRowMapper = new LayoutPortletRowMapper();

	private final Cache cache;

	public LayoutPortletRepository(JdbcTemplate jdbcTemplate, PortletInstanceRepository portletInstanceRepository) {
		this.jdbcTemplate = jdbcTemplate;
		this.portletInstanceRepository = portletInstanceRepository;
		cache = SingleVmPool.getCache(LayoutPortletRepository.class.getName());
	}

	public void addLayoutPortlet(long layoutId, long rowId, PortletName portletName) {
		long portletInstanceId = portletInstanceRepository.addPortletInstance(portletName);
		jdbcTemplate.update(ADD_LAYOUT_PORTLET, layoutId, rowId, portletInstanceId);
		cache.clear();
	}

	public void deleteLayoutPortlet(String windowID) {
		portletInstanceRepository.deletePortletInstance(windowID);
		cache.clear();
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
			layoutPortlets = queryLayoutPortlets(layoutId);
			cache.put(cacheKey, layoutPortlets);
		}
		return layoutPortlets;
	}

	private List<LayoutPortlet> queryLayoutPortlets(long layoutId) {
		return jdbcTemplate.query(GET_LAYOUT_PORTLETS, layoutPortletRowMapper, layoutId);
	}
}
