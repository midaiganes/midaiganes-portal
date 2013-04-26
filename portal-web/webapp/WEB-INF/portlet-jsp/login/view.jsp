<%@ page pageEncoding="UTF-8" language="java" session="false" trimDirectiveWhitespaces="true" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL var="loginUrl" />

<form:form method="post" action="${loginUrl}" modelAttribute="loginData" cssClass="form">
	<portal-ui:form-input-row-spring message="Username:" path="username"/>
	<portal-ui:form-input-row-spring message="Password:" path="password"/>
	<div>
		<button type="button" class="ajax-submit">Login</button>
	</div>
</form:form>