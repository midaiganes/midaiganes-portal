package ee.midaiganes.portal.layout;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import ee.midaiganes.portal.pagelayout.PageLayoutName;
import ee.midaiganes.portal.theme.ThemeName;

public class LayoutDao {
	private final JdbcTemplate jdbcTemplate;
	private static final LayoutRowMapper layoutRowMapper = new LayoutRowMapper();
	private static final LayoutTitleRowMapper layoutTitleRowMapper = new LayoutTitleRowMapper();
	private static final LayoutResultSetExtractor layoutResultSetExtractor = new LayoutResultSetExtractor();
	private static final String QRY_DELETE_LAYOUT = "DELETE FROM Layout WHERE id = ?";
	private static final String QRY_UPDATE_PAGE_LAYOUT = "UPDATE Layout SET pageLayoutId = ? WHERE id = ?";
	private static final String QRY_GET_LAYOUT_TITLES = "SELECT LayoutTitle.id, LayoutTitle.languageId, LayoutTitle.layoutId, LayoutTitle.title FROM LayoutTitle WHERE LayoutTitle.layoutId = ?";
	private static final String QRY_GET_LAYOUT_BY_ID = "SELECT Layout.id, Layout.layoutSetId, Layout.friendlyUrl, Layout.pageLayoutId, Theme.name, Theme.context, Layout.parentId, Layout.nr, Layout.defaultLayoutTitleLanguageId FROM Layout LEFT JOIN Theme ON (Layout.themeId = Theme.id) WHERE Layout.id = ?";
	private static final String QRY_GET_LAYOUT_BY_LAYOUTSETID = "SELECT Layout.id, Layout.layoutSetId, Layout.friendlyUrl, Layout.pageLayoutId, Theme.name, Theme.context, Layout.parentId, Layout.nr, Layout.defaultLayoutTitleLanguageId FROM Layout LEFT JOIN Theme ON (Layout.themeId = Theme.id) WHERE layoutSetId = ?";
	private static final String QRY_UPDATE_LAYOUT = "UPDATE Layout SET friendlyUrl = ?, pageLayoutId = ?, parentId = ?, defaultLayoutTitleLanguageId = ? WHERE id = ?";
	private static final String QRY_MOVE_LAYOUT_UP = "UPDATE Layout AS l1 JOIN Layout AS l2 ON (l1.id = ? AND (l2.parentId = l1.parentId or (l2.parentId is null and l1.parentId is null)) and l2.nr = l1.nr - 1) SET l1.nr = l2.nr, l2.nr = l1.nr";
	private static final String QRY_MOVE_LAYOUT_DOWN = "UPDATE Layout AS l1 JOIN Layout AS l2 ON (l1.id = ? AND (l2.parentId = l1.parentId or (l2.parentId is null and l1.parentId is null)) and l2.nr = l1.nr + 1) SET l1.nr = l2.nr, l2.nr = l1.nr";

	public LayoutDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<LayoutTitle> loadLayoutTitles(long layoutId) {
		return jdbcTemplate.query(QRY_GET_LAYOUT_TITLES, layoutTitleRowMapper, Long.valueOf(layoutId));
	}

	public Layout loadLayout(long layoutId) {
		return jdbcTemplate.query(QRY_GET_LAYOUT_BY_ID, layoutResultSetExtractor, Long.valueOf(layoutId));
	}

	public List<Layout> loadLayouts(long layoutSetId) {
		return jdbcTemplate.query(QRY_GET_LAYOUT_BY_LAYOUTSETID, layoutRowMapper, Long.valueOf(layoutSetId));
	}

	public void updateLayout(String friendlyUrl, PageLayoutName pageLayoutName, Long parentId, long defaultLayoutTitleLanguageId, long id) {
		jdbcTemplate.update(QRY_UPDATE_LAYOUT, friendlyUrl, pageLayoutName.getFullName(), parentId, Long.valueOf(defaultLayoutTitleLanguageId),
				Long.valueOf(id));
	}

	public void addLayoutTitle(long layoutId, long languageId, String title) {
		jdbcTemplate.update("INSERT INTO LayoutTitle(layoutId, languageId, title) VALUES(?, ?, ?)", Long.valueOf(layoutId), Long.valueOf(languageId), title);
	}

	public void updateLayoutTitle(long layoutId, long languageId, String title) {
		jdbcTemplate.update("UPDATE LayoutTitle SET languageId = ?, title = ? WHERE layoutId = ?", Long.valueOf(languageId), title, Long.valueOf(layoutId));
	}

	public void deleteLayoutTitle(long layoutId, long languageId) {
		jdbcTemplate.update("DELETE LayoutTitle WHERE languageId = ? AND layoutId = ?", Long.valueOf(languageId), Long.valueOf(layoutId));
	}

	public void updatePageLayout(long layoutId, PageLayoutName pageLayoutName) {
		jdbcTemplate.update(QRY_UPDATE_PAGE_LAYOUT, pageLayoutName.getFullName(), Long.valueOf(layoutId));
	}

	public int moveLayoutsUp(long layoutSetId, Long parentId, long nr) {
		if (parentId == null) {
			return jdbcTemplate.update("UPDATE Layout set nr = nr - 1 WHERE layoutSetId = ? AND parentId IS NULL AND nr > ?", Long.valueOf(layoutSetId),
					Long.valueOf(nr));
		}
		return jdbcTemplate.update("UPDATE Layout set nr = nr - 1 WHERE layoutSetId = ? AND parentId = ? AND nr > ?", Long.valueOf(layoutSetId), parentId,
				Long.valueOf(nr));
	}

	public int deleteLayout(long layoutId) {
		return jdbcTemplate.update(QRY_DELETE_LAYOUT, Long.valueOf(layoutId));
	}

	public int moveLayoutUp(long layoutId) {
		return jdbcTemplate.update(QRY_MOVE_LAYOUT_UP, Long.valueOf(layoutId));
	}

	public int moveLayoutDown(long layoutId) {
		return jdbcTemplate.update(QRY_MOVE_LAYOUT_DOWN, Long.valueOf(layoutId));
	}

	public long addLayout(long layoutSetId, String friendlyUrl, ThemeName themeName, PageLayoutName pageLayoutName, Long parentId,
			long defaultLayoutTitleLanguageId) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new AddLayoutPreparedStatementCreator(layoutSetId, friendlyUrl, themeName, pageLayoutName, parentId, defaultLayoutTitleLanguageId),
				keyHolder);
		return keyHolder.getKey().longValue();
	}
}
