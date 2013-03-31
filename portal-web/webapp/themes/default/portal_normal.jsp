<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ taglib prefix="portal-taglib" uri="http://midaiganes.ee/portal-tags" %>
<%@ taglib prefix="portalservice" uri="http://midaiganes.ee/portal-service-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>

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
		<c:set var="addPagePermission" value="${portalservice:hasUserPermission(pageDisplay.user.id, pageDisplay.layoutSet.resource, pageDisplay.layoutSet.id, 'EDIT')}" />
		<c:set var="changePageLayoutPermission" value="${portalservice:hasUserPermission(pageDisplay.user.id, pageDisplay.layout.resource, pageDisplay.layout.id, 'EDIT')}"/>
		<c:set var="addRemovePortletPermission" value="${portalservice:hasUserPermission(pageDisplay.user.id, pageDisplay.layout.resource, pageDisplay.layout.id, 'ADD_PORTLET')}"/>
		<c:set var="changePagePermissionsPermission" value="${portalservice:hasUserPermission(pageDisplay.user.id, pageDisplay.layout.resource, pageDisplay.layout.id, 'PERMISSIONS')}"/>
		<c:if test="${addPagePermission or changePageLayoutPermission or addRemovePortletPermission or changePagePermissionsPermission}">
			<div id="dockbar">
				<ul class="menu">
					<li>
						<a href="#">Actions</a>
						<ul>
							<c:if test="${addPagePermission}">
								<li><a href="${addLayoutUrl}" class="open-dialog">Add page</a></li>
							</c:if>
							<c:if test="${changePageLayoutPermission}">
								<li><a href="${changePageLayoutUrl}" class="open-dialog">Change page layout</a></li>
							</c:if>
							<c:if test="${addRemovePortletPermission}">
								<li><a href="${addRemovePortletUrl}" class="open-modal" data-modal-title="Add or remove portlet">Add or remove portlet</a></li>
							</c:if>
							<c:if test="${changePagePermissionsPermission}">
								<portal-taglib:portlet-render-url portletName="midaiganes_w_permissions" windowState="exclusive" portletMode="view" var="permissionPortletUrl">
									<portlet:param name="resource" value="${pageDisplay.layout.resource}"/>
									<portlet:param name="resource-prim-key" value="${pageDisplay.layout.id}"/>
								</portal-taglib:portlet-render-url>
								<li><a href="${permissionPortletUrl}" class="open-dialog">Permissions</a></li>
							</c:if>
						</ul>
					</li>
				</ul>
			</div>
		</c:if>
		<div id="page">
			<div id="header">
				<c:choose>
					<c:when test="${pageDisplay.user.defaultUser}">
						<portal-taglib:portlet-render-url portletName="midaiganes_w_login" var="loginUrl" windowState="exclusive" />
						<a href="${loginUrl}" class="open-dialog">Log in</a>
					</c:when>
					<c:otherwise>
						Username: ${pageDisplay.user.username}
					</c:otherwise>
				</c:choose>
			</div>
			<div id="site-menu">
				<c:forEach items="${navItems}" var="navItem">
					<a href="${navItem.url}">${portalservice:getLayoutTitle(navItem.layout, pageContext.request)}</a>
				</c:forEach>
			</div>
			<div id="content">
				<%-- portal-taglib:runtime-portlet name="midaiganes_w_layout-set" / --%>
				<portal-taglib:page-layout />
			</div>
		</div>
	</body>
</html>