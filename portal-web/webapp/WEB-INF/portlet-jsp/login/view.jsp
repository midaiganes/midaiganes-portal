<%@ page pageEncoding="UTF-8" language="java" session="false" trimDirectiveWhitespaces="true" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL var="loginUrl" windowState="normal" />

<form:form method="post" action="${loginUrl}" modelAttribute="loginData">
	<portal-ui:form-content>
		<portal-ui:form-input-row-spring message="Username:" path="username"/>
		<portal-ui:form-input-row-spring message="Password:" path="password"/>
		<div>
			<button type="submit">Login</button>
		</div>
	</portal-ui:form-content>
</form:form>