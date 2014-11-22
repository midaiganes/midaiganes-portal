<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL var="addWebContentUrl">
	<portlet:param name="action" value="add-web-content"/>
</portlet:actionURL>

<form action="${addWebContentUrl}" method="post">
	<portal-ui:form-content>
		<portal-ui:form-input-row message="Title" path="title" />
		<div>
			<textarea rows="10" cols="80" name="content"></textarea>
		</div>
		<portal-ui:form-buttons>
			<button type="submit">Save</button>
		</portal-ui:form-buttons>
	</portal-ui:form-content>
</form>