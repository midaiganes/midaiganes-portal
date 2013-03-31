<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ include file="../init.jsp" %>

<portlet:renderURL var="addWebContentUrl">
	<portlet:param name="action" value="add-web-content"/>
</portlet:renderURL>

<div>
	<a href="${addWebContentUrl}">add web content</a>
</div>

<%@ include file="web-content-list.jsp"%>