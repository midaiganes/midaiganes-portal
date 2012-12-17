package ee.midaiganes.tag;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;

public class LayoutPortletTagTEI extends TagExtraInfo {

	@Override
	public boolean isValid(TagData data) {
		long id = Long.parseLong(data.getAttribute("id").toString());
		return id >= 0;
	}
}
