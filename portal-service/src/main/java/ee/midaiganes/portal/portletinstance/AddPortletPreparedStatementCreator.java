package ee.midaiganes.portal.portletinstance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SqlProvider;

import ee.midaiganes.model.PortletName;
import ee.midaiganes.util.StringPool;

public class AddPortletPreparedStatementCreator implements PreparedStatementCreator, SqlProvider {
	private static final String ADD_PORTLET_INSTANCE = "INSERT INTO PortletInstance(portletContext, portletName, windowID) VALUES(?, ?, ?)";
	private static final String[] ID_ARRAY = new String[] { StringPool.ID };
	private final PortletName portletName;
	private final String windowID;

	public AddPortletPreparedStatementCreator(PortletName portletName, String windowID) {
		this.portletName = portletName;
		this.windowID = windowID;
	}

	@Override
	public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
		PreparedStatement ps = con.prepareStatement(ADD_PORTLET_INSTANCE, ID_ARRAY);
		ps.setString(1, portletName.getContext());
		ps.setString(2, portletName.getName());
		ps.setString(3, windowID);
		return ps;
	}

	@Override
	public String getSql() {
		return ADD_PORTLET_INSTANCE;
	}

}
