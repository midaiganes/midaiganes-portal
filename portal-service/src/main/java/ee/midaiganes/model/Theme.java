package ee.midaiganes.model;

import java.io.Serializable;

import ee.midaiganes.util.StringPool;

public class Theme implements Serializable {
	private static final long serialVersionUID = 1L;
	private final ThemeName themeName;
	private final String themePath;
	private final String javascriptPath;
	private final String cssPath;
	private final String cssDir;
	private final String javascriptDir;

	public Theme(ThemeName themeName, String themePath, String javascriptPath, String cssPath) {
		this.themePath = themePath;
		this.javascriptPath = javascriptPath;
		this.cssPath = cssPath;
		this.themeName = themeName;
		this.cssDir = themeName.getContextWithSlash() + themePath + StringPool.SLASH + cssPath;
		this.javascriptDir = themeName.getContextWithSlash() + themePath + StringPool.SLASH + javascriptPath;
	}

	public ThemeName getThemeName() {
		return themeName;
	}

	public String getThemePath() {
		return themePath;
	}

	public String getJavascriptPath() {
		return javascriptPath;
	}

	public String getCssPath() {
		return cssPath;
	}

	public String getCssDir() {
		return cssDir;
	}

	public String getJavascriptDir() {
		return javascriptDir;
	}

	@Override
	public String toString() {
		return "Theme [themeName=" + themeName + ", themePath=" + themePath + ", javascriptPath=" + javascriptPath + ", cssPath=" + cssPath + "]";
	}
}
