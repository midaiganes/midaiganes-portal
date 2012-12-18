package ee.midaiganes.portletsservices.webcontent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ee.midaiganes.services.SingleVmPool;
import ee.midaiganes.services.SingleVmPool.Cache;
import ee.midaiganes.services.SingleVmPool.Cache.Element;
import ee.midaiganes.util.StringUtil;

@Repository
public class WebContentRepository {
	private static final String ADD_STRUCTURE = "INSERT INTO WebContentStructure (name) VALUES(?)";
	private static final String ADD_TEMPLATE = "INSERT INTO WebContentTemplate (name, structureId, templateContent) VALUES(?, ?, ?)";
	private static final String ADD_STRUCTURE_FIELD = "INSERT INTO WebContentStructureField(structureId, fieldName, fieldType) VALUES(?, ?, ?)";
	private static final String GET_WEB_CONTENT_FIELDS = "SELECT id, webContentId, languageId, structureId, structureFieldId, fieldValue FROM WebContentField WHERE webContentId = ?";
	private static final String GET_WEB_CONTENTS = "SELECT id, defaultLanguageId, title, templateId, structureId FROM WebContent";
	private static final String GET_TEMPLATES = "SELECT id, name, structureId, templateContent FROM WebContentTemplate";
	private static final String GET_STRUCTURES_WITH_FIELDS = "SELECT id, name FROM WebContentStructure";
	private static final String GET_STRUCTURE_FIELDS = "SELECT id, structureId, fieldName, fieldType FROM WebContentStructureField WHERE structureId = ?";

	private static final String GET_WCF_BY_WCID_CACHE_KEY_PREFIX = "getWebContentFields#";
	private static final String GET_WC_CACHE_KEY = "getWebContents";
	private static final String GET_TEMPLATES_CACHE_KEY = "getTemplates";
	private static final String GET_STRUCTURES_WITH_FIELDS_CACHE_KEY = "getStructuresWithFields";
	private static final String GET_STRUCTURE_FIELDS_BY_SID_CACHE_KEY = "getStructureFields";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private final Cache cache;

	public WebContentRepository() {
		cache = SingleVmPool.getCache(WebContentRepository.class.getName());
	}

	public boolean isValidStructureFieldType(String fieldType) {
		return "textarea".equals(fieldType);
	}

	private <A> List<A> getFromCacheOrGetFromDBAndCache(String cacheKey, String qry, RowMapper<A> rm, Object... queryParams) {
		Element el = cache.getElement(cacheKey);
		List<A> ret = el != null ? el.<List<A>> get() : null;
		if (ret == null) {
			ret = jdbcTemplate.query(qry, rm, queryParams);
			cache.put(cacheKey, ret);
		}
		return ret;
	}

	public List<WebContentField> getWebContentFields(long webContentId) {
		String cacheKey = GET_WCF_BY_WCID_CACHE_KEY_PREFIX + webContentId;
		return getFromCacheOrGetFromDBAndCache(cacheKey, GET_WEB_CONTENT_FIELDS, webContentFieldRowMapper, webContentId);
	}

	public List<WebContent> getWebContents() {
		return getFromCacheOrGetFromDBAndCache(GET_WC_CACHE_KEY, GET_WEB_CONTENTS, webContentRowMapper);
	}

	public WebContent getWebContent(long id) {
		for (WebContent wc : getWebContents()) {
			if (wc.getId() == id) {
				return wc;
			}
		}
		return null;
	}

	public List<Template> getTemplates() {
		return getFromCacheOrGetFromDBAndCache(GET_TEMPLATES_CACHE_KEY, GET_TEMPLATES, templateRowMapper);
	}

	public Template getTemplate(long templateId) {
		for (Template template : getTemplates()) {
			if (template.getId() == templateId) {
				return template;
			}
		}
		return null;
	}

	public List<Structure> getStructuresWithFields() {
		List<Structure> list = getFromCacheOrGetFromDBAndCache(GET_STRUCTURES_WITH_FIELDS_CACHE_KEY, GET_STRUCTURES_WITH_FIELDS, structureRowMapper);
		for (Structure structure : list) {
			structure.setStructureFields(getStructureFields(structure.getId()));
		}
		return list;
	}

	public Structure getStructureWithFields(long id) {
		for (Structure s : getStructuresWithFields()) {
			if (s.getId() == id) {
				return s;
			}
		}
		return null;
	}

