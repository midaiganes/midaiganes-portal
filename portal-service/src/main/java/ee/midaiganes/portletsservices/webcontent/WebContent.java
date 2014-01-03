package ee.midaiganes.portletsservices.webcontent;

import java.io.Serializable;

import org.joda.time.DateTime;

import ee.midaiganes.util.StringPool;

public class WebContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String BR = "<br />";
    private static final String TWO_SPACE = "  ";
    private static final String TWO_NBSP = "&nbsp;&nbsp;";

    private final long id;
    private final long layoutSetId;
    private final String title;
    private final String content;
    private final DateTime createDate;

    public WebContent(long id, long layoutSetId, String title, String content, DateTime createDate) {
        this.id = id;
        this.layoutSetId = layoutSetId;
        this.title = title;
        this.content = content;
        this.createDate = createDate;
    }

    public long getId() {
        return id;
    }

    public long getLayoutSetId() {
        return layoutSetId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public DateTime getCreateDate() {
        return createDate;
    }

    public String getHtmlContent() {
        return content.replace(StringPool.LINE_ENDING_RN, BR).replace(StringPool.LINE_ENDING_N, BR).replace(StringPool.LINE_ENDING_N, BR).replace(TWO_SPACE, TWO_NBSP);
    }
}
