<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ taglib prefix="portal-taglib" uri="http://midaiganes.ee/portal-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE HTML>
<html>
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<link rel="stylesheet" href="${pageDisplay.theme.cssDir}/css.css" type="text/css" />
		<script type="text/javascript" charset="UTF-8" src="${pageDisplay.theme.javascriptDir}/jquery-1.8.0.min.js"></script>
		<script type="text/javascript" charset="UTF-8" src="${pageDisplay.theme.javascriptDir}/jquery-ui-1.8.22.custom.min.js"></script>
		<script type="text/javascript" charset="UTF-8" src="${pageDisplay.theme.javascriptDir}/javascript.js"></script>
	</head>
	<body>
		<div id="dockbar">
			<ul class="menu">
				<li>
					<a href="#">Actions</a>
					<ul>
						<li><a href="${addLayoutUrl}" class="open-dialog">Add page</a></li>
						<li><a href="${changePageLayoutUrl}" class="open-dialog">Change page layout</a></li>
						<li><a href="${addRemovePortletUrl}" class="open-modal" data-modal-title="Add or remove portlet">Add or remove portlet</a></li>
					</ul>
				</li>
			</ul>
		</div>
		<div id="page">
			<div id="header">
			</div>
			<div id="site-menu">
				<c:forEach items="${navItems}" var="navItem">
					<a href="${navItem.url}">${navItem.layout.defaultLayoutTitle.title}</a>
				</c:forEach>
			</div>
			<div id="content">
				<%-- portal-taglib:runtime-portlet name="snowportal_w_layout-set" / --%>
				<portal-taglib:page-layout />
			</div>
		</div>
	</body>
</html>