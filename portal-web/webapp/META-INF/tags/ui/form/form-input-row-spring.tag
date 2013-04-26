<%@ tag body-content="empty" dynamic-attributes="false" language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="message" required="true" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="isPassword" required="false" %>

<div>
	<form:label path="${path}">${message}</form:label>
	<c:choose>
		<c:when test="${isPassword eq 'true'}">
			<form:password path="${path}"/>
		</c:when>
		<c:otherwise>
			<form:input path="${path}" />
		</c:otherwise>
	</c:choose>
</div>