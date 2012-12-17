package ee.midaiganes.model;

import java.io.Serializable;

public class Theme implements Serializable {
	private static final long serialVersionUID = 1L;
	private ThemeName themeName;
	private String themePath;
	private String javascriptPath;
	private String cssPath;

	public Theme() {
	}

	public Theme(ThemeName themeName, String themePath, String javascriptPath, String cssPath) {
		setThemePath(themePath);
		setJavascriptPath(javascriptPath);
		setCssPath(cssPath);
		setThemeName(themeName);
	}

	public ThemeName getThemeName() {
		return themeName;
	}

	public void setThemeName(ThemeName themeName) {
		this.themeName = themeName;
	}

	public String getThemePath() {
		return themePath;
	}

	public void setThemePath(String themePath) {
		this.themePath = themePath;
	}

	public String getJavascriptPath() {
		return javascriptPath;
	}

	public void setJavascriptPath(String javascriptPath) {
		this.javascriptPath = javascriptPath;
	}

	public String getCssPath() {
		return cssPath;
	}

	public void setCssPath(String cssPath) {
		this.cssPath = cssPath;
	}

	@Override
	public String toString() {
		return "Theme [themeName=" + themeName + ", themePath=" + themePath + ", javascriptPath=" + javascriptPath + ", cssPath=" + cssPath + "]";
	}
}
