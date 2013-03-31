<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL var="editWebContentUrl">
	<portlet:param name="action" value="edit-web-content"/>
	<portlet:param name="id" value="${webContent.id}"/>
	
</portlet:actionURL>

<form:form action="${editWebContentUrl}" modelAttribute="webContent">
	<div>
		<form:input path="title"/>
	</div>
	<div>
		<form:textarea path="content" rows="10" cols="80"/>
	</div>
	<div>
		<input type="submit" value="Save" />
	</div>
</form:form>