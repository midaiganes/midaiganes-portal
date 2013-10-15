package ee.midaiganes.services.statementcreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SqlProvider;

import ee.midaiganes.model.PageLayoutName;
import ee.midaiganes.model.ThemeName;
import ee.midaiganes.util.StringPool;

public class AddLayoutPreparedStatementCreator implements PreparedStatementCreator, SqlProvider {
	private static final String QRY_ADD_LAYOUT = "INSERT INTO Layout(layoutSetId, friendlyUrl, themeId, pageLayoutId, parentId, nr, defaultLayoutTitleLanguageId) VALUES(?, ?, (SELECT id FROM Theme WHERE name = ? AND context = ?), ?, ?, (SELECT c FROM (SELECT COUNT(1) AS c FROM Layout WHERE layoutSetId = ? AND (parentId = ? OR ? IS NULL)) AS t), ?)";
	private static final String[] ID_ARRAY = { StringPool.ID };

	private final long layoutSetId;
	private final String friendlyUrl;
	private final ThemeName themeName;
	private final PageLayoutName pageLayoutName;
	private final Long parentId;
	private final long defaultLayoutTitleLanguageId;

	public AddLayoutPreparedStatementCreator(long layoutSetId, String friendlyUrl, ThemeName themeName, PageLayoutName pageLayoutName, Long parentId,
			long defaultLayoutTitleLanguageId) {
		this.layoutSetId = layoutSetId;
		this.friendlyUrl = friendlyUrl;
		this.themeName = themeName;
		this.pageLayoutName = pageLayoutName;
		this.parentId = parentId;
		this.defaultLayoutTitleLanguageId = defaultLayoutTitleLanguageId;
	}

	@Override
	public String getSql() {
		return QRY_ADD_LAYOUT;
	}

	@Override
	public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
		PreparedStatement ps = con.prepareStatement(QRY_ADD_LAYOUT, ID_ARRAY);
		ps.setLong(1, layoutSetId);
		ps.setString(2, friendlyUrl);
		if (themeName != null) {
			ps.setString(3, themeName.getName());
			ps.setString(4, themeName.getContext());
		} else {
			ps.setNull(3, Types.VARCHAR);
			ps.setNull(4, Types.VARCHAR);
		}
		ps.setString(5, pageLayoutName.getFullName());
		if (parentId != null) {
			ps.setLong(6, parentId);
			ps.setLong(8, parentId);
			ps.setLong(9, parentId);
		} else {
			ps.setNull(6, Types.INTEGER);
			ps.setNull(8, Types.INTEGER);
			ps.setNull(9, Types.INTEGER);
		}
		ps.setLong(7, layoutSetId);
		ps.setLong(10, defaultLayoutTitleLanguageId);
		return ps;
	}
}
