<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="false" session="false" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL portletMode="edit" var="addWebContentUrl">
	<portlet:param name="action" value="add-web-content"/>
	<portlet:param name="templateId" value="${templateId}"/>
</portlet:actionURL>

<form action="${addWebContentUrl}" method="post">
	<portal-ui:form-content>
		<portal-ui:form-title title="Add new web content" />
		<portal-ui:form-input-row message="Title" path="title" />
		<c:forEach items="${structure.structureFields}" var="field">
			<portal-ui:form-textarea-row path="field_${field.id}" message="${field.fieldName}" />
		</c:forEach>
		<div>
			<input type="submit" value="Publish"/>
		</div>
	</portal-ui:form-content>
</form>
