<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ include file="../init.jsp" %>

<c:if test="${hasUserEditPermission}">
	<portlet:renderURL portletMode="edit" var="editUrl" />
	<a href="${editUrl}">go to edit</a>
</c:if>

<c:if test="${not empty webContent}">
	<h2>${webContent.title}</h2>
	<div>${webContent.htmlContent}</div>
</c:if>