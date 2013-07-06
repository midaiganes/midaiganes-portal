<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL var="formAction" />

<form:form modelAttribute="registrationData" action="${formAction}">
	<portal-ui:form-content>
		<portal-ui:form-title title="Registration" />
		<c:if test="${not empty success and success}">
			<div>
				Registration complete!
			</div>
		</c:if>
		<portal-ui:form-input-row-spring message="Username" path="username"/>
		<portal-ui:form-input-row-spring message="Password" path="password" isPassword="true"/>
		<div>
			<button type="button" class="ajax-submit">Register</button>
		</div>
	</portal-ui:form-content>
</form:form>