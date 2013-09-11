package ee.midaiganes.util;

import javax.servlet.http.HttpServletRequest;

import ee.midaiganes.model.Layout;
import ee.midaiganes.services.util.LanguageUtil;

public class LayoutUtil {
	public static String getLayoutTitle(Layout layout, HttpServletRequest request) {
		if (layout == null) {
			throw new IllegalArgumentException("Layout is null");
		}
		Long languageId = LanguageUtil.getId(request);
		return languageId != null ? layout.getTitle(languageId.longValue()) : layout.getDefaultLayoutTitle().getTitle();
	}

}
