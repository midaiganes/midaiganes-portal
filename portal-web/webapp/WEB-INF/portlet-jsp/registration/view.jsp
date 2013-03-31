<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL var="formAction" />

<form:form modelAttribute="registrationData" action="${formAction}">
	<div><form:input path="username"/></div>
	<div><form:password path="password"/></div>
	<div><input type="submit" value="Register" /></div>
</form:form>