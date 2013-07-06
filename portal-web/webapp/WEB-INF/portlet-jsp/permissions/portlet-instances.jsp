<%@ page pageEncoding="UTF-8" language="java" session="false" trimDirectiveWhitespaces="true" %>
<%@ include file="../init.jsp" %>

<ul id="default-portlets-permissions">
	<c:forEach items="${portletInstances}" var="item">
		<portlet:renderURL var="choosePortletUrl" windowState="exclusive">
			<portlet:param name="resource-id" value="${resourceId}"/>
			<portlet:param name="resource-prim-key" value="${item.id}"/>
		</portlet:renderURL>
		<li><a href="${choosePortletUrl}" class="ajax-replace" data-replace-el="#default-portlets-permissions">${item.portletNamespace.portletName}</a></li>
	</c:forEach>
</ul>