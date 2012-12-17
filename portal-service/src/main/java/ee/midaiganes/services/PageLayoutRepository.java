package ee.midaiganes.services;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.model.PageLayout;
import ee.midaiganes.model.PageLayoutName;
import ee.midaiganes.util.PortalUtil;

public class PageLayoutRepository {
	private static final Logger log = LoggerFactory.getLogger(PageLayoutRepository.class);

	public List<PageLayout> getPageLayouts() {
		PageLayout pageLayout = new PageLayout(new PageLayoutName(PortalUtil.getPortalContextPath().replace("/", ""), "1-column"), "/layouts/");
		return Arrays.asList(pageLayout);
	}

	public PageLayout getPageLayout(String pageLayoutId) {
		return getPageLayout(new PageLayoutName(pageLayoutId));
	}

	public PageLayout getPageLayout(PageLayoutName pageLayoutName) {
		for (PageLayout pl : getPageLayouts()) {
			if (pl.getPageLayoutName().equals(pageLayoutName)) {
				return pl;
			}
		}
		if (log.isWarnEnabled()) {
			log.warn("no pageLayout with name '" + pageLayoutName + "'; All pageLayouts = " + getPageLayouts());
		}
		return null;
	}

	public PageLayout getDefaultPageLayout() {
		return getPageLayouts().get(0);
	}
}
