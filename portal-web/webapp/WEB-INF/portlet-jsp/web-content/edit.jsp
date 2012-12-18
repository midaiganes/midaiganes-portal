<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="false" session="false" %>
<%@ include file="../init.jsp" %>

<portlet:renderURL portletMode="view" var="viewUrl" />
<portlet:renderURL portletMode="edit" var="addWebContentUrl">
	<portlet:param name="action" value="add-web-content"/>
</portlet:renderURL>
<portlet:renderURL portletMode="edit" var="viewStructures">
	<portlet:param name="action" value="view-structures"/>
</portlet:renderURL>
<portlet:renderURL portletMode="edit" var="addStructureView">
	<portlet:param name="action" value="add-structure"/>
</portlet:renderURL>
<portlet:renderURL portletMode="edit" var="addTemplateView">
	<portlet:param name="action" value="add-template"/>
</portlet:renderURL>
<portlet:renderURL portletMode="edit" var="viewWebContentsListView">
	<portlet:param name="action" value="web-contents-list"/>
</portlet:renderURL>
web content edit <a href="${viewUrl}">go to view</a><br />
<a href="${viewStructures}">view structures</a><br />
<a href="${addStructureView}">add structure</a><br />
<a href="${addTemplateView}">add template</a><br />
<a href="${addWebContentUrl}">add web content</a><br />
<a href="${viewWebContentsListView}">view web contents list</a><br />