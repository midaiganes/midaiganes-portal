CREATE TABLE IF NOT EXISTS User (
	id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
	username VARCHAR(100) NOT NULL UNIQUE,
	password VARCHAR(100) NOT NULL)
	ENGINE = InnoDB, CHARACTER SET = utf8, COLLATE = utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS Theme (
	id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	context VARCHAR(100) NOT NULL,
	CONSTRAINT uq_THEME_name_context 
		UNIQUE uq_THEME_name_context (name, context))
	ENGINE = InnoDB, CHARACTER SET = utf8, COLLATE = utf8_unicode_ci;
CREATE TABLE IF NOT EXISTS LayoutSet (
	id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
	virtualHost VARCHAR(100) NOT NULL UNIQUE,
	themeId INTEGER UNSIGNED,
	CONSTRAINT fk_LAYOUTSET_themeId_theme_id
		FOREIGN KEY fk_LAYOUTSET_themeId_theme_id (themeId)
			REFERENCES Theme(id)
				ON DELETE SET NULL
				ON UPDATE SET NULL)
	ENGINE = InnoDB, CHARACTER SET = utf8, COLLATE = utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS Layout (
	id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
	layoutSetId INTEGER UNSIGNED NOT NULL,
	friendlyUrl VARCHAR(100) NOT NULL,
	themeId INTEGER UNSIGNED,
	pageLayoutId VARCHAR(100),
	nr INTEGER UNSIGNED NOT NULL DEFAULT 1,
	parentId INTEGER UNSIGNED DEFAULT NULL,
	CONSTRAINT fk_LAYOUT_parentId_layout_id 
		FOREIGN KEY fk_LAYOUT_parentId_layout_id (parentId) 
			REFERENCES Layout (id) 
				ON DELETE CASCADE
				ON UPDATE CASCADE,
	CONSTRAINT fk_LAYOUT_layoutSetId_layoutSet_id 
		FOREIGN KEY fk_LAYOUT_layoutSetId_layoutSet_id (layoutSetId) 
			REFERENCES LayoutSet(id) 
				ON DELETE CASCADE 
				ON UPDATE CASCADE,
	CONSTRAINT fk_LAYOUT_themeId_theme_id
		FOREIGN KEY fk_LAYOUT_themeId_theme_id (themeId)
			REFERENCES Theme(id)
				ON DELETE SET NULL
				ON UPDATE SET NULL,
	CONSTRAINT uq_LAYOUT_parentId_nr 
		UNIQUE uq_LAYOUT_parentId_nr (parentId, nr),
	CONSTRAINT uq_LAYOUT_layoutSetId_friendlyUrl 
		UNIQUE uq_LAYOUT_layoutSetId_friendlyUrl (layoutSetId, friendlyUrl))
	ENGINE = InnoDB, CHARACTER SET = utf8, COLLATE = utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS PortletInstance (
	id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
	portletContext VARCHAR(100) NOT NULL,
	portletName VARCHAR(100) NOT NULL,
	windowID VARCHAR(10) NOT NULL,
	CONSTRAINT uq_PI_portletContext_portletName_windowID
		UNIQUE uq_PI_portletContext_portletName_windowID (windowID, portletName, portletContext))
	ENGINE = InnoDB, CHARACTER SET = utf8, COLLATE = utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS LayoutPortlet (
	id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
	layoutId INTEGER UNSIGNED NOT NULL,
	rowId INTEGER NOT NULL,
	portletInstanceId INTEGER UNSIGNED NOT NULL,
	CONSTRAINT uq_LP_layoutId_rowId
		UNIQUE uq_LP_layoutId_rowId (layoutId, rowId),
	CONSTRAINT fk_LP_layoutId_layout_id
		FOREIGN KEY fk_LP_layoutId_layout_id (layoutId) 
			REFERENCES Layout (id) 
				ON DELETE CASCADE 
				ON UPDATE CASCADE, 
	CONSTRAINT fk_LP_portletInstanceId_portletInstance_id
		FOREIGN KEY fk_LP_portletInstanceId_portletInstance_id (portletInstanceId) 
			REFERENCES PortletInstance (id) 
				ON DELETE CASCADE 
				ON UPDATE CASCADE) 
	ENGINE = InnoDB, CHARACTER SET = utf8, COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS PortletPreference (
	id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
	preferenceName VARCHAR(100) NOT NULL,
	portletInstanceId INTEGER UNSIGNED NOT NULL,
	CONSTRAINT fk_PP_portletInstanceId_portletInstance_id
		FOREIGN KEY fk_PP_portletInstanceId_portletInstance_id (portletInstanceId) 
			REFERENCES PortletInstance (id)
				ON DELETE CASCADE
				ON UPDATE CASCADE)
	ENGINE = InnoDB, CHARACTER SET = utf8, COLLATE = utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS PortletPreferenceValue (
	id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
	portletPreferenceId INTEGER UNSIGNED NOT NULL,
	preferenceValue VARCHAR(1024) NOT NULL,
	CONSTRAINT fk_PPV_portletPreferenceId_portletPreference_id
		FOREIGN KEY fk_PPV_portletPreferenceId_portletPreference_id (portletPreferenceId) 
			REFERENCES PortletPreference (id)
				ON DELETE CASCADE
				ON UPDATE CASCADE)
	ENGINE = InnoDB, CHARACTER SET = utf8, COLLATE = utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS WebContentStructure (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
	name VARCHAR(100) NOT NULL UNIQUE)
	ENGINE = InnoDB, CHARACTER SET = utf8, COLLATE = utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS WebContentStructureField (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
	webContentStructureId INT UNSIGNED NOT NULL,
	fieldName VARCHAR(100) NOT NULL ,
	fieldType INT UNSIGNED NOT NULL ,
	CONSTRAINT fk_wcsf_wcsid_wcs_id
    	FOREIGN KEY (webContentStructureId )
    		REFERENCES WebContentStructure (id )
    			ON DELETE CASCADE
    			ON UPDATE CASCADE)
	ENGINE = InnoDB, CHARACTER SET = utf8, COLLATE = utf8_unicode_ci;
	
