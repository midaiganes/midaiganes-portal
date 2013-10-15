package ee.midaiganes.services.dao;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import ee.midaiganes.model.LayoutPortlet;
import ee.midaiganes.services.rowmapper.LayoutPortletRowMapper;

public class LayoutPortletDao {
	private static final String GET_LAYOUT_PORTLETS = "SELECT LayoutPortlet.id, LayoutPortlet.layoutId, LayoutPortlet.rowId, LayoutPortlet.portletInstanceId, PortletInstance.portletContext, PortletInstance.portletName, PortletInstance.windowID FROM LayoutPortlet JOIN PortletInstance ON (LayoutPortlet.portletInstanceId = PortletInstance.id) WHERE layoutId = ?";
	private static final String ADD_LAYOUT_PORTLET = "INSERT INTO LayoutPortlet (layoutId, rowId, portletInstanceId) VALUES(?, ?, ?)";
	private static final LayoutPortletRowMapper layoutPortletRowMapper = new LayoutPortletRowMapper();

	private final JdbcTemplate jdbcTemplate;

	public LayoutPortletDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<LayoutPortlet> loadLayoutPortlets(long layoutId) {
		return jdbcTemplate.query(GET_LAYOUT_PORTLETS, layoutPortletRowMapper, Long.valueOf(layoutId));
	}

	public void addLayoutPortlet(long layoutId, long rowId, long portletInstanceId) {
		jdbcTemplate.update(ADD_LAYOUT_PORTLET, Long.valueOf(layoutId), Long.valueOf(rowId), Long.valueOf(portletInstanceId));
	}
}
