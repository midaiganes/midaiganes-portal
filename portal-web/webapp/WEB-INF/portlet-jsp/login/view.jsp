<%@ page pageEncoding="UTF-8" language="java" session="false" trimDirectiveWhitespaces="true" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL var="loginUrl" />

<form:form method="post" action="${loginUrl}" modelAttribute="loginData">
	<div>
		<label for="username">Username:</label>
		<form:input path="username" id="username" />
	</div>
	<div>
		<label for="password">Password:</label>
		<form:password path="password" id="password" />
	</div>
	<div>
		<button type="button" class="ajax-submit">Login</button>
	</div>
</form:form>