CREATE  TABLE IF NOT EXISTS WebContentTemplate (
	`id` INT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY ,
	`name` VARCHAR(100) NOT NULL UNIQUE,
	`webContentStructureId` INT UNSIGNED NOT NULL ,
	`templateContent` LONGTEXT NOT NULL ,
	CONSTRAINT `fk_wct_wcsid_wcs_id`
    	FOREIGN KEY (`webContentStructureId` )
    		REFERENCES `WebContentStructure` (`id` )
    			ON DELETE CASCADE
    			ON UPDATE CASCADE)
    ENGINE = InnoDB, CHARACTER SET = utf8, COLLATE = utf8_general_ci;

CREATE  TABLE IF NOT EXISTS `WebContent` (
	`id` INT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
	`defaultLanguage` VARCHAR(5) NOT NULL ,
	`title` VARCHAR(100) NOT NULL UNIQUE,
	`webContentTemplateId` INT UNSIGNED NOT NULL ,
	`webContentStructureId` INT UNSIGNED NOT NULL COMMENT 'why?' ,
	CONSTRAINT `fk_wc_wctid_wct_id`
		FOREIGN KEY (`webContentTemplateId` , `webContentStructureId` )
			REFERENCES `WebContentTemplate` (`id` , `webContentStructureId` )
				ON DELETE RESTRICT
				ON UPDATE CASCADE,
	CONSTRAINT `fk_wc_wcsid_wcs_id`
		FOREIGN KEY (`webContentStructureId` )
			REFERENCES `WebContentStructure` (`id` )
				ON DELETE RESTRICT
				ON UPDATE CASCADE)
	ENGINE = InnoDB, CHARACTER SET = utf8, COLLATE = utf8_general_ci;

CREATE  TABLE IF NOT EXISTS `WebContentStructureField` (
	`id` INT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
	`webContentStructureId` INT UNSIGNED NOT NULL ,
	`fieldName` VARCHAR(100) NOT NULL ,
	`fieldType` INT UNSIGNED NOT NULL ,
	CONSTRAINT `fk_wcsf_wcsid_wcs_id`
		FOREIGN KEY (`webContentStructureId` )
			REFERENCES `WebContentStructure` (`id` )
				ON DELETE CASCADE
				ON UPDATE CASCADE)
	ENGINE = InnoDB, CHARACTER SET = utf8, COLLATE = utf8_unicode_ci;
