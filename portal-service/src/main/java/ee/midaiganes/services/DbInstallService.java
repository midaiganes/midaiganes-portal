package ee.midaiganes.services;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.services.exceptions.DbInstallFailedException;
import ee.midaiganes.util.IOUtil;

@Resource(name = PortalConfig.DB_INSTALL_SERVICE)
public class DbInstallService {

	private static final Logger log = LoggerFactory.getLogger(DbInstallService.class);

	private final JdbcTemplate jdbcTemplate;

	public DbInstallService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private static class BooleanTrueRowMapper implements RowMapper<Boolean> {
		@Override
		public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
			return Boolean.TRUE;
		}
	}

	public void install(ServletContext sc) throws DbInstallFailedException {
		try {
			if (jdbcTemplate.query("show tables like 'LayoutSet'", new BooleanTrueRowMapper()).isEmpty()) {
				doInstall(sc.getResourceAsStream("/META-INF/sql/mysql-install.sql"));
			}
		} catch (Exception e) {
			throw new DbInstallFailedException(e);
		}
	}

	private void doInstall(InputStream is) {
		try {
			String sql = IOUtil.toString(is, "UTF-8");
			for (String str : sql.split(";")) {
				if (str.trim().length() > 0) {
					log.info(str);
					jdbcTemplate.execute(str);
				}
			}

		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
}
