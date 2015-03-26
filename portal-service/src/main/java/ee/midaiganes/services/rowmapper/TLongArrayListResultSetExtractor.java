package ee.midaiganes.services.rowmapper;

import gnu.trove.list.array.TLongArrayList;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.ResultSetExtractor;

public class TLongArrayListResultSetExtractor implements ResultSetExtractor<TLongArrayList> {
    @Override
    public TLongArrayList extractData(ResultSet rs) throws SQLException {
        TLongArrayList list = new TLongArrayList();
        while (rs.next()) {
            list.add(rs.getLong(1));
        }
        return list;
    }
}
