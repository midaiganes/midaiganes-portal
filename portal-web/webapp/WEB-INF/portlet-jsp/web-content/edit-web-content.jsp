<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL var="editWebContentUrl" escapeXml="false">
	<portlet:param name="action" value="edit-web-content"/>
	<portlet:param name="id" value="${webContent.id}"/>
	
</portlet:actionURL>

<form action="${editWebContentUrl}" method="POST" accept-charset="UTF-8">
	<div>
		<portal-ui:form-input name="title" value="${webContent.title}"/>
	</div>
	<div>
		<textarea rows="10" cols="80" name="content">${webContent.content}<%-- TODO --%></textarea>
	</div>
	<div>
		<input type="submit" value="Save" />
	</div>
</form>