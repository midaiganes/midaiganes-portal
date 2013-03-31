<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL var="addWebContentUrl">
	<portlet:param name="action" value="add-web-content"/>
</portlet:actionURL>

<form action="${addWebContentUrl}" method="post">
	<div>
		<input type="text" name="title" />
	</div>
	<div>
		<textarea rows="10" cols="80" name="content"></textarea>
	</div>
	<div>
		<input type="submit" value="Save" />
	</div>
</form>