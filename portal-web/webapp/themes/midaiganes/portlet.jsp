<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ taglib prefix="portal-taglib" uri="http://midaiganes.ee/portal-tags" %>
<%@ taglib prefix="portalservice" uri="http://midaiganes.ee/portal-service-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>

<c:set var="hasRemovePortletPermission" value="${!portletNamespace.defaultWindowID and portalservice:hasUserPermission(pageDisplay.user.id, pageDisplay.layout.resource, pageDisplay.layout.id, 'EDIT')}"/>
<c:set var="hasChangePermissionsPermission" value="${portalservice:hasUserPermission(pageDisplay.user.id, portletInstance.resource, portletInstance.id, 'PERMISSIONS')}"/>
<c:choose>
	<c:when test="${hasRemovePortletPermission or hasChangePermissionsPermission}">
		<div class="portlet">
			<div class="portlet-top">
				<c:if test="${hasRemovePortletPermission}">
					<portal-taglib:portlet-action-url portletName="midaiganes_w_add-remove-portlet" var="removePortletUrl">
						<portlet:param name="action" value="remove-portlet"/>
						<portlet:param name="window-id" value="${portletNamespace.windowID}" />
					</portal-taglib:portlet-action-url>
					<a href="${removePortletUrl}" class="remove-portlet">X</a>
				</c:if>
				<c:if test="${hasChangePermissionsPermission}">
					<portal-taglib:portlet-render-url portletName="midaiganes_w_permissions" windowState="exclusive" portletMode="view" var="permissionPortletUrl">
						<portlet:param name="resource" value="${portletInstance.resource}"/>
						<portlet:param name="resource-prim-key" value="${portletInstance.id}"/>
					</portal-taglib:portlet-render-url>
					<a href="${permissionPortletUrl}" class="open-dialog">Permissions</a>
				</c:if>
			</div>
			<div class="portlet-body">
				${portletContent}
			</div>
		</div>
	</c:when>
	<c:otherwise>
		${portletContent}
	</c:otherwise>
</c:choose>