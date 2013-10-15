package ee.midaiganes.services.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.ResultSetExtractor;

public final class LongResultSetExtractor implements ResultSetExtractor<Long> {
	@Override
	public final Long extractData(final ResultSet rs) throws SQLException {
		if (rs.next()) {
			final long value = rs.getLong(1);
			return rs.wasNull() ? null : Long.valueOf(value);
		}
		return null;
	}
}