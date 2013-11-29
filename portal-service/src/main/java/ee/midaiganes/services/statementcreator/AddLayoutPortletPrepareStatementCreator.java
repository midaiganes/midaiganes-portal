package ee.midaiganes.services.statementcreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SqlProvider;

import ee.midaiganes.util.StringPool;

public class AddLayoutPortletPrepareStatementCreator implements PreparedStatementCreator, SqlProvider {
    private static final String ADD_LAYOUT_PORTLET = "INSERT INTO LayoutPortlet (layoutId, rowId, portletInstanceId, boxIndex) VALUES(?, ?, ?, (SELECT IFNULL((SELECT maxBoxIndex + 1 FROM (SELECT MAX(boxIndex) AS maxBoxIndex FROM LayoutPortlet WHERE layoutId = ? AND rowId = ?) as t), 1)))";
    private static final String[] ID_ARRAY = { StringPool.ID };
    private final long layoutId;
    private final long rowId;
    private final long portletInstanceId;

    public AddLayoutPortletPrepareStatementCreator(long layoutId, long rowId, long portletInstanceId) {
        this.layoutId = layoutId;
        this.rowId = rowId;
        this.portletInstanceId = portletInstanceId;
    }

    @Override
    public String getSql() {
        return ADD_LAYOUT_PORTLET;
    }

    @Override
    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement(ADD_LAYOUT_PORTLET, ID_ARRAY);
        ps.setLong(1, layoutId);
        ps.setLong(2, rowId);
        ps.setLong(3, portletInstanceId);
        ps.setLong(4, layoutId);
        ps.setLong(5, rowId);
        return ps;
    }

}
