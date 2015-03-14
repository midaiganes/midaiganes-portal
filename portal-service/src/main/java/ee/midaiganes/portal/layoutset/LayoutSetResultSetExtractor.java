package ee.midaiganes.portal.layoutset;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class LayoutSetResultSetExtractor implements ResultSetExtractor<LayoutSet> {
    private static final LayoutSetRowMapper layoutSetRowMapper = new LayoutSetRowMapper();

    @Override
    public LayoutSet extractData(ResultSet rs) throws SQLException, DataAccessException {
        if (rs.next()) {
            LayoutSet ls = layoutSetRowMapper.mapRow(rs, 1);
            if (rs.next()) {
                throw new IllegalStateException("Found more than one row!");
            }
            return ls;
        }
        return null;
    }

}
