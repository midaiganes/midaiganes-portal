package ee.midaiganes.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.model.PageLayout;
import ee.midaiganes.model.PageLayoutName;
import ee.midaiganes.util.PropsValues;
import ee.midaiganes.util.StringPool;

public class PageLayoutRepository {
	private static final Logger log = LoggerFactory.getLogger(PageLayoutRepository.class);
	private final ConcurrentHashMap<PageLayoutName, PageLayout> pageLayouts = new ConcurrentHashMap<>();

	public PageLayoutRepository() {
		PageLayoutName name = new PageLayoutName(PropsValues.PORTAL_CONTEXT.replaceAll("^/", StringPool.EMPTY), "1-column");
		pageLayouts.put(name, new PageLayout(name, "/layouts/"));
	}

	public List<PageLayout> getPageLayouts() {
		return Collections.unmodifiableList(new ArrayList<>(pageLayouts.values()));
	}

	public PageLayout getPageLayout(String pageLayoutId) {
		return getPageLayout(new PageLayoutName(pageLayoutId));
	}

	public PageLayout getPageLayout(PageLayoutName pageLayoutName) {
		PageLayout pl = pageLayouts.get(pageLayoutName);
		if (pl != null) {
			return pl;
		}
		if (log.isWarnEnabled()) {
			log.warn("no pageLayout with name '" + pageLayoutName + "'; All pageLayouts = " + getPageLayouts());
		}
		return null;
	}

	public PageLayout getDefaultPageLayout() {
		Enumeration<PageLayout> e = this.pageLayouts.elements();
		return e.hasMoreElements() ? e.nextElement() : null;
	}
}