	public List<StructureField> getStructureFields(long structureId) {
		return getFromCacheOrGetFromDBAndCache(GET_STRUCTURE_FIELDS_BY_SID_CACHE_KEY + structureId, GET_STRUCTURE_FIELDS, structureFieldRowMapper, structureId);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	public long addWebContent(final String defaultLanguageId, final String title, final long templateId, final List<String> languageIds,
			final List<Long> structureFieldIds, final List<String> fieldValues) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		try {
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con
							.prepareStatement(
									"INSERT INTO WebContent (defaultLanguageId, title, templateId, structureId) VALUES (?, ?, ?, (SELECT structureId FROM WebContentTemplate WHERE id = ?))",
									new String[] { "id" });
					ps.setString(1, defaultLanguageId);
					ps.setString(2, title);
					ps.setLong(3, templateId);
					ps.setLong(4, templateId);
					return ps;
				}
			}, keyHolder);

			long webContentId = keyHolder.getKey().longValue();
			String sql = "INSERT INTO WebContentField (webContentId, languageId, structureId, structureFieldId, fieldValue) VALUES ";
			sql += StringUtil.repeat("(" + webContentId + ", ?, (SELECT structureId FROM WebContentTemplate WHERE id = ?), ?, ?)", ",", languageIds.size());
			jdbcTemplate.update(sql, new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int j = 1;
					for (int i = 0; i < languageIds.size(); i++) {
						ps.setString(j++, languageIds.get(i));
						ps.setLong(j++, templateId);
						ps.setLong(j++, structureFieldIds.get(i));
						ps.setString(j++, fieldValues.get(i));
					}
				}
			});
			return webContentId;
		} finally {
			cache.clear();
		}
	}

	public long addStructure(final String name) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		try {
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(ADD_STRUCTURE, new String[] { "id" });
					ps.setString(1, name);
					return ps;
				}
			}, keyHolder);
			return keyHolder.getKey().longValue();
		} finally {
			cache.clear();
		}
	}

	public long addStructureField(final String name, final String fieldType, final long structureId) {
		if (!isValidStructureFieldType(fieldType)) {
			throw new InvalidStructureFieldTypeException(fieldType);
		}
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(ADD_STRUCTURE_FIELD, new String[] { "id" });
					ps.setLong(1, structureId);
					ps.setString(2, name);
					ps.setString(3, fieldType);
					return ps;
				}
			}, keyHolder);
			return keyHolder.getKey().longValue();
		} finally {
			cache.clear();
		}
	}

	public long addTemplate(final String name, final long structureId, final String templateContent) {
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(ADD_TEMPLATE, new String[] { "id" });
					ps.setString(1, name);
					ps.setLong(2, structureId);
					ps.setString(3, templateContent);
					return ps;
				}
			}, keyHolder);
			return keyHolder.getKey().longValue();
		} finally {
			cache.clear();
		}
	}

	private static RowMapper<Structure> structureRowMapper = new RowMapper<Structure>() {
		@Override
		public Structure mapRow(ResultSet rs, int rowNum) throws SQLException {
			Structure s = new Structure();
			s.setId(rs.getLong(1));
			s.setName(rs.getString(2));
			return s;
		}
	};
	private static RowMapper<StructureField> structureFieldRowMapper = new RowMapper<StructureField>() {
		@Override
		public StructureField mapRow(ResultSet rs, int rowNum) throws SQLException {
			StructureField sf = new StructureField();
			sf.setId(rs.getLong(1));
			sf.setStructureId(rs.getLong(2));
			sf.setFieldName(rs.getString(3));
			sf.setFieldType(rs.getString(4));
			return sf;
		}
	};
	private static RowMapper<Template> templateRowMapper = new RowMapper<Template>() {
		@Override
		public Template mapRow(ResultSet rs, int rowNum) throws SQLException {
			Template t = new Template();
			t.setId(rs.getLong(1));
			t.setName(rs.getString(2));
			t.setStructureId(rs.getLong(3));
			t.setTemplateContent(rs.getString(4));
			return t;
		}
	};
	private static final RowMapper<WebContent> webContentRowMapper = new RowMapper<WebContent>() {
		@Override
		public WebContent mapRow(ResultSet rs, int rowNum) throws SQLException {
			WebContent wc = new WebContent();
			wc.setId(rs.getLong(1));
			wc.setDefaultLanguageId(rs.getString(2));
			wc.setTitle(rs.getString(3));
			wc.setTemplateId(rs.getLong(4));
			wc.setStructureId(rs.getLong(5));
			return wc;
		}
	};
	private static RowMapper<WebContentField> webContentFieldRowMapper = new RowMapper<WebContentField>() {
		@Override
		public WebContentField mapRow(ResultSet rs, int rowNum) throws SQLException {
			WebContentField wcf = new WebContentField();
			wcf.setId(rs.getLong(1));
			wcf.setWebContentId(rs.getLong(2));
			wcf.setLanguageId(rs.getString(3));
			wcf.setStructureId(rs.getLong(4));
			wcf.setStructureFieldId(rs.getLong(5));
			wcf.setFieldValue(rs.getString(6));
			return wcf;
		}
	};
}
