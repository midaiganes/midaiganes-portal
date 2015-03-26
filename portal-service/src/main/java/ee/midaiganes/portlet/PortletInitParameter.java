package ee.midaiganes.portlet;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.ImmutableList;

public class PortletInitParameter implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String name;
    private final String value;
    private final ImmutableList<Description> description;

    public PortletInitParameter(String id, String name, String value, ImmutableList<Description> description) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public List<Description> getDescription() {
        return description;
    }

    public static class Description implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String lang;
        private final String value;

        public Description(String lang, String value) {
            this.lang = lang;
            this.value = value;
        }

        public String getLang() {
            return lang;
        }

        public String getValue() {
            return value;
        }
    }
}
