<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ taglib prefix="portal-taglib" uri="http://midaiganes.ee/portal-tags" %>
<%@ taglib prefix="portalservice" uri="http://midaiganes.ee/portal-service-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>

<c:set var="addPagePermission" value="${portalservice:hasUserPermission(pageDisplay.user.id, pageDisplay.layoutSet.resource, pageDisplay.layoutSet.id, 'EDIT')}" />
<c:set var="changePageLayoutPermission" value="${portalservice:hasUserPermission(pageDisplay.user.id, pageDisplay.layout.resource, pageDisplay.layout.id, 'EDIT')}"/>
<c:set var="addRemovePortletPermission" value="${portalservice:hasUserPermission(pageDisplay.user.id, pageDisplay.layout.resource, pageDisplay.layout.id, 'ADD_PORTLET')}"/>
<c:set var="changePagePermissionsPermission" value="${portalservice:hasUserPermission(pageDisplay.user.id, pageDisplay.layout.resource, pageDisplay.layout.id, 'PERMISSIONS')}"/>

<!DOCTYPE HTML>
<html>
	<head>
		<title>MIDAIGANES</title>
		<meta charset="utf-8">
		<link rel="stylesheet" href="${pageDisplay.theme.cssDir}/css.css" type="text/css" />
		<c:if test="${addRemovePortletPermission}">
			<portal-taglib:portlet-action-url var="movePortletUrl" portletName="midaiganes_w_add-remove-portlet" windowState="exclusive" portletMode="view">
				<portlet:param name="action" value="move"/>
				<portlet:param name="window-id" value="WINDOW_ID" />
				<portlet:param name="portletBoxId" value="PORTLET_BOX_ID" />
				<portlet:param name="boxIndex" value="BOX_INDEX"/>
			</portal-taglib:portlet-action-url>
			<script type="text/javascript">
				window.Midaiganes = {
					Admin : {
						movePortletUrl : '${movePortletUrl}'
					}
				};
			</script>
		</c:if>
	</head>
	<body>
		<c:if test="${addPagePermission or changePageLayoutPermission or addRemovePortletPermission or changePagePermissionsPermission}">
			<div id="dockbar">
				<ul>
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
		<div class="content">
			<header>
				<c:if test="${!pageDisplay.user.defaultUser}">
					<portal-taglib:portlet-action-url portletName="midaiganes_w_login" var="logoutUrl">
						<portlet:param name="action" value="logout"/>
					</portal-taglib:portlet-action-url>
					<a href="${logoutUrl}" style="float:right;color: white;margin: 13px; text-decoration: none;">log out</a>
				</c:if>
				<h1>MIDAIGANES</h1>
			</header>
			
			<%--
			
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
			--%>
			<div class="navigation">
				<ul>
					<c:forEach items="${navItems}" var="navItem">
						<li>
							<c:choose>
								<c:when test="${navItem.layout.id == pageDisplay.layout.id}">
									<a href="${navItem.url}" class="active">${portalservice:getLayoutTitle(navItem.layout, pageContext.request)}</a>
								</c:when>
								<c:otherwise>
									<a href="${navItem.url}">${portalservice:getLayoutTitle(navItem.layout, pageContext.request)}</a>
								</c:otherwise>
							</c:choose>
						</li>
					</c:forEach>
				</ul>
			</div>
			
			<%-- portal-taglib:runtime-portlet name="midaiganes_w_layout-set" / --%>
			<%--
			<c:choose>
				<c:when test="${pageDisplay.user.defaultUser}">
					<div class="row">
						<div class="box6">
						</div>
						<div class="box6">
							<portal-taglib:runtime-portlet name="midaiganes_w_login" />
						</div>
					</div>
				</c:when>
				<c:otherwise>
					<portal-taglib:page-layout />
				</c:otherwise>
			</c:choose>
			--%>
			<portal-taglib:page-layout />
		</div>
		<footer>
			<div class="content">
				&copy; midaiganes
			</div>
		</footer>
		<script type="text/javascript" charset="UTF-8" src="${pageDisplay.theme.javascriptDir}/jquery-2.0.3.min.js"></script>
		<script type="text/javascript" charset="UTF-8" src="${pageDisplay.theme.javascriptDir}/jquery-ui-1.10.3.custom.min.js"></script>
		<script type="text/javascript" charset="UTF-8" src="${pageDisplay.theme.javascriptDir}/javascript.js"></script>
		<script type="text/javascript" charset="UTF-8" src="${pageDisplay.theme.javascriptDir}/portlets.js"></script>
	</body>
</html>