<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ taglib prefix="portal-taglib" uri="http://midaiganes.ee/portal-tags" %>
<%@ taglib prefix="portalservice" uri="http://midaiganes.ee/portal-service-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>

<!DOCTYPE HTML>
<html>
	<head>
		<title></title>
		<meta charset="utf-8">
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
						<a href="#">Actions &#9660;</a>
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
			<header id="header">
				<c:choose>
					<c:when test="${pageDisplay.user.defaultUser}">
						<portal-taglib:portlet-render-url portletName="midaiganes_w_login" var="loginUrl" windowState="exclusive" />
						<a href="${loginUrl}" class="open-dialog">Log in</a>
						or
						<portal-taglib:portlet-render-url portletName="midaiganes_w_registration" var="registrationUrl" windowState="exclusive"/>
						<a href="${registrationUrl}" class="open-dialog">Create an account</a>
					</c:when>
					<c:otherwise>
						<span>Username: ${pageDisplay.user.username}</span>
						<portal-taglib:portlet-action-url portletName="midaiganes_w_login" var="logoutUrl">
							<portlet:param name="action" value="logout"/>
						</portal-taglib:portlet-action-url>
						<a href="${logoutUrl}">log out</a>
					</c:otherwise>
				</c:choose>
			</header>
			<nav id="site-menu">
				<ul>
					<c:forEach items="${navItems}" var="navItem">
						<li>
							<c:choose>
								<c:when test="${navItem.layout.id == pageDisplay.layout.id}">
									<a href="${navItem.url}" class="active">${navItem.layoutTitle}</a>
								</c:when>
								<c:otherwise>
									<a href="${navItem.url}">${navItem.layoutTitle}</a>
								</c:otherwise>
							</c:choose>
						</li>
					</c:forEach>
				</ul>
			</nav>
			<div id="content">
				<%-- portal-taglib:runtime-portlet name="midaiganes_w_layout-set" / --%>
				<portal-taglib:page-layout />
			</div>
		</div>
	</body>
</html>