<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL var="editWebContentUrl" escapeXml="false">
	<portlet:param name="action" value="edit-web-content"/>
	<portlet:param name="id" value="${webContent.id}"/>
</portlet:actionURL>

<form action="${editWebContentUrl}" method="POST" accept-charset="UTF-8">
	<portal-ui:form-content>
		<portal-ui:form-input-row message="Title" path="title" value="${webContent.title}" />
		<div>
			<textarea rows="10" cols="80" name="content">${webContent.content}<%-- TODO --%></textarea>
		</div>
		<portal-ui:form-buttons>
			<button type="submit">Save</button>
		</portal-ui:form-buttons>
	</portal-ui:form-content>
</form>