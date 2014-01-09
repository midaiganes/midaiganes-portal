package ee.midaiganes.services.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.ResultSetExtractor;

public class PortletPreferencesResultSetExtractor implements ResultSetExtractor<Map<String, String[]>> {
    @Override
    public Map<String, String[]> extractData(ResultSet rs) throws SQLException {
        Map<String, String[]> map = new HashMap<>();
        while (rs.next()) {
            String name = rs.getString(1);
            String value = rs.getString(2);
            String[] values = map.get(name);
            if (values == null) {
                values = new String[] { value };
            } else {
                String[] nv = new String[values.length + 1];
                System.arraycopy(values, 0, nv, 0, values.length);
                nv[nv.length - 1] = value;
                values = nv;
            }
            map.put(name, values);
        }
        return map;
    }

}