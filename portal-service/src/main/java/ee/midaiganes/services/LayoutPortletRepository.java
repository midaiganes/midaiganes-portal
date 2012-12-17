package ee.midaiganes.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import ee.midaiganes.model.LayoutPortlet;
import ee.midaiganes.model.PortletInstance;
import ee.midaiganes.model.PortletName;
import ee.midaiganes.services.SingleVmPool.Cache;

public class LayoutPortletRepository {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Resource
	private PortletInstanceRepository portletInstanceRepository;

	private static final String GET_LAYOUT_PORTLETS = "SELECT LayoutPortlet.id, LayoutPortlet.layoutId, LayoutPortlet.rowId, LayoutPortlet.portletInstanceId, PortletInstance.id, PortletInstance.portletContext, PortletInstance.portletName, PortletInstance.windowID FROM LayoutPortlet JOIN PortletInstance ON (LayoutPortlet.portletInstanceId = PortletInstance.id) WHERE layoutId = ?";
	private static final String ADD_LAYOUT_PORTLET = "INSERT INTO LayoutPortlet (layoutId, rowId, portletInstanceId) VALUES(?, ?, ?)";

	private final Cache cache;

	public LayoutPortletRepository() {
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

	private static final RowMapper<LayoutPortlet> layoutPortletRowMapper = new RowMapper<LayoutPortlet>() {

		@Override
		public LayoutPortlet mapRow(ResultSet rs, int rowNum) throws SQLException {
			LayoutPortlet layoutPortlet = new LayoutPortlet();
			layoutPortlet.setId(rs.getLong(1));
			layoutPortlet.setLayoutId(rs.getLong(2));
			layoutPortlet.setRowId(rs.getLong(3));
			layoutPortlet.setPortletInstanceId(rs.getLong(4));
			PortletInstance portletInstance = new PortletInstance();
			portletInstance.setId(rs.getLong(5));
			portletInstance.setPortletName(new PortletName(rs.getString(6), rs.getString(7)));
			portletInstance.setWindowID(rs.getString(8));
			layoutPortlet.setPortletInstance(portletInstance);
			return layoutPortlet;
		}
	};

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
			if (layoutPortlet.getPortletInstance().getWindowID().equals(portletWindowID)) {
				return layoutPortlet;
			}
		}
		return null;
	}

	private List<LayoutPortlet> getLayoutPortlets(long layoutId) {
		String cacheKey = Long.toString(layoutId);
		List<LayoutPortlet> layoutPortlets = cache.get(cacheKey);
		if (layoutPortlets == null) {
			layoutPortlets = jdbcTemplate.query(GET_LAYOUT_PORTLETS, layoutPortletRowMapper, layoutId);
			cache.put(cacheKey, layoutPortlets);
		}
		return layoutPortlets;
	}
}
