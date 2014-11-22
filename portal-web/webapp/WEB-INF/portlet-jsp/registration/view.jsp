<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL var="formAction" windowState="exclusive" />

<form action="${formAction}" method="post">
	<portal-ui:form-content>
		<portal-ui:form-title title="Registration" />
		<c:if test="${not empty success and success}">
			<portal-ui:msg msg="Registration complete!"/>
		</c:if>
		<portal-ui:form-input-row message="Username" path="username"/>
		<portal-ui:form-input-row message="Password" path="password" isPassword="true"/>
		<portal-ui:form-buttons>
			<button type="button" class="ajax-submit">Register</button>
		</portal-ui:form-buttons>
	</portal-ui:form-content>
</form>