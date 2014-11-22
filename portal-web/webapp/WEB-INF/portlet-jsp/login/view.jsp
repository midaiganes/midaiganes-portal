<%@ page pageEncoding="UTF-8" language="java" session="false" trimDirectiveWhitespaces="true" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL var="loginUrl" windowState="normal" />

<c:if test="${not empty loginfailed and loginfailed}">
	<portal-ui:msg-error msg="Invalid username or password." />
</c:if>
<form method="post" action="${loginUrl}">
	<portal-ui:form-content>
		<portal-ui:form-title title="Login" />
		<portal-ui:form-input-row message="Username:" path="username"/>
		<portal-ui:form-input-row message="Password:" path="password"/>
		<portal-ui:form-buttons>
			<button type="submit">Login</button>
		</portal-ui:form-buttons>
	</portal-ui:form-content>
</form>