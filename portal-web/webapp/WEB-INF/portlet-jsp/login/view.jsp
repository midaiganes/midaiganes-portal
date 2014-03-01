<%@ page pageEncoding="UTF-8" language="java" session="false" trimDirectiveWhitespaces="true" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL var="loginUrl" windowState="normal" />

<c:if test="${not empty loginfailed and loginfailed}">
	<p class="message error">
		Invalid username or password.
	</p>
</c:if>
<form method="post" action="${loginUrl}">
	<portal-ui:form-content>
		<portal-ui:form-input-row message="Username:" path="username"/>
		<portal-ui:form-input-row message="Password:" path="password"/>
		<div>
			<button type="submit">Login</button>
		</div>
	</portal-ui:form-content>
</form>