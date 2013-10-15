package ee.midaiganes.beans;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class PortalDataSource extends BasicDataSource implements DisposableBean {
	private static final Logger log = LoggerFactory.getLogger(PortalDataSource.class);

	@Override
	public void destroy() throws Exception {
		log.info("close PortalDataSource");
		super.close();
		String driverName = getDriverClassName();
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		if (drivers != null) {
			while (drivers.hasMoreElements()) {
				Driver driver = drivers.nextElement();
				if (driverName.equals(driver.getClass().getName())) {
					DriverManager.deregisterDriver(driver);
				}
			}
		}
	}
}
